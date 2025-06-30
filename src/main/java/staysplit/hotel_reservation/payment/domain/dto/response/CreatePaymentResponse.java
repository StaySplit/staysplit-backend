package staysplit.hotel_reservation.payment.domain.dto.response;

import staysplit.hotel_reservation.payment.domain.entity.PaymentEntity;
import staysplit.hotel_reservation.payment.domain.enums.PaymentStatus;

import java.time.LocalDateTime;

public record CreatePaymentResponse(
        Long paymentId,
        PaymentStatus status,
        LocalDateTime paidAt
) {
    public static CreatePaymentResponse from(PaymentEntity entity) {
        return new CreatePaymentResponse(entity.getId(), entity.getStatus(), entity.getPaidAt());
    }
}