package staysplit.hotel_reservation.reservation.dto.response;

import lombok.Builder;

@Builder
public record ParticipantDetailResponse(
        String email,
        String name,
        Integer splitAmount,
        String paymentStatus
) {
}
