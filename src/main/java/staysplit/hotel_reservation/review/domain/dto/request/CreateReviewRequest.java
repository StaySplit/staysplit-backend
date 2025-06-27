package staysplit.hotel_reservation.review.domain.dto.request;

public record CreateReviewRequest(
   Integer id,
   Integer userId,
   String nickname,
   Integer hotelId,
   String content,
   Integer rating
) {}
