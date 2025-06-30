package staysplit.hotel_reservation.payment.domain.dto.request;

import staysplit.hotel_reservation.payment.domain.enums.PaymentStatus;

public record CreatePaymentRequest(
        Long customerId,
        String payMethod,
        String payName,
        Integer amount,
        PaymentStatus status,
        String impUid
) {
}
