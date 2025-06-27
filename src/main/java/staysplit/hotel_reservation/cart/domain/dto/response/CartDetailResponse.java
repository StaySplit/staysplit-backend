package staysplit.hotel_reservation.cart.domain.dto.response;


import staysplit.hotel_reservation.cart.domain.entity.CartEntity;
import staysplit.hotel_reservation.cartItem.domain.dto.response.CartItemDetailResponse;

import java.util.List;

public record CartDetailResponse(
        Long cartId,
        Long customerId,
        List<CartItemDetailResponse> rooms,
        Long totalPrice
) {
    public static CartDetailResponse from(CartEntity cart) {

        List<CartItemDetailResponse> items = cart.getCartItemList().stream()
                .map(CartItemDetailResponse::from)
                .toList();

        long totalPrice = cart.getCartItemList().stream()
                .mapToLong(item -> item.getSubTotal())
                .sum();

        return new CartDetailResponse(
                cart.getId(),
                cart.getCustomer().getId(),
                items,
                totalPrice
        );
    }
}
