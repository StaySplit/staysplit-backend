package staysplit.hotel_reservation.cartItem.domain.entitiy;

import jakarta.persistence.*;
import lombok.*;
import staysplit.hotel_reservation.cart.domain.entity.CartEntity;
import staysplit.hotel_reservation.room.domain.RoomEntity;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;

    @OneToOne
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer pricePerNight;

    @Column(nullable = false)
    private Integer subTotal;

    public void changeQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void subtractQuantity(int quantity) {
        if (this.quantity < quantity) {
            throw new IllegalStateException("현재 수량보다 더 많이 차감할 수 없습니다.");
        }
        this.quantity -= quantity;
    }

    public Integer getSubtotal(int quantity) {
        return quantity * pricePerNight;
    }

    public void updateSubtotal() {
        this.subTotal = this.quantity * this.pricePerNight;
    }
}
