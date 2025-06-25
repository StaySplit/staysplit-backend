package staysplit.hotel_reservation.review.domain.dto.request;

public record ModifyReviewRequest (
        Long customerId,
        String content,
        Integer rating
){

}
