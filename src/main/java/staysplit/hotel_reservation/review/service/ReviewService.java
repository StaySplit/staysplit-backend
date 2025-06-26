package staysplit.hotel_reservation.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.hotel.entity.HotelEntity;
import staysplit.hotel_reservation.hotel.repository.HotelRepository;
import staysplit.hotel_reservation.review.domain.dto.request.CreateReviewRequest;
import staysplit.hotel_reservation.review.domain.dto.request.ModifyReviewRequest;
import staysplit.hotel_reservation.review.domain.dto.response.CreateReviewResponse;
import staysplit.hotel_reservation.review.domain.dto.response.GetReviewResponse;
import staysplit.hotel_reservation.review.domain.entity.ReviewEntity;
import staysplit.hotel_reservation.review.repository.ReviewRepository;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.user.domain.entity.UserEntity;
import staysplit.hotel_reservation.user.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository; // ✅ CustomerRepository → UserRepository

    public CreateReviewResponse createReview(CreateReviewRequest request){
        boolean exists = reviewRepository.existsByUserIdAndHotelId(request.userId(), request.hotelId());
        if (exists) {
            throw new IllegalStateException("이미 해당 호텔에 대한 리뷰를 작성하셨습니다.");
        }

        HotelEntity hotel = hotelRepository.findById(request.hotelId())
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND, "호텔을 찾을 수 없습니다."));


        UserEntity user = userRepository.findById(request.userId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
        ReviewEntity review = ReviewEntity.builder()
                .user(user)
                .hotel(hotel)
                .content(request.content())
                .rating(request.rating())
                .nickname(request.nickname())
                .build();

        reviewRepository.save(review);
        return CreateReviewResponse.from(review);
    }

    @Transactional(readOnly = true)
    public Page<GetReviewResponse> getReviewByUserId(Integer userId, Pageable pageable){
        Page<ReviewEntity> review = reviewRepository.getReviewByUserId(userId, pageable);
        return review.map(GetReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<GetReviewResponse> getReviewByHotelId(Integer hotelId, Pageable pageable){
        Page<ReviewEntity> review = reviewRepository.getReviewByHotelId(hotelId, pageable);
        return review.map(GetReviewResponse::from);
    }

    public GetReviewResponse modifyReview(Integer reviewId, ModifyReviewRequest request){
        ReviewEntity review = validateReview(reviewId);
        hasAuthority(review, request.userId(), reviewId);

        review.setContent(request.content());
        review.setRating(request.rating());

        return GetReviewResponse.from(review);
    }

    public void deleteReview(Integer userId, Integer reviewId) {
        ReviewEntity review = validateReview(reviewId);
        hasAuthority(review, userId, reviewId);
        review.markDeleted();
    }

    private ReviewEntity validateReview(Integer reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND,
                        ErrorCode.REVIEW_NOT_FOUND.getMessage()));
    }

    private void hasAuthority(ReviewEntity review, Integer userId, Integer reviewId) {
        if (!(review.getId().equals(reviewId) && review.getUser().getId().equals(userId))) {
            throw new AppException(ErrorCode.UNAUTHORIZED_REVIEWER,
                    ErrorCode.UNAUTHORIZED_REVIEWER.getMessage());
        }
    }
}