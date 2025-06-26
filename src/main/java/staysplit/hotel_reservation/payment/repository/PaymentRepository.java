package staysplit.hotel_reservation.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import staysplit.hotel_reservation.payment.domain.entity.PaymentEntity;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    List<PaymentEntity> findByCustomerId(Long customerId);
    boolean existsByImpUid(String impUid);
}