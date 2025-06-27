package staysplit.hotel_reservation.reservation.domain.dto.reqeust;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {
    private Integer hotelId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<RoomReservationDto> rooms;
    private List<String> invitedEmails;
    private Boolean isSplitPayment;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomReservationDto {
        private Integer roomId;
        private Integer quantity;
    }
}