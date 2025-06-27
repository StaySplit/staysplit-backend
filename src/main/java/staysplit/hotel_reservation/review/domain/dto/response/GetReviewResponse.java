package staysplit.hotel_reservation.review.domain.dto.response;

import staysplit.hotel_reservation.review.domain.entity.ReviewEntity;

public record GetReviewResponse(
        Long reviewId,
        Long customerId,
        Long hotelId,
        String nickname,
        String content,
        Integer rating
        //닉네임추가
) {
    public static GetReviewResponse from(ReviewEntity review) {
        return new GetReviewResponse(
                review.getId(),
                review.geCustomerId(),
                review.getHotelId(),
                review.getNickname(),
                review.getContent(),
                review.getRating()
        );
    }
}
