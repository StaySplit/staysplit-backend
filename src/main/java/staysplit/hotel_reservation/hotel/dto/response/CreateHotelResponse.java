package staysplit.hotel_reservation.hotel.dto.response;

import staysplit.hotel_reservation.hotel.entity.HotelEntity;

public record CreateHotelResponse(
        Long hotelId,
        String name,
        String address,
        String description,
        Integer starLevel,
        double rating,
        String imageUrl
) {
    public static CreateHotelResponse toDto(HotelEntity hotel) {
        return new CreateHotelResponse(
                hotel.getHotelId(),
                hotel.getName(),
                hotel.getAddress(),
                hotel.getDescription(),
                hotel.getStarLevel(),
                hotel.getRating(),
                hotel.getImageUrl()
        );
    }
}
