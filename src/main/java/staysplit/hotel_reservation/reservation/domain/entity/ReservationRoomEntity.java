package staysplit.hotel_reservation.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import staysplit.hotel_reservation.room.domain.RoomEntity;

@Entity
@Table(name = "reservation_room")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReservationRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "price_per_night")
    private Integer pricePerNight;

    @Column(nullable = false)
    private Integer nights;

    @Column(name = "subtotal_price")
    private Integer subtotalPrice;

    public void calculateSubtotal() {
        this.subtotalPrice = pricePerNight * quantity * nights;
    }
}