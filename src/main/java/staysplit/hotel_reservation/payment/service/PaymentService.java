package staysplit.hotel_reservation.payment.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.customer.repository.CustomerRepository;
import staysplit.hotel_reservation.payment.domain.dto.request.CreatePaymentRequest;
import staysplit.hotel_reservation.payment.domain.dto.response.CreatePaymentResponse;
import staysplit.hotel_reservation.payment.domain.dto.response.GetPaymentResponse;
import staysplit.hotel_reservation.payment.domain.entity.PaymentEntity;
import staysplit.hotel_reservation.payment.repository.PaymentRepository;
import staysplit.hotel_reservation.reservation.domain.entity.ReservationEntity;
import staysplit.hotel_reservation.reservation.domain.entity.ReservationParticipantEntity;
import staysplit.hotel_reservation.reservation.domain.enums.PaymentStatus;
import staysplit.hotel_reservation.reservation.domain.enums.ReservationStatus;
import staysplit.hotel_reservation.reservation.reposiotry.ReservationParticipantRepository;
import staysplit.hotel_reservation.reservation.reposiotry.ReservationRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final IamportClient iamportClient;
    private final ReservationParticipantRepository participantRepository;
    private final ReservationRepository reservationRepository;

    public CreatePaymentResponse verifyAndCreatePayment(CreatePaymentRequest request)
            throws IamportResponseException, IOException {

        if (paymentRepository.existsByImpUid(request.impUid())) {
            throw new AppException(ErrorCode.DUPLICATE_PAYMENT,
                    ErrorCode.DUPLICATE_PAYMENT.getMessage());
        }

        // 1. imp_uid로 아임포트 서버에 결제 정보 요청
        IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.impUid());
        Payment payment = iamportResponse.getResponse();

        // 2. 검증 로직
        if (payment == null) {
            throw new AppException(ErrorCode.INVALID_PAYMENT,
                    ErrorCode.INVALID_PAYMENT.getMessage());
        }

        if (payment.getAmount().intValue() != request.amount()) {
            throw new AppException(ErrorCode.PAYMENT_AMOUNT_MISMATCH,
                    ErrorCode.PAYMENT_AMOUNT_MISMATCH.getMessage());
        }

        if (!"paid".equals(payment.getStatus())) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_STATUS,
                    ErrorCode.INVALID_PAYMENT_STATUS.getMessage());
        }

        // 결제 성공 시 Reservation Status 변경
        changeReservationStatus(request.customerId(), request.reservationId(), request.isSplitPayment());

        // 3. 결제 저장
        return createPayment(request);
    }

    @Transactional
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {

        CustomerEntity customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND,
                        ErrorCode.USER_NOT_FOUND.getMessage()));
//        HotelEntity hotel = reservationRepository.findById(request.reservationId())
//                .orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND,
//                ErrorCode.RESERVATION_NOT_FOUND.getMessage()));


        PaymentEntity payment = PaymentEntity.builder()
                                .impUid(request.impUid())
                                .customer(customer)
                                .payMethod(request.payMethod())
                                .payName(request.payName())
                                .amount(request.amount())
                                .status(request.status())
                                .paidAt(LocalDateTime.now())
                                .build();


        paymentRepository.save(payment);
        return CreatePaymentResponse.from(payment);
    }

/*
    @Transactional(readOnly = true)
    public List<CreatePaymentResponse> getPaymentsByCustomer(Integer customerId) {
        return paymentRepository.findByCustomerId(customerId).stream()
                .map(CreatePaymentResponse::from)
                .collect(Collectors.toList());
    }
*/

    @Transactional(readOnly = true)
    public GetPaymentResponse getPaymentsByReservationId(Integer reservationId) {
        PaymentEntity payment = paymentRepository.findByReservationId(reservationId);
        if (payment == null) {
            throw new AppException(ErrorCode.INVALID_PAYMENT, ErrorCode.INVALID_PAYMENT.getMessage());
        }
        return GetPaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public Page<GetPaymentResponse> getPaymentsByCustomerId(Integer customerId, Pageable page) {
        Page<PaymentEntity> payment = paymentRepository.findByCustomerId(customerId, page);
        if (payment.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_PAYMENT, ErrorCode.INVALID_PAYMENT.getMessage());
        }
        return payment.map(GetPaymentResponse::from);

    }


    private void changeReservationStatus(Integer customerId, Integer reservationId, boolean isSplitPayment) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND, ErrorCode.RESERVATION_NOT_FOUND.getMessage()));



        if (reservation.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new AppException(ErrorCode.EXPIRED_RESERVATION, ErrorCode.EXPIRED_RESERVATION.getMessage());
        }

        List<ReservationParticipantEntity> participants = participantRepository.findByReservationId(reservationId);
        boolean allCompleted = participants.stream().allMatch(p -> p.getPaymentStatus() == PaymentStatus.CONFIRMED);

        if (allCompleted) {
            reservation.updateStatus(ReservationStatus.CONFIRMED);
        }
    }
}