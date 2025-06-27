package staysplit.hotel_reservation.reservation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import staysplit.hotel_reservation.reservation.domain.entity.ReservationEntity;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Integer> {

    @Query("SELECT r FROM ReservationEntity r WHERE r.customer.id = :customerId ORDER BY r.reservationId DESC")
    Page<ReservationEntity> findByCustomerIdOrderByIdDesc(@Param("customerId") Integer customerId, Pageable pageable);

    @Query("SELECT DISTINCT r FROM ReservationEntity r " +
            "LEFT JOIN r.participants p " +
            "WHERE r.customer.id = :customerId OR p.customer.id = :customerId " +
            "ORDER BY r.reservationId DESC")
    Page<ReservationEntity> findAllUserReservations(@Param("customerId") Integer customerId, Pageable pageable);
}