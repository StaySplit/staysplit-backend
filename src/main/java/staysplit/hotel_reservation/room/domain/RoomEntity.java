package staysplit.hotel_reservation.room.domain;

import jakarta.persistence.*;
import lombok.*;
import staysplit.hotel_reservation.hotel.entity.HotelEntity;

@Entity
@Table(name = "room")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    HotelEntity hotel;

    @Column(name = "room_type", nullable = false)
    String roomType;

    @Column(name = "max_occupancy", nullable = false)
    Integer maxOccupancy;

    @Column(nullable = false)
    Integer price;

    String description;

    public void updateRoom(String roomType, Integer maxOccupancy, Integer price, String description) {
        this.roomType = roomType;
        this.maxOccupancy = maxOccupancy;
        this.price = price;
        this.description = description;
    }

    public void changeRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void changePrice(Integer price) {
        this.price = price;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeMaxOccupancy(Integer maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }
}