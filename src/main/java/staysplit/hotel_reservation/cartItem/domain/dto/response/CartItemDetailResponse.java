package staysplit.hotel_reservation.cartItem.domain.dto.response;

import staysplit.hotel_reservation.cartItem.domain.entitiy.CartItemEntity;

import java.time.LocalDate;

public record CartItemDetailResponse(
        Long cartId,
        Long hotelId,
        Long roomId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer quantity,
        Integer pricePerNight,
        Integer subTotal
) {
    public static CartItemDetailResponse from(CartItemEntity cartItem) {
        return new CartItemDetailResponse(
                cartItem.getCart().getId(),
                cartItem.getRoom().getHotel().getHotelId(),
                cartItem.getRoom().getId(),
                cartItem.getCheckInDate(),
                cartItem.getCheckOutDate(),
                cartItem.getQuantity(),
                cartItem.getPricePerNight(),
                cartItem.getSubTotal());
    }
}
