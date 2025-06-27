package staysplit.hotel_reservation.room.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import staysplit.hotel_reservation.common.entity.Response;
import staysplit.hotel_reservation.room.domain.dto.request.CreateRoomRequest;
import staysplit.hotel_reservation.room.domain.dto.request.UpdateRoomRequest;
import staysplit.hotel_reservation.room.domain.dto.response.RoomDeleteResponse;
import staysplit.hotel_reservation.room.domain.dto.response.RoomDetailResponse;
import staysplit.hotel_reservation.room.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public Response<RoomDetailResponse> createRoom(@Valid @RequestBody CreateRoomRequest request, Authentication authentication) {
        RoomDetailResponse infoResponse = roomService.createRoom(authentication.getName(), request);
        return Response.success(infoResponse);
    }

    @GetMapping("/{roomId}")
    public Response<RoomDetailResponse> getRoomById(@PathVariable Long roomId) {
        RoomDetailResponse RoomDetailResponse = roomService.findRoomById(roomId);
        return Response.success(RoomDetailResponse);
    }

    @DeleteMapping("/{roomId}")
    public Response<RoomDeleteResponse> deleteRoom(@PathVariable Long roomId, Authentication authentication) {
        RoomDeleteResponse response = roomService.deleteRoom(authentication.getName(), roomId);
        return Response.success(response);
    }

    @PutMapping("/{roomId}")
    public Response<RoomDetailResponse> updateRoom(@PathVariable Long roomId,
                                                 @Valid @RequestBody UpdateRoomRequest updateRoomRequest,
                                                 Authentication authentication) {
        RoomDetailResponse response = roomService.updateRoom(authentication.getName(), roomId, updateRoomRequest);
        return Response.success(response);
    }

}
