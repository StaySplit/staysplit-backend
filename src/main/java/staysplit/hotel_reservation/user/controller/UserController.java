package staysplit.hotel_reservation.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import staysplit.hotel_reservation.common.entity.Response;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.user.domain.dto.request.LoginRequest;
import staysplit.hotel_reservation.user.domain.dto.request.PasswordUpdateRequest;
import staysplit.hotel_reservation.user.domain.dto.response.UserLoginResponse;
import staysplit.hotel_reservation.user.domain.dto.response.UserLoginStatusResponse;
import staysplit.hotel_reservation.user.service.UserService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    @Value("${jwt.expiration}")
    private int jwtExpirationInSeconds;

    private final UserService userService;

    // 일반 로그인, 소셜 로그인 아님
    // role을 반환
    @PostMapping("/login")
    public Response<String> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        UserLoginResponse response = userService.login(loginRequest);

        ResponseCookie cookie = ResponseCookie.from("token", response.jwt())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(jwtExpirationInSeconds)
                .build();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return Response.success("ROLE_" + response.role());
    }

    // 비밀 번호 변경
    @PutMapping("/pw")
    public Response<String> changePassword(@RequestBody PasswordUpdateRequest request,
                                           Authentication authentication) {
        String response = userService.changePassword(request, authentication.getName());
        return Response.success(response);
    }

    @PostMapping("/logout")
    public Response<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
        return Response.success("로그아웃 성공");
    }

    @GetMapping("/auth/status")
    public Response<UserLoginStatusResponse> getUserLoginStatus(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new AppException(ErrorCode.USER_NOT_LOGGED_IN, ErrorCode.USER_NOT_FOUND.getMessage());
        }
        String email = userDetails.getUsername();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("UNKNOWN");

        UserLoginStatusResponse response = new UserLoginStatusResponse(email, role, true);
        return Response.success(response);
    }
}
