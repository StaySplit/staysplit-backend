package staysplit.hotel_reservation.hotel.dto.response;

import staysplit.hotel_reservation.hotel.entity.HotelEntity;

public record GetHotelListResponse(
        Integer hotelId,
        String name,
        String address,
        Integer starLevel,
        double rating,
        Integer reviewCount
) {
    public static GetHotelListResponse toDto(HotelEntity hotel) {
        return new GetHotelListResponse(
                hotel.getHotelId(),
                hotel.getName(),
                hotel.getAddress(),
                hotel.getStarLevel(),
                hotel.getRating(),
                hotel.getReviewCount()
        );
    }
}
