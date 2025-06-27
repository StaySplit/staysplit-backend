package staysplit.hotel_reservation.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import staysplit.hotel_reservation.common.entity.Response;
import staysplit.hotel_reservation.provider.service.ProviderService;
import staysplit.hotel_reservation.user.domain.dto.request.LoginRequest;
import staysplit.hotel_reservation.user.domain.dto.request.PasswordUpdateRequest;
import staysplit.hotel_reservation.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ProviderService providerService;


    // 일반 로그인, 소셜 로그인 아님
    @PostMapping("/login")
    public Response<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = userService.login(loginRequest);
        return Response.success(jwt);
    }


    // 비밀 번호 변경
    @PutMapping("/pw")
    public Response<String> changePassword(@RequestBody PasswordUpdateRequest request,
                                           Authentication authentication) {
        String response = userService.changePassword(request, authentication.getName());
        return Response.success(response);
    }

    @DeleteMapping
    public Response<String> deleteUser(Authentication authentication) {
        userService.deleteUser(authentication.getName());
        return Response.success("회원 탈퇴가 완료되었습니다.");
    }
}
