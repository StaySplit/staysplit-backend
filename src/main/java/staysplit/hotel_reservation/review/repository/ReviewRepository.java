package staysplit.hotel_reservation.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import staysplit.hotel_reservation.review.domain.entity.ReviewEntity;


@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    @Query("SELECT r FROM ReviewEntity r WHERE r.hotel.hotelId = :hotelId AND r.deletedAt IS NULL")
    Page<ReviewEntity> getReviewByHotelId(@Param("hotelId") Long hotelId, Pageable pageable);

    @Query("SELECT r FROM ReviewEntity r WHERE r.customer.id = :customerId AND r.deletedAt IS NULL")
    Page<ReviewEntity> getReviewByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    @Query("SELECT COUNT(r) > 0 FROM ReviewEntity r WHERE r.customer.id = :customerId AND r.hotel.hotelId = :hotelId AND r.deletedAt IS NULL")
    boolean existsByUserIdAndHotelId(@Param("customerId") Long customerId, @Param("hotelId") Long hotelId);
}
