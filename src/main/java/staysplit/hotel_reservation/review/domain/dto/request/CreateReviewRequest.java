package staysplit.hotel_reservation.review.domain.dto.request;

public record CreateReviewRequest(
   Long id,
   Long customerId,
   String nickname,
   Long hotelId,
   String content,
   Integer rating
) {}
