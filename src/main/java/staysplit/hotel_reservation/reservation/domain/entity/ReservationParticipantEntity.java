package staysplit.hotel_reservation.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.reservation.domain.enums.PaymentStatus;
import staysplit.hotel_reservation.reservedRoom.entity.ReservedRoomEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReservationParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_participant_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    @Setter
    private ReservationEntity reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_room_id", nullable = false)
    private ReservedRoomEntity reservedRoom;

    @Column(nullable = false)
    private Integer splitAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private String invitationToken;

    public void updatePaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }

    public boolean isPaid() {
        return this.paymentStatus == PaymentStatus.CONFIRMED;
    }

}

