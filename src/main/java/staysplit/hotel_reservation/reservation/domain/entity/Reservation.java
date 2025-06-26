package staysplit.hotel_reservation.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import staysplit.hotel_reservation.common.entity.BaseEntity;
import staysplit.hotel_reservation.hotel.entity.HotelEntity;
import staysplit.hotel_reservation.user.domain.entity.UserEntity;
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
public class Reservation  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;


    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;

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
    private List<ReservationParticipant> participants = new ArrayList<>();

    public enum ReservationStatus {
        PENDING, CONFIRMED, CANCELLED
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }

    public void updatePricePaid(Integer amount) {
        this.pricePaid += amount;
    }

    public void addParticipant(ReservationParticipant participant) {
        this.participants.add(participant);
        participant.setReservation(this);
    }

    public boolean isOwner(Integer userId) {
        return this.user.getId().equals(userId);
    }

    public boolean hasParticipant(String email) {
        return participants.stream()
                .anyMatch(p -> p.getUserEmail().equals(email));
    }

    public ReservationDetailResponse toDetailResponse(Integer currentUserId) {
        boolean isOwner = this.isOwner(currentUserId);

        return ReservationDetailResponse.builder()
                .reservationId(this.reservationId)
                .reservationNumber(this.reservationNumber)
                .hotel(ReservationDetailResponse.HotelInfo.builder()
                        .hotelId(this.hotel.getHotelId().intValue())
                        .name(this.hotel.getName())
                        .address(this.hotel.getAddress())
                        .build())
                .checkInDate(this.checkInDate)
                .checkOutDate(this.checkOutDate)
                .totalPrice(this.totalPrice)
                .pricePaid(this.pricePaid)
                .status(this.status.name())
                .isOwner(isOwner)
                .owner(ReservationDetailResponse.OwnerInfo.builder()
                        .userId(this.user.getId().intValue())
                        .email(this.user.getEmail())
                        .build())
                .participants(this.participants.stream()
                        .map(ReservationParticipant::toParticipantInfo)
                        .toList())
                .build();
    }

    public ReservationListResponse toListResponse(Integer currentUserId) {
        boolean isOwner = this.isOwner(currentUserId);

        return ReservationListResponse.builder()
                .reservationId(this.reservationId)
                .reservationNumber(this.reservationNumber)
                .hotelName(this.hotel.getName())
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