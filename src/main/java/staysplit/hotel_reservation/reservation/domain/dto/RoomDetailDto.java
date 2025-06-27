package staysplit.hotel_reservation.reservation.domain.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDetailDto {
    private Integer roomId;
    private String roomType;
    private Integer quantity;
    private Integer pricePerNight;
    private Integer nights;
    private Integer subtotalPrice;

    public Integer calculateSubtotal() {
        return pricePerNight * nights * quantity;
    }
}