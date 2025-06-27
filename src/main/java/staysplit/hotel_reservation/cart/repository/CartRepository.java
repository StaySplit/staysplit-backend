package staysplit.hotel_reservation.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import staysplit.hotel_reservation.cart.domain.entity.CartEntity;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    Optional<CartEntity> findByCustomer(CustomerEntity customer);
}
