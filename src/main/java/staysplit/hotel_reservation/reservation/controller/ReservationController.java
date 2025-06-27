package staysplit.hotel_reservation.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import staysplit.hotel_reservation.common.entity.Response;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.customer.repository.CustomerRepository;
import staysplit.hotel_reservation.reservation.domain.entity.ReservationEntity;
import staysplit.hotel_reservation.reservation.domain.dto.reqeust.CreateReservationRequest;
import staysplit.hotel_reservation.reservation.domain.dto.response.ReservationDetailResponse;
import staysplit.hotel_reservation.reservation.domain.dto.response.ReservationListResponse;
import staysplit.hotel_reservation.reservation.service.ReservationService;
import staysplit.hotel_reservation.user.domain.entity.UserEntity;
import staysplit.hotel_reservation.user.repository.UserRepository;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;

import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    @GetMapping("/list")
    public Response<Page<ReservationListResponse>> getReservationList(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<ReservationListResponse> reservations = reservationService.getUserReservations(
                authentication.getName(), pageable);

        return Response.success(reservations);
    }

    @GetMapping("/{reservationId}")
    public Response<ReservationDetailResponse> getReservationDetail(
            Authentication authentication,
            @PathVariable Integer reservationId) {

        ReservationDetailResponse reservation = reservationService.getReservationDetail(
                authentication.getName(), reservationId);

        return Response.success(reservation);
    }

    @DeleteMapping("/{reservationId}")
    public Response<String> cancelReservation(
            Authentication authentication,
            @PathVariable Integer reservationId) {

        reservationService.cancelReservation(authentication.getName(), reservationId);

        return Response.success("예약이 성공적으로 취소되었습니다.");
    }

    @PostMapping("/temp")
    public Response<Map<String, String>> createTempReservation(
            @RequestBody CreateReservationRequest request,
            Authentication authentication) {

        String reservationKey = reservationService.createTempReservation(request, getCustomerId(authentication));

        return Response.success(Map.of("reservationKey", reservationKey));
    }

    @PostMapping("/confirm/{merchantPayKey}")
    public Response<ReservationDetailResponse> confirmReservation(
            @PathVariable String merchantPayKey,
            Authentication authentication) {

        ReservationEntity reservation = reservationService.saveReserveAfterPayment(merchantPayKey);
        ReservationDetailResponse response = reservation.toDetailResponse(getCustomerId(authentication));

        return Response.success(response);
    }

    private Integer getCustomerId(Authentication authentication) {
        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        CustomerEntity customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "고객 정보를 찾을 수 없습니다."));

        return customer.getId();
    }
}