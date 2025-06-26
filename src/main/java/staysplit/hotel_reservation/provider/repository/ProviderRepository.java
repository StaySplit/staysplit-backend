package staysplit.hotel_reservation.provider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import staysplit.hotel_reservation.provider.domain.entity.ProviderEntity;
import staysplit.hotel_reservation.user.domain.entity.UserEntity;

import java.util.Optional;


@Repository
public interface ProviderRepository extends JpaRepository<ProviderEntity, Integer> {

    @Query("SELECT p FROM ProviderEntity p WHERE p.user.email = :email")
    Optional<ProviderEntity> findByEmail(@Param("email") String email);

}
