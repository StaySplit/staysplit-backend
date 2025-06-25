package staysplit.hotel_reservation.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import staysplit.hotel_reservation.common.entity.BaseEntity;
import staysplit.hotel_reservation.user.domain.enums.LoginSource;
import staysplit.hotel_reservation.user.domain.enums.Role;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // TODO: create a method in service
    public void changePassword(String hashedPassword) {
        this.password = hashedPassword;
    }

}
