package staysplit.hotel_reservation.reservation.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import staysplit.hotel_reservation.customer.repository.CustomerRepository;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisUsernameInitializer {

    private final CustomerRepository customerRepository;
    private final UsernameAutocompleteService autocompleteService;

    @PostConstruct
    public void init() {
        log.info("RedisUsernameInitializer");
        List<String> usernames = customerRepository.findAllUsernames();
        if (usernames != null) {
            usernames.forEach(autocompleteService::addUsername);
        }
    }
}
