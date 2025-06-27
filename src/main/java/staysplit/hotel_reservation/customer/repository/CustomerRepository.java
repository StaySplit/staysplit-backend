package staysplit.hotel_reservation.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.user.domain.entity.UserEntity;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByUser(UserEntity user);
    boolean existsByNickname(String nickname);

    @Query("SELECT c FROM CustomerEntity c WHERE c.user.email = :email")
    Optional<CustomerEntity> findByUserByEmail(@Param("email") String email);

    Page<CustomerEntity> findAll(Pageable pageable);


}
