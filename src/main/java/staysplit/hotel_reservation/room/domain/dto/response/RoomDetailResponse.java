package staysplit.hotel_reservation.room.domain.dto.response;

import staysplit.hotel_reservation.room.domain.RoomEntity;

public record RoomDetailResponse(
        Long hotelId,
        String hotelName,
        Long roomId,
        String roomType,
        Integer quantity,
        Integer maxOccupancy,
        Integer price
) {
    public static RoomDetailResponse from(RoomEntity room) {
        return new RoomDetailResponse(room.getHotel().getHotelId(),
                room.getHotel().getName(),
                room.getId(),
                room.getRoomType(),
                room.getQuantity(),
                room.getMaxOccupancy(),
                room.getPrice());
    }
}
