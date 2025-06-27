package staysplit.hotel_reservation.cartItem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import staysplit.hotel_reservation.cart.domain.entity.CartEntity;
import staysplit.hotel_reservation.cart.repository.CartItemRepository;
import staysplit.hotel_reservation.cart.repository.CartRepository;
import staysplit.hotel_reservation.cart.service.CartValidator;
import staysplit.hotel_reservation.cartItem.domain.dto.request.CreateCartItemRequest;
import staysplit.hotel_reservation.cartItem.domain.dto.response.CartItemDetailResponse;
import staysplit.hotel_reservation.cartItem.domain.entitiy.CartItemEntity;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.customer.repository.CustomerRepository;
import staysplit.hotel_reservation.hotel.entity.HotelEntity;
import staysplit.hotel_reservation.room.domain.RoomEntity;
import staysplit.hotel_reservation.room.repository.RoomRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock private CartItemRepository cartItemRepository;
    @Mock private CartRepository cartRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private CartValidator cartValidator;

    @InjectMocks
    private CartItemService cartItemService;

    @Mock private CustomerEntity customer;
    @Mock private CartEntity cart;
    @Mock private RoomEntity room;

    private final String email = "test@example.com";
    private final LocalDate checkIn = LocalDate.now().plusDays(1);
    private final LocalDate checkOut = checkIn.plusDays(2);

    @Test
    void modifyQuantity_success() {
        RoomEntity room = RoomEntity.builder()
                .id(1L)
                .price(1000)
                .hotel(HotelEntity.builder().hotelId(1L).name("Test Hotel").build())
                .build();

        CartItemEntity cartItem = CartItemEntity.builder()
                .cart(cart)
                .room(room)
                .quantity(2)
                .pricePerNight(1000)
                .subTotal(2000)
                .build();

        CartItemEntity spyCartItem = spy(cartItem);

        when(cartValidator.validateCustomer(email)).thenReturn(customer);
        when(cartValidator.validateCartItemById(1L)).thenReturn(spyCartItem);
        when(cart.getCustomer()).thenReturn(customer);
        when(customer.getId()).thenReturn(1L);

        CartItemDetailResponse response = cartItemService.modifyQuantity(1L, -1, email);

        verify(spyCartItem).changeQuantity(1);
        assertThat(response.quantity()).isEqualTo(1);
    }

    @Test
    void addToCart_success() {
        HotelEntity hotel = HotelEntity.builder()
                .hotelId(1L)
                .name("Test Hotel")
                .build();

        RoomEntity room = RoomEntity.builder()
                .id(1L)
                .price(1000)
                .hotel(hotel)
                .build();

        when(cartValidator.validateCustomer(email)).thenReturn(customer);
        when(cartValidator.validateCartByCustomer(customer)).thenReturn(cart);
        when(cartValidator.validateRoomById(1L)).thenReturn(room);
        when(cartItemRepository.findByCartAndRoomAndCheckInDateAndCheckOutDate(cart, room, checkIn, checkOut))
                .thenReturn(Optional.empty());

        CreateCartItemRequest request = new CreateCartItemRequest(1L, 2, checkIn, checkOut);

        CartItemDetailResponse response = cartItemService.addToCart(request, email);

        verify(cartItemRepository).save(any(CartItemEntity.class));
        assertThat(response.quantity()).isEqualTo(2);
        assertThat(response.pricePerNight()).isEqualTo(1000);
        assertThat(response.subTotal()).isEqualTo(2000);
    }

    @Test
    void addToCart_duplicate_throws() {
        CartItemEntity cartItem = mock(CartItemEntity.class);

        when(cartValidator.validateCustomer(email)).thenReturn(customer);
        when(cartValidator.validateCartByCustomer(customer)).thenReturn(cart);
        when(cartValidator.validateRoomById(1L)).thenReturn(room);
        when(cartItemRepository.findByCartAndRoomAndCheckInDateAndCheckOutDate(cart, room, checkIn, checkOut))
                .thenReturn(Optional.of(cartItem));

        CreateCartItemRequest request = new CreateCartItemRequest(1L, 2, checkIn, checkOut);

        assertThatThrownBy(() -> cartItemService.addToCart(request, email))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.ROOM_ALREADY_IN_CART.getMessage());
    }

    @Test
    void getCartItem_success() {
        CartItemEntity cartItem = CartItemEntity.builder()
                .quantity(2)
                .pricePerNight(500)
                .subTotal(1000)
                .build();

        when(cartValidator.validateCustomer(email)).thenReturn(customer);
        when(cartValidator.validateCartItemById(1L)).thenReturn(cartItem);

        CartItemDetailResponse response = cartItemService.getCartItem(1L, email);

        assertThat(response.quantity()).isEqualTo(2);
        assertThat(response.pricePerNight()).isEqualTo(500);
        assertThat(response.subTotal()).isEqualTo(1000);
    }

    @Test
    void deleteCartItem_success() {
        CartItemEntity cartItem = CartItemEntity.builder().build();

        when(cartValidator.validateCustomer(email)).thenReturn(customer);
        when(cartValidator.validateCartByCustomer(customer)).thenReturn(cart);
        when(cartValidator.validateCartItemById(1L)).thenReturn(cartItem);

        cartItemService.deleteCartItem(email, 1L);

        verify(cartItemRepository).delete(cartItem);
    }
}

