package staysplit.hotel_reservation.provider.domain.dto.response;

import staysplit.hotel_reservation.user.domain.entity.UserEntity;

public record ProviderSignupResponse(
        Integer id,
        String email,
        String role,
        String nickname
) {
    public ProviderSignupResponse(UserEntity user, String nickname) {
        this(user.getId(), user.getEmail(), user.getRole().toString(), nickname);
    }
}
