package staysplit.hotel_reservation.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.hotel.entity.HotelEntity;
import staysplit.hotel_reservation.reservation.domain.enums.ReservationStatus;
import staysplit.hotel_reservation.reservedRoom.entity.ReservedRoomEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false )
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;

    @Column(nullable = false, unique = true, length = 50)
    private String reservationNumber;

    @Column(nullable = false)
    private Integer nights;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Builder.Default
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservedRoomEntity> reservedRooms = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationParticipantEntity> participants = new ArrayList<>();

    private Integer totalPrice;

    @Column(nullable = false)
    @Builder.Default
    private Integer pricePaid = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;


    public void addReservedRoom(ReservedRoomEntity reservedRoom) {
        reservedRooms.add(reservedRoom);
    }

    public void addParticipant(ReservationParticipantEntity participant) {
        participants.add(participant);
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }

    public void updatePricePaid(Integer amount) {
        this.pricePaid += amount;
    }

    public void updateTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }
}
