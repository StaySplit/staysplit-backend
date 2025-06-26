package staysplit.hotel_reservation.hotel.dto.request;

public record CreateHotelRequest(
        String name,
        String address,
        String description,
        Integer starLevel,
        Double rating
) {}
