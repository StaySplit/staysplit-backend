package staysplit.hotel_reservation.reservation.domain.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInvitationDto {
    private Integer reservationId;
    private String reservationNumber;
    private String inviterEmail;
    private String inviterName;
    private String hotelName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer splitAmount;
    private String invitationToken;
    private LocalDateTime expiresAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}