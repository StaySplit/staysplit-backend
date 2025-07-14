package staysplit.hotel_reservation.hotel.dto.request;

import java.math.BigDecimal;

public record CreateHotelRequest(
        String name,
        String address,
        BigDecimal longitude,
        BigDecimal latitude,
        String description,
        Integer starLevel
) {}
