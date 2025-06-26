package staysplit.hotel_reservation.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.hotel.entity.HotelEntity;
import staysplit.hotel_reservation.hotel.repository.HotelRepository;
import staysplit.hotel_reservation.reservation.domain.entity.Reservation;
import staysplit.hotel_reservation.reservation.domain.entity.ReservationParticipant;
import staysplit.hotel_reservation.reservation.domain.dto.RoomDetailDto;
import staysplit.hotel_reservation.reservation.domain.dto.TempReservationDto;
import staysplit.hotel_reservation.reservation.domain.dto.reqeust.CreateReservationRequest;
import staysplit.hotel_reservation.reservation.domain.dto.response.ReservationDetailResponse;
import staysplit.hotel_reservation.reservation.domain.dto.response.ReservationListResponse;
import staysplit.hotel_reservation.reservation.repository.ReservationRepository;
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

    @Transactional(readOnly = true)
    public Page<ReservationListResponse> getUserReservations(String email, Pageable pageable) {
        UserEntity user = validateUser(email);
        Page<Reservation> reservations = reservationRepository.findByUserIdOrderByIdDesc(user.getId(), pageable);
        return reservations.map(reservation -> reservation.toListResponse(user.getId()));
    }

    @Transactional(readOnly = true)
    public ReservationDetailResponse getReservationDetail(String email, Integer reservationId) {
        UserEntity user = validateUser(email);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND, "예약을 찾을 수 없습니다."));

        boolean isOwner = reservation.isOwner(user.getId());
        boolean isParticipant = reservation.hasParticipant(user.getEmail());

        if (!isOwner && !isParticipant) {
            throw new AppException(ErrorCode.UNAUTHORIZED_PROVIDER, "해당 예약에 접근할 권한이 없습니다.");
        }

        return reservation.toDetailResponse(user.getId());
    }

    @Transactional
    public void cancelReservation(String email, Integer reservationId) {
        UserEntity user = validateUser(email);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND, "예약을 찾을 수 없습니다."));

        if (!reservation.isOwner(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_PROVIDER, "예약 취소 권한이 없습니다.");
        }

        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new AppException(ErrorCode.HOTEL_NOT_FOUND, "이미 취소된 예약입니다.");
        }

        reservation.updateStatus(Reservation.ReservationStatus.CANCELLED);
    }

    public String createTempReservation(CreateReservationRequest request, Integer userId) {
        HotelEntity hotel = validateHotel(request.getHotelId());
        String reservationKey = UUID.randomUUID().toString();

        List<RoomDetailDto> rooms = request.getRooms().stream()
                .map(room -> RoomDetailDto.builder()
                        .roomId(room.getRoomId())
                        .quantity(room.getQuantity())
                        .build())
                .toList();

        Integer totalPrice = calculateTotalPrice(rooms);

        TempReservationDto tempReservation = TempReservationDto.builder()
                .userId(userId)
                .hotelId(request.getHotelId())
                .hotelName(hotel.getName())
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
    public Reservation saveReserveAfterPayment(String merchantPayKey) {
        TempReservationDto tempReservation = redisService.getTempReservation(merchantPayKey);

        if (tempReservation == null) {
            throw new AppException(ErrorCode.HOTEL_NOT_FOUND, "유효하지 않은 결제 정보입니다.");
        }

        UserEntity user = userRepository.findById(tempReservation.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        HotelEntity hotel = validateHotel(tempReservation.getHotelId());

        Reservation reservation = Reservation.builder()
                .user(user)
                .hotel(hotel)
                .reservationNumber(generateReservationNumber())
                .checkInDate(tempReservation.getCheckInDate())
                .checkOutDate(tempReservation.getCheckOutDate())
                .totalPrice(tempReservation.getTotalPrice())
                .status(Reservation.ReservationStatus.CONFIRMED)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);


        if (Boolean.TRUE.equals(tempReservation.getIsSplitPayment()) &&
                tempReservation.getInvitedEmails() != null &&
                !tempReservation.getInvitedEmails().isEmpty()) {

            Integer splitAmount = tempReservation.getTotalPrice() / (tempReservation.getInvitedEmails().size() + 1);

            for (String email : tempReservation.getInvitedEmails()) {
                ReservationParticipant participant = ReservationParticipant.of(
                        savedReservation, email, "", splitAmount
                );
                savedReservation.addParticipant(participant);
            }
        }

        redisService.deleteTempReservation(merchantPayKey);
        return savedReservation;
    }

    private UserEntity validateUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    private HotelEntity validateHotel(Integer hotelId) {
        return hotelRepository.findByHotelId(hotelId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND, "호텔을 찾을 수 없습니다."));
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

    private String generateReservationNumber() {
        return "RES" + System.currentTimeMillis();
    }
}