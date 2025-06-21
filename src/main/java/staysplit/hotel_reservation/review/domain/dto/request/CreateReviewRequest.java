package staysplit.hotel_reservation.review.domain.dto.request;

public record CreateReviewRequest(
   Long reviewId,
   Long userId,
   Long hotelId,
   String content,
   Integer rating
) {}
