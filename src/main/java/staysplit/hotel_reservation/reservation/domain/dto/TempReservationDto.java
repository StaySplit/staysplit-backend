package staysplit.hotel_reservation.reservation.domain.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempReservationDto {
    private Integer userId;
    private Integer hotelId;
    private String hotelName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalPrice;
    private List<RoomDetailDto> rooms;
    private List<String> invitedEmails;
    private Boolean isSplitPayment;
    private LocalDateTime createdAt;

    public LocalDateTime getExpiresAt() {
        return createdAt.plusMinutes(15);
    }
}