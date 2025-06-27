package staysplit.hotel_reservation.reservation.domain.dto.response;

import lombok.*;
import staysplit.hotel_reservation.reservation.domain.dto.RoomDetailDto;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ReservationDetailResponse {
    private Integer reservationId;
    private String reservationNumber;
    private HotelInfo hotel;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalPrice;
    private Integer pricePaid;
    private String status;
    private Boolean isOwner;
    private OwnerInfo owner;
    private List<ParticipantInfo> participants;
    private List<RoomDetailDto> rooms;

    @Getter
    @Builder
    public static class HotelInfo {
        private Integer hotelId;
        private String name;
        private String address;
        private String imageUrl;
    }

    @Getter
    @Builder
    public static class OwnerInfo {
        private Integer userId;
        private String email;
        private String name;
    }

    @Getter
    @Builder
    public static class ParticipantInfo {
        private String email;
        private String name;
        private Integer splitAmount;
        private String paymentStatus;
    }
}