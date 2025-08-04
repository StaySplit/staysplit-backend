package staysplit.hotel_reservation.reservation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import staysplit.hotel_reservation.hotel.entity.HotelEntity;
import staysplit.hotel_reservation.photo.service.PhotoUrlBuilder;
import staysplit.hotel_reservation.reservation.dto.response.*;
import staysplit.hotel_reservation.reservation.domain.entity.ReservationEntity;
import staysplit.hotel_reservation.reservation.domain.entity.ReservationParticipantEntity;
import staysplit.hotel_reservation.reservedRoom.dto.response.ReservedRoomInfo;
import staysplit.hotel_reservation.reservedRoom.entity.ReservedRoomEntity;
import staysplit.hotel_reservation.room.domain.RoomEntity;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationMapper {

    private final PhotoUrlBuilder photoUrlBuilder;
    public ReservationDetailResponse toReservationDetailResponse(ReservationEntity reservation) {
        HotelEntity hotel = reservation.getHotel();

        return ReservationDetailResponse.builder()
                .reservationId(reservation.getId())
                .reservationNumber(reservation.getReservationNumber())
                .reservationStatus(reservation.getStatus().toString())
                .nights(reservation.getNights())
                .checkIn(reservation.getCheckInDate())
                .checkOut(reservation.getCheckOutDate())
                .participants(reservation.getParticipants().stream().map(this::toParticipantDetailResponse).toList())
                .rooms(reservation.getReservedRooms().stream().map(this::toReservedRoomDetailResponse).toList())
                .pricePaid(reservation.getPricePaid())
                .totalPrice(reservation.getTotalPrice())
                .hotelName(hotel.getName())
                .hotelAddress(hotel.getAddress())
                .hotelMainImageUrl(
                        hotel.getMainPhoto().isPresent() ? hotel.getMainPhoto().get().buildFullUrl(photoUrlBuilder) : null)
                .build();
    }

    public ReservationInfoListResponse toReservationListResponse(ReservationEntity reservation) {
        return ReservationInfoListResponse.builder()
                // 예약 정보
                .reservationId(reservation.getId())
                .userName(reservation.getCustomer().getName())
                .reservationNumber(reservation.getReservationNumber())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .reservationStatus(reservation.getStatus().toString())
                .totalPrice(reservation.getTotalPrice())
                .pricePaid(reservation.getPricePaid())
                .createdAt(reservation.getCreatedAt())

                // 호텔 정보
                .hotelName(reservation.getHotel().getName())
                .hotelAddress(reservation.getHotel().getAddress())
                .hotelCheckInTime(reservation.getHotel().getHotelCheckInTime().toString())
                .hotelCheckOutTime(reservation.getHotel().getHotelCheckOutTime().toString())
                .hotelPhotos(reservation.getHotel().getMainPhoto().isPresent() ? reservation.getHotel().getMainPhoto().get().buildFullUrl(photoUrlBuilder): null )

                // 방 정보들 (ReservationRoom + Room 조인 정보)
                .rooms(reservation.getReservedRooms().stream()
                        .map(this::toReservedRoomInfo)
                        .toList())
                .build();
    }

    public ReservedRoomInfo toReservedRoomInfo(ReservedRoomEntity reservationRoom) {
        RoomEntity room = reservationRoom.getRoom();

        return ReservedRoomInfo.builder()
                .reservationRoomId(reservationRoom.getId())
                .roomId(reservationRoom.getRoom().getId())
                .quantity(reservationRoom.getQuantity())
                .pricePerNight(reservationRoom.getPricePerNight())
                .nights(reservationRoom.getNights())
                .subtotalPrice(reservationRoom.getSubtotalPrice())
                .participantCount(
                        reservationRoom.getParticipants() != null
                                ? reservationRoom.getParticipants().size()
                                : 0
                )

                // Room 테이블 정보
                .roomType(room != null ? room.getRoomType() : null)
                .maxOccupancy(room != null ? room.getMaxOccupancy() : null)
                .roomDescription(room != null ? room.getDescription() : null)
                .build();
    }

    public ParticipantDetailResponse toParticipantDetailResponse(ReservationParticipantEntity participant) {
        return ParticipantDetailResponse.builder()
                .email(participant.getCustomer().getUser().getEmail())
                .name(participant.getCustomer().getName())
                .splitAmount(participant.getSplitAmount())
                .paymentStatus(participant.getPaymentStatus().toString())
                .build();
    }

    public ReservedRoomDetailResponse toReservedRoomDetailResponse(ReservedRoomEntity reservedRoom) {

        return ReservedRoomDetailResponse.builder()
                .roomId(reservedRoom.getId())
                .roomType(reservedRoom.getRoom().getRoomType())
                .quantity(reservedRoom.getQuantity())
                .nights(reservedRoom.getNights())
                .pricePerNight(reservedRoom.getPricePerNight())
                .subTotal(reservedRoom.getSubtotalPrice())
                .build();
    }

    public ReservationListResponse toListResponse(ReservationEntity reservation) {
        ReservedRoomEntity firstRoom = reservation.getReservedRooms().get(0);
        RoomEntity room = firstRoom.getRoom();
        HotelEntity hotel = room.getHotel();

        return ReservationListResponse.builder()
                .reservationId(reservation.getId())
                .reservationNumber(reservation.getReservationNumber())
                .reservationStatus(reservation.getStatus().toString())
                .totalPrice(reservation.getTotalPrice())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .hotelName(hotel.getName())
                .hotelAddress(hotel.getAddress())
                .hotelMainImageUrl(
                        hotel.getMainPhoto().isPresent() ? hotel.getMainPhoto().get().buildFullUrl(photoUrlBuilder) : null
                )
                .build();
    }

    public ReservationListResponseForProvider toListResponseForProvider(ReservationEntity reservation) {
        List<String> participantNames = reservation.getParticipants()
                .stream().map(p -> p.getCustomer().getName())
                .toList();

        return ReservationListResponseForProvider.builder()
                .reservationId(reservation.getId())
                .reservationNumber(reservation.getReservationNumber())
                .nights(reservation.getNights())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .totalPrice(reservation.getTotalPrice())
                .reservationStatus(reservation.getStatus().toString())
                .participantNames(participantNames)
                .numberOfParticipants(participantNames.size())
                .build();
    }

}
