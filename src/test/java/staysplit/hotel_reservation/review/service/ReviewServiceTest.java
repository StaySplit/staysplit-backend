package staysplit.hotel_reservation.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.review.domain.dto.request.CreateReviewRequest;
import staysplit.hotel_reservation.review.domain.dto.request.ModifyReviewRequest;
import staysplit.hotel_reservation.review.domain.dto.response.CreateReviewResponse;
import staysplit.hotel_reservation.review.domain.dto.response.GetReviewResponse;
import staysplit.hotel_reservation.review.domain.entity.ReviewEntity;
import staysplit.hotel_reservation.review.repository.ReviewRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReviewSuccess() {
        // given
        CreateReviewRequest request = new CreateReviewRequest(1L, 100L, 200L, "좋았어요", 5);
        when(reviewRepository.existsByUserIdAndHotelId(200L, 100L)).thenReturn(false);
        when(reviewRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // when
        CreateReviewResponse response = reviewService.createReview(request);

        // then
        assertThat(response.content()).isEqualTo("좋았어요");
        verify(reviewRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("이미 존재하는 리뷰 작성 시 예외")
    void createReviewDuplicateFail() {
        // given
        CreateReviewRequest request = new CreateReviewRequest(1L, 100L, 200L, "좋았어요", 5);
        when(reviewRepository.existsByUserIdAndHotelId(200L, 100L)).thenReturn(true);

        // expect
        assertThatThrownBy(() -> reviewService.createReview(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 해당 호텔에 대한 리뷰를 작성하셨습니다.");
    }

    @Test
    @DisplayName("리뷰 조회 - 사용자 ID 기준")
    void getReviewByUserId() {
        ReviewEntity review = ReviewEntity.builder()
                .reviewId(1L).userId(200L).hotelId(100L).content("좋음").rating(4).build();
        Page<ReviewEntity> page = new PageImpl<>(List.of(review));
        when(reviewRepository.getReviewByUserId(eq(200L), any())).thenReturn(page);

        Page<GetReviewResponse> result = reviewService.getReviewByUserId(200L, PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).content()).isEqualTo("좋음");
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void modifyReviewSuccess() {
        // given
        ReviewEntity review = ReviewEntity.builder()
                .reviewId(1L).userId(200L).hotelId(100L).content("기존").rating(3).build();

        ModifyReviewRequest request = new ModifyReviewRequest(1L, 200L, 100L, "수정됨", 5);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        // when
        GetReviewResponse response = reviewService.modifyReview(request);

        // then
        assertThat(response.content()).isEqualTo("수정됨");
        assertThat(response.rating()).isEqualTo(5);
    }

    @Test
    @DisplayName("리뷰 수정 - 권한 없음 예외")
    void modifyReviewUnauthorized() {
        ReviewEntity review = ReviewEntity.builder()
                .reviewId(1L).userId(999L).hotelId(100L).content("기존").rating(3).build();

        ModifyReviewRequest request = new ModifyReviewRequest(1L, 200L, 100L, "수정됨", 5);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.modifyReview(request))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.UNAUTHORIZED_REVIEWER.getMessage());
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReviewSuccess() {
        ReviewEntity review = ReviewEntity.builder()
                .reviewId(1L).userId(200L).hotelId(100L).content("삭제용").rating(4).build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(200L, 1L);

        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    @DisplayName("리뷰 삭제 - 권한 없음 예외")
    void deleteReviewUnauthorized() {
        ReviewEntity review = ReviewEntity.builder()
                .reviewId(1L).userId(999L).hotelId(100L).content("삭제용").rating(4).build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.deleteReview(200L, 1L))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.UNAUTHORIZED_REVIEWER.getMessage());
    }

    @Test
    @DisplayName("리뷰 삭제 - 존재하지 않는 ID")
    void deleteReviewNotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(200L, 1L))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.REVIEW_NOT_FOUND.getMessage());
    }
}
