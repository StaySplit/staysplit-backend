package staysplit.hotel_reservation.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;
import staysplit.hotel_reservation.common.entity.BaseEntity;
import staysplit.hotel_reservation.user.domain.enums.LoginSource;
import staysplit.hotel_reservation.user.domain.enums.Role;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE user_entity SET deleted_at = current_timestamp WHERE user_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;


    private String password;

    @Setter
    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginSource loginSource;

    @Column(unique = true)
    private String socialId;

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changePassword(String hashedPassword) {
        this.password = hashedPassword;
    }

}
