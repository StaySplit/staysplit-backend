package staysplit.hotel_reservation.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "iamport")
public class IamportProperties {
    private String apiKey;
    private String secret;
}