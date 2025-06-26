package staysplit.hotel_reservation.hotel.dto.response;

import staysplit.hotel_reservation.hotel.entity.HotelEntity;

import java.math.BigDecimal;

public record GetHotelDetailResponse(
        Integer hotelId,
        String name,
        String address,
        BigDecimal longitude,
        BigDecimal latitude,
        String description,
        Integer starLevel,
        double rating,
        Integer reviewCount
) {
    public static GetHotelDetailResponse toDto(HotelEntity hotel) {
        return new GetHotelDetailResponse(
                hotel.getHotelId(),
                hotel.getName(),
                hotel.getAddress(),
                hotel.getLongitude(),
                hotel.getLatitude(),
                hotel.getDescription(),
                hotel.getStarLevel(),
                hotel.getRating(),
                hotel.getReviewCount()
        );
    }
}
