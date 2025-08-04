package staysplit.hotel_reservation.reservation.reposiotry.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import staysplit.hotel_reservation.reservation.domain.entity.ReservationEntity;
import staysplit.hotel_reservation.reservation.domain.enums.ReservationStatus;

import java.time.LocalDate;

@Repository
public interface ReservationRepositoryCustom {

    Page<ReservationEntity> findReservationsWithRoomByCustomerId(Integer customerId, Pageable pageable);

    Page<ReservationEntity> findReservationsByCustomer(Integer customerId, Pageable pageable);

    @Deprecated
    Page<ReservationEntity> findReservationsByCustomerWithFilters(Integer customerId, ReservationStatus status,
                                                                   LocalDate afterDate, Pageable pageable);

    Page<ReservationEntity> findReservationsByHotelWithFilters(Integer hotelId,
                                                               Pageable pageable,
                                                               ReservationSearchConditionForProviders condition);
}
