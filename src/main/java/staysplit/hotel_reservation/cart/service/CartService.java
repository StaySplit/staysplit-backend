package staysplit.hotel_reservation.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import staysplit.hotel_reservation.cart.domain.dto.response.CartDetailResponse;
import staysplit.hotel_reservation.cart.domain.dto.response.CreateCartResponse;
import staysplit.hotel_reservation.cart.domain.entity.CartEntity;
import staysplit.hotel_reservation.cart.repository.CartRepository;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartValidator cartValidator;


    // 사용자의 장바구니 조회
    @Transactional(readOnly = true)
    public CartDetailResponse getMyCart(String email) {
        CustomerEntity customer = cartValidator.validateCustomer(email);
        CartEntity cart = cartValidator.validateCartByCustomer(customer);

        return CartDetailResponse.from(cart);
    }

    // 장바구니 생성
    public CreateCartResponse createCart(String email) {
        CustomerEntity customer = cartValidator.validateCustomer(email);
        CartEntity cart = new CartEntity(customer);
        cartRepository.save(cart);
        return new CreateCartResponse(cart.getId());
    }

    // 장바구니 삭제
    public void deleteCart(String email) {
        CustomerEntity customer = cartValidator.validateCustomer(email);
        CartEntity cart = cartValidator.validateCartByCustomer(customer);
        cartRepository.delete(cart);
    }

}
