package staysplit.hotel_reservation.payment.domain.dto.response;

import staysplit.hotel_reservation.payment.domain.entity.PaymentEntity;
import staysplit.hotel_reservation.payment.domain.enums.PaymentStatus;

import java.time.LocalDateTime;

public record GetPaymentResponse(
        Integer paymentId,
        PaymentStatus status,
        LocalDateTime paidAt,
        Integer amount,
        String method,
        String payName,
        Integer reservationId) {
    public static GetPaymentResponse from(PaymentEntity payment) {
        return new GetPaymentResponse(
                payment.getId(),
                payment.getStatus(),
                payment.getPaidAt(),
                payment.getAmount(),
                payment.getPayMethod(),
                payment.getPayName(),
                payment.getReservation().getId()
        );
    }
}


