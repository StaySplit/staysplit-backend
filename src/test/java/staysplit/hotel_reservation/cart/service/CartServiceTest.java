package staysplit.hotel_reservation.cart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import staysplit.hotel_reservation.cart.domain.dto.response.CartDetailResponse;
import staysplit.hotel_reservation.cart.domain.dto.response.CreateCartResponse;
import staysplit.hotel_reservation.cart.domain.entity.CartEntity;
import staysplit.hotel_reservation.cart.repository.CartRepository;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.user.domain.entity.UserEntity;
import staysplit.hotel_reservation.user.domain.enums.LoginSource;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartValidator cartValidator;

    @InjectMocks
    private CartService cartService;

    private final String email = "test@example.com";

    private CustomerEntity customer;
    private CartEntity cart;

    @BeforeEach
    void setUp() {
        Object Role;
        UserEntity user = UserEntity.builder()
                .id(1L)
                .email(email)
                .role(staysplit.hotel_reservation.user.domain.enums.Role.CUSTOMER)
                .loginSource(LoginSource.LOCAL)
                .build();

        customer = CustomerEntity.builder()
                .id(1L)
                .user(user)
                .name("손님")
                .birthdate(LocalDate.of(1990, 1, 1))
                .nickname("여행객")
                .build();

        cart = CartEntity.builder()
                .id(100L)
                .customer(customer)
                .cartItemList(new ArrayList<>())
                .build();
    }

    @Test
    void getMyCart_success() {
        when(cartValidator.validateCustomer(email)).thenReturn(customer);
        when(cartValidator.validateCartByCustomer(customer)).thenReturn(cart);

        CartDetailResponse response = cartService.getMyCart(email);

        assertThat(response.cartId()).isEqualTo(100L);
    }

    @Test
    void createCart_success() {
        when(cartValidator.validateCustomer(email)).thenReturn(customer);

        CartEntity savedCart = CartEntity.builder()
                .id(123L)
                .customer(customer)
                .build();

        when(cartRepository.save(any(CartEntity.class))).thenReturn(savedCart);

        CreateCartResponse response = cartService.createCart(email);

        verify(cartRepository).save(any(CartEntity.class));
        assertThat(response).isNotNull();
        assertThat(response.cartId()).isEqualTo(123L);
    }

    @Test
    void deleteCart_success() {
        when(cartValidator.validateCustomer(email)).thenReturn(customer);
        when(cartValidator.validateCartByCustomer(customer)).thenReturn(cart);

        cartService.deleteCart(email);

        verify(cartRepository).delete(cart);
    }
}

