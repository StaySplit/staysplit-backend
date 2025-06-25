package staysplit.hotel_reservation.review.domain.dto.response;

import staysplit.hotel_reservation.review.domain.entity.ReviewEntity;

public record CreateReviewResponse(
        Long reviewId,
        Long customerId,
        Long hotelId,
        String nickname,
        String content,
        Integer rating
) {

    public static CreateReviewResponse from(ReviewEntity review) {
        return new CreateReviewResponse(
                review.getId(),
                review.geCustomerId(),
                review.getHotelId(),
                review.getNickname(),
                review.getContent(),
                review.getRating()
        );
    }
}
