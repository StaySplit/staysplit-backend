package staysplit.hotel_reservation.reservation.dto.response;

import lombok.Builder;
import staysplit.hotel_reservation.photo.domain.PhotoEntity;
import staysplit.hotel_reservation.reservedRoom.dto.response.ReservedRoomInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Builder
public record ReservationInfoListResponse(
        // 예약 기본 정보
        Integer reservationId,
        String userName,
        String reservationNumber,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        String reservationStatus,
        Integer totalPrice,
        Integer pricePaid,
        LocalDateTime createdAt,

        // 호텔 정보
        String hotelName,
        String hotelAddress,
        String hotelCheckInTime,
        String hotelCheckOutTime,
        List<PhotoEntity> hotelPhotos,

        // 방 정보들
        List<ReservedRoomInfo> rooms
) {}

