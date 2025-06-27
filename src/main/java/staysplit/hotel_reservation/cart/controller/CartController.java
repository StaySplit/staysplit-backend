package staysplit.hotel_reservation.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import staysplit.hotel_reservation.cart.domain.dto.response.CartDetailResponse;
import staysplit.hotel_reservation.cart.domain.dto.response.CreateCartResponse;
import staysplit.hotel_reservation.cart.service.CartService;
import staysplit.hotel_reservation.common.entity.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    public final CartService cartService;

    @GetMapping("/my")
    public Response<CartDetailResponse> getMyCart(Authentication authentication) {
        CartDetailResponse myCart = cartService.getMyCart(authentication.getName());
        return Response.success(myCart);
    }

    @PostMapping("/my")
    public Response<CreateCartResponse> createCart(Authentication authentication) {
        CreateCartResponse newCart = cartService.createCart(authentication.getName());
        return Response.success(newCart);
    }

    @DeleteMapping("/my")
    public Response<String> deleteCart(Authentication authentication) {
        cartService.deleteCart(authentication.getName());
        return Response.success("장바구니를 삭제했습니다");
    }
}
