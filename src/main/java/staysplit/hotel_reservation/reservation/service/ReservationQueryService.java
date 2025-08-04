package staysplit.hotel_reservation.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.customer.service.CustomerValidator;
import staysplit.hotel_reservation.provider.domain.entity.ProviderEntity;
import staysplit.hotel_reservation.provider.service.ProviderValidator;
import staysplit.hotel_reservation.reservation.dto.response.ReservationDetailResponse;
import staysplit.hotel_reservation.reservation.dto.response.ReservationInfoListResponse;
import staysplit.hotel_reservation.reservation.dto.response.ReservationListResponse;
import staysplit.hotel_reservation.reservation.domain.entity.ReservationEntity;
import staysplit.hotel_reservation.reservation.domain.enums.ReservationStatus;
import staysplit.hotel_reservation.reservation.dto.response.ReservationListResponseForProvider;
import staysplit.hotel_reservation.reservation.mapper.ReservationMapper;
import staysplit.hotel_reservation.reservation.reposiotry.ReservationRepository;
import staysplit.hotel_reservation.reservation.reposiotry.search.ReservationSearchConditionForProviders;
import staysplit.hotel_reservation.reservedRoom.repository.ReservedRoomRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationRepository reservationRepository;
    private final ReservedRoomRepository reservedRoomRepository;
    private final CustomerValidator customerValidator;
    private final ProviderValidator providerValidator;
    private final ReservationMapper mapper;

    public Page<ReservationInfoListResponse> findAllReservationsByCustomer(String email, Pageable pageable) {
        CustomerEntity customer = customerValidator.validateCustomerByEmail(email);

        Page<ReservationEntity> reservations = reservationRepository
                .findReservationsWithRoomByCustomerId(customer.getId(), pageable);

        return reservations.map(mapper::toReservationListResponse);
    }

    @Deprecated
    public Page<ReservationListResponse> findAllReservationsByCustomerWithFilter(String email, ReservationStatus status,
                                                                       LocalDate afterDate, Pageable pageable) {
        CustomerEntity customer = customerValidator.validateCustomerByEmail(email);
        return reservationRepository.findReservationsByCustomerWithFilters(customer.getId(), status, afterDate, pageable)
                .map(mapper::toListResponse);
    }

    public ReservationDetailResponse findReservationByReservationId(String email, Integer reservationId) {
        CustomerEntity customer = customerValidator.validateCustomerByEmail(email);
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND,
                        ErrorCode.RESERVATION_KEY_NOT_FOUND.getMessage()));

        return mapper.toReservationDetailResponse(reservation);
    }

    public Page<ReservationListResponseForProvider> findAllReservationsToHotel(String email, Pageable pageable,
                                                                               ReservationSearchConditionForProviders conditions) {
        ProviderEntity provider = providerValidator.validateProvider(email);
        Page<ReservationEntity> reservations = reservationRepository.findReservationsByHotelWithFilters(provider.getId(), pageable, conditions);
        return reservations.map(mapper::toListResponseForProvider);
    }
}
