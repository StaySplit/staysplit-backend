package staysplit.hotel_reservation.payment.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import staysplit.hotel_reservation.payment.domain.dto.request.CreatePaymentRequest;
import staysplit.hotel_reservation.payment.domain.dto.response.CreatePaymentResponse;
import staysplit.hotel_reservation.payment.domain.dto.response.ErrorResponse;
import staysplit.hotel_reservation.payment.domain.dto.response.GetPaymentResponse;
import staysplit.hotel_reservation.payment.service.PaymentService;
import staysplit.hotel_reservation.common.entity.Response;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    private final IamportClient iamportClient;

    @PostMapping("/verify")
    public Response<?> verifyAndCreate(@RequestBody CreatePaymentRequest request) {
        try {
            CreatePaymentResponse response = paymentService.verifyAndCreatePayment(request);
            return Response.success(response);
        } catch (IamportResponseException e) {
            return Response.error(new ErrorResponse("아임포트 API 오류: " + e.getMessage()));
        } catch (IllegalStateException e) {
            return Response.error(new ErrorResponse("결제 검증 실패: " + e.getMessage()));
        } catch (Exception e) {
            return Response.error(new ErrorResponse("결제 검증 중 알 수 없는 오류가 발생했습니다."));
        }
    }

/*    @GetMapping("/customers/{customerId}")
    public Response<List<CreatePaymentResponse>> getPaymentsByCustomer(@PathVariable Integer customerId) {
        List<CreatePaymentResponse> payments = paymentService.getPaymentsByCustomer(customerId);
        return Response.success(payments);
    }*/

    @GetMapping("/reservations/{reservationId}")
    public Response<GetPaymentResponse> getPaymentsByReservationId(@PathVariable Integer reservationId) {
        GetPaymentResponse payments = paymentService.getPaymentsByReservationId(reservationId);
        return Response.success(payments);
    }

    @GetMapping("/customerId/{customerId}")
    public Response<Page<GetPaymentResponse>> getPaymentsByCustomerId(@PathVariable Integer customerId, Pageable page) {
        return Response.success(paymentService.getPaymentsByCustomerId(customerId, page));
    }

}
