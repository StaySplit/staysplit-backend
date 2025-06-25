package staysplit.hotel_reservation.common.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccessTokenDto {

    @JsonProperty("access_token")
    private String accessToken;
}
