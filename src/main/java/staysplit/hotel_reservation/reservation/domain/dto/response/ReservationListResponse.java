package staysplit.hotel_reservation.reservation.domain.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter
@Builder
public class ReservationListResponse {
    private Integer reservationId;
    private String reservationNumber;
    private String hotelName;
    private String hotelImageUrl;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalPrice;
    private String status;
    private Integer totalParticipants;
    private Boolean isOwner;
    private Boolean isPaid;
}