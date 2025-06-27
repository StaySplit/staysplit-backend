package staysplit.hotel_reservation.cartItem.domain.dto.request;


import java.time.LocalDate;

public record CreateCartItemRequest (
        Long roomId,
        Integer quantity,
        LocalDate checkInDate,
        LocalDate checkOutDate
){
}
