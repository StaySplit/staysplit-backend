package staysplit.hotel_reservation.cart.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import staysplit.hotel_reservation.cartItem.domain.entitiy.CartItemEntity;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemEntity> cartItemList = new ArrayList<>();

    public CartEntity(CustomerEntity customer) {
        this.customer = customer;
    }
}
