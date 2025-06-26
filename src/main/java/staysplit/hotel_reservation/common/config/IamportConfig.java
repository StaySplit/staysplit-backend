package staysplit.hotel_reservation.common.config;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IamportProperties.class)
public class IamportConfig {

    @Bean
    public IamportClient iamportClient(IamportProperties props) {
        return new IamportClient(props.getApiKey(), props.getSecret());
    }
}
