package staysplit.hotel_reservation.review.domain.dto.request;

public record ModifyReviewRequest (
        Integer userId,
        String content,
        Integer rating
){

}
