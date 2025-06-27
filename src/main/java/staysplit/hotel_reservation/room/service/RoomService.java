package staysplit.hotel_reservation.room.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.hotel.entity.HotelEntity;
import staysplit.hotel_reservation.hotel.repository.HotelRepository;
import staysplit.hotel_reservation.provider.domain.entity.ProviderEntity;
import staysplit.hotel_reservation.provider.repository.ProviderRepository;
import staysplit.hotel_reservation.room.domain.RoomEntity;
import staysplit.hotel_reservation.room.domain.dto.request.CreateRoomRequest;
import staysplit.hotel_reservation.room.domain.dto.request.UpdateRoomRequest;
import staysplit.hotel_reservation.room.domain.dto.response.RoomDeleteResponse;
import staysplit.hotel_reservation.room.domain.dto.response.RoomDetailResponse;
import staysplit.hotel_reservation.room.repository.RoomRepository;
import staysplit.hotel_reservation.user.domain.entity.UserEntity;
import staysplit.hotel_reservation.user.repository.UserRepository;


@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;

    public RoomDetailResponse createRoom(String email, CreateRoomRequest request) {
        ProviderEntity provider = validateProvider(email);

        RoomEntity room = RoomEntity.builder()
                .hotel(provider.getHotel())
                .photoUrl(request.photoUrl())
                .roomType(request.roomType())
                .quantity(request.quantity())
                .price(request.price())
                .description(request.description())
                .maxOccupancy(request.occupancy())
                .roomType(request.roomType())
                .build();

        roomRepository.save(room);
        providerRepository.save(provider);
        return RoomDetailResponse.from(room);
    }

    public RoomDetailResponse updateRoom(String email, Long roomId, UpdateRoomRequest request) {
        RoomEntity room = validateRoom(roomId);
        ProviderEntity provider = validateProvider(email);
        if (!hasAuthority(provider, room)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_PROVIDER,
                    ErrorCode.UNAUTHORIZED_PROVIDER.getMessage());
        }

        room.updateRoom(request.roomType(), request.photoUrl(), request.maxOccupancy(),
                request.price(), request.description(), request.quantity());

        return RoomDetailResponse.from(room);
    }

    public RoomDeleteResponse deleteRoom(String email, Long roomId) {
        RoomEntity room = validateRoom(roomId);
        ProviderEntity provider = validateProvider(email);
        if (!provider.getId().equals(room.getHotel().getProvider().getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_PROVIDER,
                    ErrorCode.UNAUTHORIZED_PROVIDER.getMessage());
        }
        RoomDeleteResponse response = new RoomDeleteResponse(room.getHotel().getHotelId(), room.getId());
        roomRepository.delete(room);

        return response;
    }
    

    @Transactional(readOnly = true)
    public RoomDetailResponse findRoomById(Long roomId) {
        RoomEntity room = validateRoom(roomId);
        return RoomDetailResponse.from(room);
    }

    @Transactional(readOnly = true)
    public Page<RoomDetailResponse> findAllRoomsByHotel(Long hotelId, Pageable pageable) {
        HotelEntity hotel = validateHotelById(hotelId);
        Page<RoomEntity> hotels = roomRepository.findByHotel_HotelId(hotelId, pageable);
        return hotels.map(RoomDetailResponse::from);
    }


    // ------------//

    private boolean hasAuthority(ProviderEntity provider, RoomEntity room) {

        if (!room.getHotel().getProvider().getId().equals(provider.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_PROVIDER,
                    ErrorCode.UNAUTHORIZED_PROVIDER.getMessage());
        }
        return true;
    }

    private HotelEntity validateHotelById(Long hotelId) {
        return hotelRepository.findByHotelId(hotelId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND,
                        ErrorCode.HOTEL_NOT_FOUND.getMessage()));
    }

    private RoomEntity validateRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND,
                        ErrorCode.ROOM_NOT_FOUND.getMessage()));
    }

    private ProviderEntity validateProvider(String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));

        return providerRepository.findById(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND,
                        ErrorCode.USER_NOT_FOUND.getMessage()));
    }
}
