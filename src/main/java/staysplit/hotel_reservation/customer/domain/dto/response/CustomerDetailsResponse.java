package staysplit.hotel_reservation.customer.domain.dto.response;

import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;

import java.time.LocalDate;

public record CustomerDetailsResponse(
        Integer id,
        String email,
        String name,
        LocalDate birthdate,
        String nickname) {

    public static CustomerDetailsResponse from(CustomerEntity customer) {
        return new CustomerDetailsResponse(
                customer.getId(),
                customer.getUser().getEmail(),
                customer.getName(),
                customer.getBirthdate(),
                customer.getNickname()
        );
    }
}

