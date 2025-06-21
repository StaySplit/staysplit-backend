package staysplit.hotel_reservation.review.domain.dto.request;

public record ModifyReviewRequest (
        Long reviewId,
        Long userId,
        Long hotelId,
        String content,
        Integer rating
){

}
