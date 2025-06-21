package staysplit.hotel_reservation.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // USER
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "중복되는 닉네임입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "사용자의 이메일이 중복됩니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    INVALID_PASSWORD(HttpStatus.CONFLICT, "비밀번호가 틀립니다."),
    ADDITIONAL_INFO_REQUIRED(HttpStatus.UNAUTHORIZED, "추가 정보 입력이 필요합니다."),
    DUPLICATE_SOCIAL_ID(HttpStatus.CONFLICT, "이미 가입된 계정입니다."),
    PROVIDER_ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 존재하는 공급자입니다"),

    // Room
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 방입니다"),
    UNAUTHORIZED_PROVIDER(HttpStatus.UNAUTHORIZED, "이 방에 대한 권한이 없습니다"),

    // Hotel
    HOTEL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 호텔입니다"),

    // Review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."),
    UNAUTHORIZED_REVIEWER(HttpStatus.UNAUTHORIZED, "리뷰 작성자가 아닙니다.");

    private HttpStatus httpStatus;
    private String message;
}
