package staysplit.hotel_reservation.hotel.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.hotel.dto.request.*;
import staysplit.hotel_reservation.hotel.dto.response.*;
import staysplit.hotel_reservation.hotel.entity.HotelEntity;
import staysplit.hotel_reservation.hotel.mapper.HotelMapper;
import staysplit.hotel_reservation.hotel.repository.HotelRepository;
import staysplit.hotel_reservation.photo.service.PhotoUrlBuilder;
import staysplit.hotel_reservation.provider.domain.entity.ProviderEntity;
import staysplit.hotel_reservation.provider.repository.ProviderRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final ProviderRepository providerRepository;
    private final PhotoUrlBuilder photoUrlBuilder;
    private final HotelMapper mapper;

    public CreateHotelResponse createHotel(CreateHotelRequest request, String providerEmail) {
        ProviderEntity provider = validateProvider(providerEmail);

        HotelEntity hotel = HotelEntity.builder()
                .provider(provider)
                .name(request.name())
                .address(request.address())
                .longitude(BigDecimal.valueOf(request.longitude()))
                .latitude(BigDecimal.valueOf(request.latitude()))
                .description(request.description())
                .starLevel(request.starLevel())
                .build();

        provider.addHotel(hotel);
        hotelRepository.save(hotel);

        return mapper.toCreateHotelResponse(hotel);
    }

    @Transactional(readOnly = true)
    public GetHotelDetailResponse getHotelDetails(Integer hotelId) {
        HotelEntity hotel = getHotelOrThrow(hotelId);
        return mapper.toDetailResponse(hotel, photoUrlBuilder);
    }

    @Transactional(readOnly = true)
    public Page<GetHotelListResponse> getHotelList(Pageable pageable) {
        Page<HotelEntity> hotelPage = hotelRepository.findAll(pageable);
        return hotelPage.map(hotel -> mapper.toListResponse(hotel, photoUrlBuilder));
    }

    public GetHotelDetailResponse updateHotel(Integer hotelId, UpdateHotelRequest request, String providerEmail) {
        ProviderEntity provider = validateProvider(providerEmail);
        HotelEntity hotel = getHotelOrThrow(hotelId);

        verifyOwnership(hotel, provider);

        hotel.updateHotel(request);
        return mapper.toDetailResponse(hotel, photoUrlBuilder);
    }

    @Transactional(readOnly = true)
    public Page<GetHotelListResponse> searchHotels(HotelSearchRequest request, Pageable pageable) {
        double centerLat = request.lat();
        double centerLon = request.lon();

        // 5km 반경 기준 박스 범위 대략 계산 (1도 ≒ 111km)
        double kmPerDegree = 111;
        double delta = 5.0 / kmPerDegree;

        double minLat = centerLat - delta;
        double maxLat = centerLat + delta;
        double minLon = centerLon - delta;
        double maxLon = centerLon + delta;

        List<HotelEntity> candidates = hotelRepository.searchByLocation(minLat, maxLat, minLon, maxLon);

        // 거리 계산 + 예약 가능 여부 필터
        LocalDate checkIn = LocalDate.parse(request.checkInDate());
        LocalDate checkOut = LocalDate.parse(request.checkOutDate());

        List<HotelEntity> filtered = candidates.stream()
                .map(hotel -> Map.entry(hotel,
                        haversine(hotel.getLatitude().doubleValue(), hotel.getLongitude().doubleValue(), centerLat, centerLon)))
                .filter(entry -> entry.getValue() <= 5)
                .filter(entry -> {
                    HotelEntity hotel = entry.getKey();
                    return hotel.getRooms().stream()
                            .anyMatch(room ->
                                    room.getMaxOccupancy() >= request.guestCount() &&
                                    room.isAvailableDuring(checkIn, checkOut));
                })
                .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .toList();

        // 페이징 수작업 적용
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());

        List<HotelEntity> paged = filtered.subList(start, end);

        return new PageImpl<>(paged, pageable, filtered.size())
                .map(hotel -> GetHotelListResponse.toDto(hotel, photoUrlBuilder));
    }

    public DeleteHotelResponse deleteHotel(Integer hotelId, String email) {
        ProviderEntity provider = validateProvider(email);
        HotelEntity hotel = getHotelOrThrow(hotelId);

        verifyOwnership(hotel, provider);

        hotelRepository.delete(hotel);
        provider.addHotel(null);
        return new DeleteHotelResponse("호텔이 성공적으로 삭제되었습니다.", hotelId);
    }

    private ProviderEntity validateProvider(String email) {
        return providerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));
    }

    private HotelEntity getHotelOrThrow(Integer hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "호텔을 찾을 수 없습니다."));
    }

    private void verifyOwnership(HotelEntity hotel, ProviderEntity provider) {
        if (!hotel.getProvider().getId().equals(provider.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이 호텔에 대한 권한이 없습니다.");
        }
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}