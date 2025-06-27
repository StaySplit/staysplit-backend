package staysplit.hotel_reservation.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.hotel.entity.HotelEntity;
import staysplit.hotel_reservation.reservation.domain.dto.RoomDetailDto;
import staysplit.hotel_reservation.reservation.domain.dto.response.ReservationDetailResponse;
import staysplit.hotel_reservation.reservation.domain.dto.response.ReservationListResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "reservation_number", nullable = false, unique = true, length = 50)
    private String reservationNumber;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "price_paid", nullable = false)
    @Builder.Default
    private Integer pricePaid = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReservationRoomEntity> reservationRooms = new ArrayList<>();

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReservationParticipantEntity> participants = new ArrayList<>();

    public enum ReservationStatus {
        PENDING, CONFIRMED, CANCELLED
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }

    public void updatePricePaid(Integer amount) {
        this.pricePaid += amount;
    }

    public void addParticipant(ReservationParticipantEntity participant) {
        this.participants.add(participant);
        participant.setReservation(this);
    }

    public void addReservationRoom(ReservationRoomEntity reservationRoom) {
        this.reservationRooms.add(reservationRoom);
    }

    public boolean isOwner(Integer customerId) {
        return this.customer.getId().equals(customerId);
    }

    public boolean hasParticipant(String email) {
        return participants.stream()
                .anyMatch(p -> p.getUserEmail().equals(email));
    }

    public HotelEntity getHotel() {
        return reservationRooms.isEmpty() ? null :
                reservationRooms.get(0).getRoom().getHotel();
    }

    public String getHotelName() {
        HotelEntity hotel = getHotel();
        return hotel != null ? hotel.getName() : null;
    }

    public ReservationDetailResponse toDetailResponse(Integer currentCustomerId) {
        boolean isOwner = this.isOwner(currentCustomerId);
        HotelEntity hotel = getHotel();

        return ReservationDetailResponse.builder()
                .reservationId(this.reservationId)
                .reservationNumber(this.reservationNumber)
                .hotel(ReservationDetailResponse.HotelInfo.builder()
                        .hotelId(hotel != null ? hotel.getHotelId() : null)
                        .name(hotel != null ? hotel.getName() : null)
                        .address(hotel != null ? hotel.getAddress() : null)
                        .build())
                .checkInDate(this.checkInDate)
                .checkOutDate(this.checkOutDate)
                .totalPrice(this.totalPrice)
                .pricePaid(this.pricePaid)
                .status(this.status.name())
                .isOwner(isOwner)
                .owner(ReservationDetailResponse.OwnerInfo.builder()
                        .userId(this.customer.getId())
                        .email(this.customer.getUser().getEmail())
                        .build())
                .participants(this.participants.stream()
                        .map(ReservationParticipantEntity::toParticipantInfo)
                        .toList())
                .rooms(this.reservationRooms.stream()
                        .map(rr -> RoomDetailDto.builder()
                                .roomId(rr.getRoom().getId())
                                .roomType(rr.getRoom().getRoomType())
                                .quantity(rr.getQuantity())
                                .pricePerNight(rr.getPricePerNight())
                                .nights(rr.getNights())
                                .subtotalPrice(rr.getSubtotalPrice())
                                .build())
                        .toList())
                .build();
    }

    public ReservationListResponse toListResponse(Integer currentCustomerId) {
        boolean isOwner = this.isOwner(currentCustomerId);

        return ReservationListResponse.builder()
                .reservationId(this.reservationId)
                .reservationNumber(this.reservationNumber)
                .hotelName(getHotelName())
                .checkInDate(this.checkInDate)
                .checkOutDate(this.checkOutDate)
                .totalPrice(this.totalPrice)
                .status(this.status.name())
                .totalParticipants(this.participants.size() + 1)
                .isOwner(isOwner)
                .isPaid(isOwner ? this.pricePaid >= this.totalPrice : false)
                .build();
    }
}