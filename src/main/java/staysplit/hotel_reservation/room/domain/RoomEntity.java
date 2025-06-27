package staysplit.hotel_reservation.room.domain;

import jakarta.persistence.*;
import lombok.*;
import staysplit.hotel_reservation.hotel.entity.HotelEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;

    @Column(nullable = false)
    private String roomType;

    private Integer quantity;

    private String photoUrl;

    @Column(nullable = false)
    private Integer maxOccupancy;

    @Column(nullable = false)
    private Integer price;

    private String description;

    public void updateRoom(String roomType, String photoUrl, Integer maxOccupancy,
                           Integer price, String description, Integer quantity) {
        this.roomType = roomType;
        this.photoUrl = photoUrl;
        this.quantity = quantity;
        this.maxOccupancy = maxOccupancy;
        this.price = price;
        this.description = description;
    }

}
