package staysplit.hotel_reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import staysplit.hotel_reservation.common.config.IamportProperties;

@SpringBootApplication
@EnableConfigurationProperties(IamportProperties.class)
public class HotelReservationApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelReservationApplication.class, args);
	}

}
