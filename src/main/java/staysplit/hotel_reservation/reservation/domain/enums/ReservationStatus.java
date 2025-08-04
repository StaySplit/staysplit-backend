package staysplit.hotel_reservation.reservation.domain.enums;

import java.util.Optional;

public enum ReservationStatus {
    PENDING, // 결제 미완료, 임시 예약 (재고 확보)
    CONFIRMED, // 결제 완료
    CANCELLED; // 예약 취소

    public static Optional<ReservationStatus> from(String value) {
        try {
            return Optional.of(ReservationStatus.valueOf(value.toUpperCase()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
