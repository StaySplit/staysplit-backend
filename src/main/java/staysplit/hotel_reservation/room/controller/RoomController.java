package staysplit.hotel_reservation.room.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import staysplit.hotel_reservation.common.entity.Response;
import staysplit.hotel_reservation.room.domain.dto.request.CreateRoomRequest;
import staysplit.hotel_reservation.room.domain.dto.request.UpdateRoomRequest;
import staysplit.hotel_reservation.room.domain.dto.response.RoomDeleteResponse;
import staysplit.hotel_reservation.room.domain.dto.response.RoomInfoResponse;
import staysplit.hotel_reservation.room.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public Response<RoomInfoResponse> createRoom(@Valid @RequestBody CreateRoomRequest request, Authentication authentication) {
        RoomInfoResponse infoResponse = roomService.createRoom(authentication.getName(), request);
        return Response.success(infoResponse);
    }

    @GetMapping("/{roomId}")
    public Response<RoomInfoResponse> getRoomById(@PathVariable Integer roomId) {
        RoomInfoResponse roomInfoResponse = roomService.findRoomById(roomId);
        return Response.success(roomInfoResponse);
    }

    @DeleteMapping("/{roomId}")
    public Response<RoomDeleteResponse> deleteRoom(@PathVariable Integer roomId, Authentication authentication) {
        RoomDeleteResponse response = roomService.deleteRoom(authentication.getName(), roomId);
        return Response.success(response);
    }

    @PutMapping("/{roomId}")
    public Response<RoomInfoResponse> updateRoom(@PathVariable Integer roomId,
                                                 @Valid @RequestBody UpdateRoomRequest updateRoomRequest,
                                                 Authentication authentication) {
        RoomInfoResponse response = roomService.updateRoom(authentication.getName(), updateRoomRequest);
        return Response.success(response);
    }

    //
    // TODO: HotelController에 옮기고, SecConfig에서 uri 열어두기
    // GET ALL ROOMS
    @GetMapping("/hotels/{hotelId}")
    public Response<Page<RoomInfoResponse>> findAllRoomsByHotelId(@PathVariable Integer hotelId,
                                                                  @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.ASC)
                                                                  Pageable pageable) {
        Page<RoomInfoResponse> rooms = roomService.findAllRoomsByHotel(hotelId, pageable);
        return Response.success(rooms);
    }
}
