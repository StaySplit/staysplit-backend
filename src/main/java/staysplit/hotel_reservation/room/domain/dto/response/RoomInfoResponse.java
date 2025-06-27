package staysplit.hotel_reservation.room.domain.dto.response;

import staysplit.hotel_reservation.room.domain.RoomEntity;

public record RoomInfoResponse(
        Integer hotelId,
        String hotelName,
        String roomType,
        Integer maxOccupancy,
        Integer price
) {
    public static RoomInfoResponse from(RoomEntity room) {
        return new RoomInfoResponse(room.getHotel().getHotelId(),
                room.getHotel().getName(),
                room.getRoomType(),
                room.getMaxOccupancy(),
                room.getPrice());
    }
}
