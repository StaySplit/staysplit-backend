package staysplit.hotel_reservation.reservation.reposiotry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import staysplit.hotel_reservation.reservation.domain.entity.ReservationEntity;
import staysplit.hotel_reservation.reservation.reposiotry.search.ReservationRepositoryCustom;


@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Integer>, ReservationRepositoryCustom {
/*
    QueryDSL사용하지 않는경우 사용
    @Query("SELECT r FROM ReservationEntity r " +
            "JOIN FETCH r.hotel h " +
            "WHERE r.customer.id = :customerId " +
            "ORDER BY r.createdAt DESC")
    Page<ReservationEntity> findReservationsWithRoomByCustomerId(@Param("customerId") Integer customerId, Pageable pageable);
*/

}
