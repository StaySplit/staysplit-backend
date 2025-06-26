package staysplit.hotel_reservation.payment.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.payment.domain.enums.PaymentStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    CustomerEntity customer;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "reservation_id", nullable = false)
//    ReservationEntity reservation;

//    @Column(unique = true)
//    private String merchantUid; // 가맹점 UID

    @Column(unique = true)
    private String impUid; // 아임포트 UID

    @Column
    private String payMethod;

    @Column
    private String payName;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @CreatedDate
    @Column(name = "paid_at", updatable = false)
    private LocalDateTime paidAt;
}
