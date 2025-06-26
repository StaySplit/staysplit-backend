package staysplit.hotel_reservation.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import staysplit.hotel_reservation.reservation.domain.dto.response.ReservationDetailResponse;
import staysplit.hotel_reservation.user.domain.entity.UserEntity;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;

@Entity
@Table(name = "user_reservation")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReservationParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    @Setter
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;


    @Transient
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


    public static ReservationParticipant of(Reservation reservation, UserEntity user, Integer splitAmount) {
        return ReservationParticipant.builder()
                .reservation(reservation)
                .user(user)
                .splitAmount(splitAmount)
                .invitationToken(java.util.UUID.randomUUID().toString())
                .build();
    }


    public static ReservationParticipant ofInvitation(Reservation reservation, Integer splitAmount) {
        return ReservationParticipant.builder()
                .reservation(reservation)
                .user(null)
                .splitAmount(splitAmount)
                .invitationToken(java.util.UUID.randomUUID().toString())
                .build();
    }

    public static ReservationParticipant ofEmailInvitation(Reservation reservation, String email, Integer splitAmount) {
        return ReservationParticipant.builder()
                .reservation(reservation)
                .user(null)
                .splitAmount(splitAmount)
                .invitationToken(java.util.UUID.randomUUID().toString())
                .build();
    }

    @Deprecated
    public static ReservationParticipant of(Reservation reservation, String email, String userName, Integer splitAmount) {
        return ofEmailInvitation(reservation, email, splitAmount);
    }

    public void updatePaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }

    public boolean isPaid() {
        return this.paymentStatus == PaymentStatus.COMPLETED;
    }

    public void linkUser(UserEntity user) {
        this.user = user;
    }

    public String getUserName() {
        if (user == null) {
            return "초대 대기중";
        }
        String email = user.getEmail();
        return email != null ? email : "이메일 없음";
    }

    public String getUserEmail() {
        if (user == null) {
            return null;
        }
        return user.getEmail();
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