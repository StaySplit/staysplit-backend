package staysplit.hotel_reservation.review.domain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import staysplit.hotel_reservation.common.entity.Response;
import staysplit.hotel_reservation.review.domain.dto.request.CreateReviewRequest;
import staysplit.hotel_reservation.review.domain.dto.request.ModifyReviewRequest;
import staysplit.hotel_reservation.review.domain.dto.response.CreateReviewResponse;
import staysplit.hotel_reservation.review.domain.dto.response.GetReviewResponse;
import staysplit.hotel_reservation.review.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    @Autowired
    private final ReviewService reviewService;

    @PostMapping("/create-review")
    public Response<CreateReviewResponse> createReview(@RequestBody CreateReviewRequest request){
        CreateReviewResponse response = reviewService.createReview(request);
        return Response.success(response);
    }

    @GetMapping("/user/{userId}")
    public Page<GetReviewResponse> getReviewByUserId(@PathVariable Long userId, @PageableDefault(size = 10, sort = "reviewId", direction = Sort.Direction.ASC) Pageable pageable) {
        return reviewService.getReviewByUserId(userId, pageable);
    }

    @GetMapping("/hotel/{hotelId}")
    public Page<GetReviewResponse> getReviewByHotelId(@PathVariable Long hotelId, @PageableDefault(size = 10, sort = "reviewId", direction = Sort.Direction.ASC) Pageable pageable) {
        return reviewService.getReviewByHotelId(hotelId, pageable);
    }

    @DeleteMapping("/delete")
    public Response<String> deleteReview(@RequestParam Long userId, @RequestParam Long reviewId) {
        reviewService.deleteReview(userId, reviewId);
        return Response.success("리뷰가 삭제되었습니다.");
    }

    @PutMapping("/modify")
    public Response<String> modifyReview(@RequestBody ModifyReviewRequest request) {
        reviewService.modifyReview(request);
        return Response.success("리뷰가 수정되었습니다.");
    }
}
