package staysplit.hotel_reservation.user.domain.dto.request;


public record LoginRequest(
        String email,
        String password
) {
}
