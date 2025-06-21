package staysplit.hotel_reservation.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import staysplit.hotel_reservation.common.security.jwt.JwtTokenFilter;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    private final String[] ALLOWED_URLS = {
            "/api/customers/sign-up",
            "/api/providers/sign-up",
            "/api/users/login",

            "/oauth2/authorization/google",
            "/login/oauth2/code/google",
            "/api/customers/google/login",
            "/oauth/**",

            "/api/review/**",
    };

    private final String[] GET_ALLOWED_URLS = {
            "/api/hotels/*"
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .cors(cors -> cors.configurationSource(configurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req.requestMatchers(ALLOWED_URLS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hotels/list").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hotels/*").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // TODO: 추가적으로 배포 url이 나오면 추가
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("*")); // 모든 http method 허용
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 http header 허용
        configuration.setAllowCredentials(true); // Authorization header 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
