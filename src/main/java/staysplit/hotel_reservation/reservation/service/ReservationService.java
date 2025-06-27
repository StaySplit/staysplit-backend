package staysplit.hotel_reservation.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.customer.repository.CustomerRepository;
import staysplit.hotel_reservation.hotel.entity.HotelEntity;
import staysplit.hotel_reservation.hotel.repository.HotelRepository;
import staysplit.hotel_reservation.reservation.domain.entity.ReservationEntity;
import staysplit.hotel_reservation.reservation.domain.entity.ReservationParticipantEntity;
import staysplit.hotel_reservation.reservation.domain.entity.ReservationRoomEntity;
import staysplit.hotel_reservation.reservation.domain.dto.RoomDetailDto;
import staysplit.hotel_reservation.reservation.domain.dto.TempReservationDto;
import staysplit.hotel_reservation.reservation.domain.dto.reqeust.CreateReservationRequest;
import staysplit.hotel_reservation.reservation.domain.dto.response.ReservationDetailResponse;
import staysplit.hotel_reservation.reservation.domain.dto.response.ReservationListResponse;
import staysplit.hotel_reservation.reservation.repository.ReservationRepository;
import staysplit.hotel_reservation.reservation.repository.ReservationRoomRepository;
import staysplit.hotel_reservation.room.domain.RoomEntity;
import staysplit.hotel_reservation.room.repository.RoomRepository;
import staysplit.hotel_reservation.user.domain.entity.UserEntity;
import staysplit.hotel_reservation.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final ReservationRedisService redisService;
    private final ObjectMapper objectMapper;
    private final ReservationRoomRepository reservationRoomRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public Page<ReservationListResponse> getUserReservations(String email, Pageable pageable) {
        CustomerEntity customer = validateCustomer(email);
        Page<ReservationEntity> reservations = reservationRepository.findByCustomerIdOrderByIdDesc(customer.getId(), pageable);
        return reservations.map(reservation -> reservation.toListResponse(customer.getId()));
    }

    @Transactional(readOnly = true)
    public ReservationDetailResponse getReservationDetail(String email, Integer reservationId) {
        CustomerEntity customer = validateCustomer(email);

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND, "예약을 찾을 수 없습니다."));

        boolean isOwner = reservation.isOwner(customer.getId());
        boolean isParticipant = reservation.hasParticipant(customer.getUser().getEmail());

        if (!isOwner && !isParticipant) {
            throw new AppException(ErrorCode.UNAUTHORIZED_PROVIDER, "해당 예약에 접근할 권한이 없습니다.");
        }

        return reservation.toDetailResponse(customer.getId());
    }

    @Transactional
    public void cancelReservation(String email, Integer reservationId) {
        CustomerEntity customer = validateCustomer(email);

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND, "예약을 찾을 수 없습니다."));

        if (!reservation.isOwner(customer.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_PROVIDER, "예약 취소 권한이 없습니다.");
        }

        if (reservation.getStatus() == ReservationEntity.ReservationStatus.CANCELLED) {
            throw new AppException(ErrorCode.HOTEL_NOT_FOUND, "이미 취소된 예약입니다.");
        }

        reservation.updateStatus(ReservationEntity.ReservationStatus.CANCELLED);
    }

    public String createTempReservation(CreateReservationRequest request, Integer customerId) {
        String reservationKey = UUID.randomUUID().toString();

        List<RoomDetailDto> rooms = request.getRooms().stream()
                .map(room -> {
                    RoomEntity roomEntity = validateRoom(room.getRoomId());
                    return RoomDetailDto.builder()
                            .roomId(room.getRoomId())
                            .quantity(room.getQuantity())
                            .pricePerNight(roomEntity.getPrice())
                            .nights(calculateNights(request.getCheckInDate(), request.getCheckOutDate()))
                            .build();
                })
                .toList();

        Integer totalPrice = calculateTotalPrice(rooms);

        TempReservationDto tempReservation = TempReservationDto.builder()
                .userId(customerId)
                .hotelId(request.getHotelId())
                .hotelName(getHotelNameFromRooms(rooms))
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalPrice(totalPrice)
                .rooms(rooms)
                .invitedEmails(request.getInvitedEmails())
                .isSplitPayment(request.getIsSplitPayment())
                .createdAt(LocalDateTime.now())
                .build();

        redisService.saveTempReservation(reservationKey, tempReservation);
        return reservationKey;
    }

    @Transactional
    public ReservationEntity saveReserveAfterPayment(String merchantPayKey) {
        TempReservationDto tempReservation = redisService.getTempReservation(merchantPayKey);

        if (tempReservation == null) {
            throw new AppException(ErrorCode.HOTEL_NOT_FOUND, "유효하지 않은 결제 정보입니다.");
        }

        CustomerEntity customer = validateCustomer(tempReservation.getUserId());

        ReservationEntity reservation = ReservationEntity.builder()
                .customer(customer)
                .reservationNumber(generateReservationNumber())
                .checkInDate(tempReservation.getCheckInDate())
                .checkOutDate(tempReservation.getCheckOutDate())
                .totalPrice(tempReservation.getTotalPrice())
                .status(ReservationEntity.ReservationStatus.CONFIRMED)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        ReservationEntity savedReservation = reservationRepository.save(reservation);

        for (RoomDetailDto roomDetail : tempReservation.getRooms()) {
            RoomEntity room = validateRoom(roomDetail.getRoomId());

            ReservationRoomEntity reservationRoom = ReservationRoomEntity.builder()
                    .reservation(savedReservation)
                    .room(room)
                    .quantity(roomDetail.getQuantity())
                    .pricePerNight(roomDetail.getPricePerNight())
                    .nights(roomDetail.getNights())
                    .build();

            reservationRoom.calculateSubtotal();
            reservationRoomRepository.save(reservationRoom);
            savedReservation.addReservationRoom(reservationRoom);
        }

        if (Boolean.TRUE.equals(tempReservation.getIsSplitPayment()) &&
                tempReservation.getInvitedEmails() != null &&
                !tempReservation.getInvitedEmails().isEmpty()) {

            Integer splitAmount = tempReservation.getTotalPrice() / (tempReservation.getInvitedEmails().size() + 1);

            for (String email : tempReservation.getInvitedEmails()) {
                CustomerEntity invitedCustomer = findCustomerByEmail(email);
                ReservationParticipantEntity participant = ReservationParticipantEntity.of(
                        savedReservation, invitedCustomer, splitAmount
                );
                savedReservation.addParticipant(participant);
            }
        }

        redisService.deleteTempReservation(merchantPayKey);
        return savedReservation;
    }

    private CustomerEntity validateCustomer(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return customerRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "고객 정보를 찾을 수 없습니다."));
    }

    private CustomerEntity validateCustomer(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "고객을 찾을 수 없습니다."));
    }

    private HotelEntity validateHotel(Integer hotelId) {
        return hotelRepository.findByHotelId(hotelId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND, "호텔을 찾을 수 없습니다."));
    }

    private RoomEntity validateRoom(Integer roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND, "객실을 찾을 수 없습니다."));
    }

    private CustomerEntity findCustomerByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return null;
        return customerRepository.findByUser(user).orElse(null);
    }

    private Integer calculateTotalPrice(List<RoomDetailDto> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            return 0;
        }

        return rooms.stream()
                .mapToInt(room -> {
                    if (room.getPricePerNight() != null && room.getQuantity() != null) {
                        int nights = room.getNights() != null ? room.getNights() : 1;
                        return room.getPricePerNight() * room.getQuantity() * nights;
                    }
                    return 0;
                })
                .sum();
    }

    private Integer calculateNights(java.time.LocalDate checkIn, java.time.LocalDate checkOut) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    private String getHotelNameFromRooms(List<RoomDetailDto> rooms) {
        if (rooms.isEmpty()) return null;
        RoomEntity room = validateRoom(rooms.get(0).getRoomId());
        return room.getHotel().getName();
    }

    private String generateReservationNumber() {
        return "RES" + System.currentTimeMillis();
    }
}