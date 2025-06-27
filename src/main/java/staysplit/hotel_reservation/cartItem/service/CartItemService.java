package staysplit.hotel_reservation.cartItem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import staysplit.hotel_reservation.cart.service.CartValidator;
import staysplit.hotel_reservation.cartItem.domain.dto.request.CreateCartItemRequest;
import staysplit.hotel_reservation.cartItem.domain.dto.response.CartItemDetailResponse;
import staysplit.hotel_reservation.cart.domain.entity.CartEntity;
import staysplit.hotel_reservation.cartItem.domain.entitiy.CartItemEntity;
import staysplit.hotel_reservation.cart.repository.CartItemRepository;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.room.domain.RoomEntity;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartValidator cartValidator;

    // 카트에 있는 방 수량 변경
    public CartItemDetailResponse modifyQuantity(Long cartItemId, int delta, String email) {
        CustomerEntity customer = cartValidator.validateCustomer(email);
        CartItemEntity cartItem = cartValidator.validateCartItemById(cartItemId);

        // customer가 권한이 있는지 확인
        if (!customer.getId().equals(cartItem.getCart().getCustomer().getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_CUSTOMER, ErrorCode.UNAUTHORIZED_CUSTOMER.getMessage());
        }

        int updated = cartItem.getQuantity() + delta;

        if (updated < 0) {
            throw new IllegalStateException("수량이 음수가 될 수 없습니다");
        }

        if (updated == 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }

        cartItem.changeQuantity(updated);
        return CartItemDetailResponse.from(cartItem);
    }

    // 카트에서 방 삭제하기
    public void deleteCartItem(String email, Long cartItemId) {
        CustomerEntity customer = cartValidator.validateCustomer(email);
        CartEntity cart = cartValidator.validateCartByCustomer(customer);
        CartItemEntity cartItem = cartValidator.validateCartItemById(cartItemId);
        cartItemRepository.delete(cartItem);
    }

    // 카트에 추가하거나 수량 올리기
    public CartItemDetailResponse addToCart(CreateCartItemRequest request, String email) {
        CustomerEntity customer = cartValidator.validateCustomer(email);
        CartEntity cart = cartValidator.validateCartByCustomer(customer);
        RoomEntity room = cartValidator.validateRoomById(request.roomId());

        // 방이 이미 있으면 에러
        Optional<CartItemEntity> existingCartItem = cartItemRepository
                .findByCartAndRoomAndCheckInDateAndCheckOutDate(cart, room, request.checkInDate(), request.checkOutDate());

        if (existingCartItem.isPresent()) {
            throw new AppException(ErrorCode.ROOM_ALREADY_IN_CART, ErrorCode.ROOM_ALREADY_IN_CART.getMessage());
        }

        // if it is a new room and chek in checkout date, create a new entity
        // TODO: Reservation 테이블을 보고 checkin checkout와 겹치는 방 수를 확인 후 남아있는 방 수량을 개산한 후 room.quantity에서 수량 제거
        // 요청된 수량과 같거나 많으면 허락
        CartItemEntity cartItem = CartItemEntity.builder()
                .cart(cart)
                .room(room)
                .quantity(request.quantity())
                .checkInDate(request.checkInDate())
                .checkOutDate(request.checkOutDate())
                .pricePerNight(room.getPrice())
                .subTotal(request.quantity() * room.getPrice())
                .build();

        cartItemRepository.save(cartItem);
        return CartItemDetailResponse.from(cartItem);
    }

    @Transactional(readOnly = true)
    public CartItemDetailResponse getCartItem(Long cartItemId, String email) {
        CustomerEntity customer = cartValidator.validateCustomer(email);
        CartItemEntity cartItem = cartValidator.validateCartItemById(cartItemId);
        return CartItemDetailResponse.from(cartItem);
    }

}
