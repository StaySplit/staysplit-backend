package staysplit.hotel_reservation.review.domain.dto.response;

import staysplit.hotel_reservation.customer.domain.dto.response.CustomerInfoResponse;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.review.domain.entity.ReviewEntity;

import java.time.LocalDate;

public record CreateReviewResponse(
        Long reviewId,
        Long userId,
        Long hotelId,
        String content,
        Integer rating
) {

    public static CreateReviewResponse from(ReviewEntity review) {
        return new CreateReviewResponse(
                review.getReviewId(),
                review.getUserId(),
                review.getHotelId(),
                review.getContent(),
                review.getRating()
        );
    }
}
