package staysplit.hotel_reservation.provider.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import staysplit.hotel_reservation.hotel.entity.HotelEntity;
import staysplit.hotel_reservation.user.domain.entity.UserEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProviderEntity {

    @Id
    @Column(name = "provider_id")
    private Integer id;

    @Column(name = "nickname")
    private String nickname;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;

    public void addHotel(HotelEntity hotel) {
        this.hotel = hotel;
    }
}