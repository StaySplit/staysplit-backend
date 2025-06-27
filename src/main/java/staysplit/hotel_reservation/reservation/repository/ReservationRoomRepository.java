package staysplit.hotel_reservation.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import staysplit.hotel_reservation.reservation.domain.entity.ReservationRoomEntity;

import java.util.List;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoomEntity, Integer> {

    List<ReservationRoomEntity> findByReservationReservationId(Integer reservationId);

    @Query("SELECT rr FROM ReservationRoomEntity rr WHERE rr.room.hotel.hotelId = :hotelId")
    List<ReservationRoomEntity> findByHotelId(@Param("hotelId") Integer hotelId);
}