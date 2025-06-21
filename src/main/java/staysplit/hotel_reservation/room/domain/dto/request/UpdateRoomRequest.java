package staysplit.hotel_reservation.room.domain.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateRoomRequest(

        @NotNull(message = "Room ID를 입력해 주세요.")
        Long roomId,

        @NotBlank(message = "사진 파일명을 입력해 주세요.")
        String photoUrl,

        @NotBlank(message = "객실에 대한 설명을 입력해 주세요.")
        String description,

        @NotBlank(message = "객실 타입을 입력해 주세요.")
        String roomType,

        @Positive(message = "가격은 0이상이어야 합니다.")
        Integer price,

        @Positive(message = "최소 수용 인원은 1이상 이어야 합니다.")
        Integer maxOccupancy
) {
}
