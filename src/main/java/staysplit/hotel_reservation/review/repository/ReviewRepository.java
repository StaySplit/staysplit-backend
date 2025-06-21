package staysplit.hotel_reservation.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import staysplit.hotel_reservation.review.domain.entity.ReviewEntity;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Page<ReviewEntity> getReviewByHotelId(Long hotelId, Pageable pageable);
    Page<ReviewEntity> getReviewByUserId(Long userId, Pageable pageable);
    boolean existsByUserIdAndHotelId(Long userId, Long hotelId);

}
