package staysplit.hotel_reservation.payment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import staysplit.hotel_reservation.payment.domain.entity.PaymentEntity;


public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer> {
    Page<PaymentEntity> findByCustomerId(Integer customerId, Pageable page);
    PaymentEntity findByReservationId(Integer reservationId);
    boolean existsByImpUid(String impUid);
}