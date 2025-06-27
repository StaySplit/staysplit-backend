package staysplit.hotel_reservation.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import staysplit.hotel_reservation.cart.domain.entity.CartEntity;
import staysplit.hotel_reservation.cartItem.domain.entitiy.CartItemEntity;
import staysplit.hotel_reservation.cart.repository.CartItemRepository;
import staysplit.hotel_reservation.cart.repository.CartRepository;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.customer.repository.CustomerRepository;
import staysplit.hotel_reservation.room.domain.RoomEntity;
import staysplit.hotel_reservation.room.repository.RoomRepository;

@Component
@RequiredArgsConstructor
public class CartValidator {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;


    public RoomEntity validateRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND, ErrorCode.ROOM_NOT_FOUND.getMessage()));
    }

    public CartItemEntity validateCartItemById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND, ErrorCode.CART_ITEM_NOT_FOUND.getMessage()));
    }


    public CartEntity validateCartByCustomer(CustomerEntity customer) {
        return cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND, ErrorCode.CART_NOT_FOUND.getMessage()));
    }

    public CustomerEntity validateCustomer(String email) {
        return customerRepository.findByUserByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));
    }
}
