package staysplit.hotel_reservation.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import staysplit.hotel_reservation.reservation.domain.dto.response.ReservationDetailResponse;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;

@Entity
@Table(name = "user_reservation")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReservationParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    @Setter
    private ReservationEntity reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(name = "split_amount", nullable = false)
    private Integer splitAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "invitation_token")
    private String invitationToken;

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED
    }

    public static ReservationParticipantEntity of(ReservationEntity reservation, CustomerEntity customer, Integer splitAmount) {
        return ReservationParticipantEntity.builder()
                .reservation(reservation)
                .customer(customer)
                .splitAmount(splitAmount)
                .invitationToken(java.util.UUID.randomUUID().toString())
                .build();
    }

    public static ReservationParticipantEntity ofInvitation(ReservationEntity reservation, Integer splitAmount) {
        return ReservationParticipantEntity.builder()
                .reservation(reservation)
                .customer(null)
                .splitAmount(splitAmount)
                .invitationToken(java.util.UUID.randomUUID().toString())
                .build();
    }

    public static ReservationParticipantEntity ofEmailInvitation(ReservationEntity reservation, String email, Integer splitAmount) {
        return ReservationParticipantEntity.builder()
                .reservation(reservation)
                .customer(null)
                .splitAmount(splitAmount)
                .invitationToken(java.util.UUID.randomUUID().toString())
                .build();
    }

    public void updatePaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }

    public boolean isPaid() {
        return this.paymentStatus == PaymentStatus.COMPLETED;
    }

    public void linkCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public String getUserName() {
        return customer != null ? customer.getName() : "초대 대기중";
    }

    public String getUserEmail() {
        return customer != null ? customer.getUser().getEmail() : null;
    }

    public ReservationDetailResponse.ParticipantInfo toParticipantInfo() {
        return ReservationDetailResponse.ParticipantInfo.builder()
                .email(this.getUserEmail())
                .name(this.getUserName())
                .splitAmount(this.splitAmount)
                .paymentStatus(this.paymentStatus.name())
                .build();
    }
}