package staysplit.hotel_reservation.cartItem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import staysplit.hotel_reservation.cartItem.domain.dto.request.CreateCartItemRequest;
import staysplit.hotel_reservation.cartItem.domain.dto.request.UpdateCartItemQuantityRequest;
import staysplit.hotel_reservation.cartItem.domain.dto.response.CartItemDetailResponse;
import staysplit.hotel_reservation.cartItem.service.CartItemService;
import staysplit.hotel_reservation.common.entity.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts/items")
public class CartItemController {
    private final CartItemService cartItemService;

    @PostMapping()
    public Response<CartItemDetailResponse> addNewItem(@Valid @RequestBody CreateCartItemRequest request, Authentication authentication) {
        CartItemDetailResponse cartItemDetailResponse = cartItemService.addToCart(request, authentication.getName());
        return Response.success(cartItemDetailResponse);
    }

    @GetMapping("/{cartItemId}")
    public Response<CartItemDetailResponse> getCartItem(@PathVariable Long cartItemId, Authentication authentication) {
        CartItemDetailResponse cartItem = cartItemService.getCartItem(cartItemId, authentication.getName());
        return Response.success(cartItem);
    }

    // 수량 수정
    @PutMapping("/{cartItemId}")
    public Response<?> modifyCartItemQuantity(@PathVariable Long cartItemId,
                                              @Valid @RequestBody UpdateCartItemQuantityRequest request,
                                              Authentication authentication) {
        CartItemDetailResponse response = cartItemService.modifyQuantity(cartItemId, request.quantity(), authentication.getName());
        if (response == null) {
            return Response.success("장바구니에서 삭제되었습니다.");
        }
        return Response.success(response);
    }
}
