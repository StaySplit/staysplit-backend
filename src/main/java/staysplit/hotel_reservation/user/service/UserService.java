package staysplit.hotel_reservation.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.common.security.jwt.JwtTokenProvider;
import staysplit.hotel_reservation.user.domain.dto.request.PasswordUpdateRequest;
import staysplit.hotel_reservation.user.domain.entity.UserEntity;
import staysplit.hotel_reservation.user.domain.dto.request.LoginRequest;
import staysplit.hotel_reservation.user.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 일반 로그인
    @Transactional(readOnly = true)
    public String login(LoginRequest loginRequest) {
        UserEntity user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, ErrorCode.INVALID_PASSWORD.getMessage());
        }

        return jwtTokenProvider.createToken(user.getEmail(), user.getRole().toString());
    }

    public String changePassword(PasswordUpdateRequest request, String email) {
        UserEntity user = validateUser(email);
        user.changePassword(passwordEncoder.encode(request.password()));
        return "비밀번호가 변경되었습니다.";
    }

    public void deleteUser(String email) {
        UserEntity user = validateUser(email);
        userRepository.delete(user);
    }

    private UserEntity validateUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND,
                        ErrorCode.USER_NOT_FOUND.getMessage()));
    }
}
