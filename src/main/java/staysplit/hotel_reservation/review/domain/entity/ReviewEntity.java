package staysplit.hotel_reservation.review.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long hotelId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer rating;

    public void setContent(String content) {
        this.content = content;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }

}
