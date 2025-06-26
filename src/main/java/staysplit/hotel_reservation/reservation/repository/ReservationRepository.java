package staysplit.hotel_reservation.reservation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import staysplit.hotel_reservation.reservation.domain.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    Page<Reservation> findByUserIdOrderByIdDesc(Integer userId, Pageable pageable);
}