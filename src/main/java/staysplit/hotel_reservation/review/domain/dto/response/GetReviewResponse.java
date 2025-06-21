package staysplit.hotel_reservation.review.domain.dto.response;

import staysplit.hotel_reservation.review.domain.entity.ReviewEntity;

public record GetReviewResponse(
        Long reviewId,
        Long userId,
        Long hotelId,
        String content,
        Integer rating
        //닉네임추가
) {
    public static GetReviewResponse from(ReviewEntity review) {
        return new GetReviewResponse(
                review.getReviewId(),
                review.getUserId(),
                review.getHotelId(),
                review.getContent(),
                review.getRating()
        );
    }
}
