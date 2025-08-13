package staysplit.hotel_reservation.provider.domain.dto.reqeust;

public record ProviderSignupRequest(
        String email,
        String password,
        String nickname
) {}
