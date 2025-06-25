package staysplit.hotel_reservation.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import staysplit.hotel_reservation.user.domain.entity.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
    Optional<UserEntity> findBySocialId(String socialId);
    Optional<UserEntity> findByEmail(String email);
    boolean existsBySocialId(String socialId);
}
