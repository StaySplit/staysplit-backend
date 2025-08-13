package staysplit.hotel_reservation.provider.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.provider.domain.dto.reqeust.ProviderSignupRequest;
import staysplit.hotel_reservation.provider.domain.dto.response.ProviderSignupResponse;
import staysplit.hotel_reservation.provider.domain.entity.ProviderEntity;
import staysplit.hotel_reservation.provider.repository.ProviderRepository;
import staysplit.hotel_reservation.user.domain.entity.UserEntity;
import staysplit.hotel_reservation.user.domain.enums.Role;
import staysplit.hotel_reservation.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderServiceTest {

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private ProviderService providerService;

    private final String email = "provider@example.com";
    private final String rawPassword = "12345";
    private final String encodedPassword = "ncodedPassword";
    private final String nickName = "testNick";

    private ProviderSignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        signupRequest = new ProviderSignupRequest(email, rawPassword, nickName);
    }

    @Test
    void signup_successful_returnsProviderInfoResponse() {
        // given
        given(userRepository.existsByEmail(email)).willReturn(false);
        given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);

        UserEntity mockSavedUser = UserEntity.builder()
                .id(1)
                .email(email)
                .password(encodedPassword)
                .role(Role.PROVIDER)
                .build();

        ProviderEntity mockSavedProvider = ProviderEntity.builder()
                .id(1)
                .user(mockSavedUser)
                .build();

        given(userRepository.save(any(UserEntity.class))).willReturn(mockSavedUser);
        given(providerRepository.save(any(ProviderEntity.class))).willReturn(mockSavedProvider);

        // when
        ProviderSignupResponse response = providerService.signup(signupRequest);

        // then
        verify(userRepository).save(any(UserEntity.class));
        verify(providerRepository).save(any(ProviderEntity.class));

        assertThat(response.email()).isEqualTo(email);
    }

    @Test
    void signup_duplicateEmail_throwsAppException() {
        // given
        given(userRepository.existsByEmail(email)).willReturn(true);

        // expect
        assertThatThrownBy(() -> providerService.signup(signupRequest))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.DUPLICATE_EMAIL.getMessage());

        verify(userRepository, never()).save(any());
        verify(providerRepository, never()).save(any());
    }
}