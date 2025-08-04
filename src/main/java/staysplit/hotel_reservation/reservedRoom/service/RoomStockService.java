package staysplit.hotel_reservation.reservedRoom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.reservedRoom.entity.ReservedRoomEntity;
import staysplit.hotel_reservation.reservedRoom.repository.ReservedRoomRepository;
import staysplit.hotel_reservation.room.domain.RoomEntity;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RoomStockService {
    private final ReservedRoomRepository reservedRoomRepository;

    public int validateAvailableStock(RoomEntity room, LocalDate checkIn, LocalDate checkOut, int requestedQuantity) {
        int reservedCount = reservedRoomRepository.countReservedRoomsForDateRange(room.getId(), checkIn, checkOut);
        int available = room.getTotalQuantity() - reservedCount;
        if (available < requestedQuantity) {
            throw new AppException(ErrorCode.INSUFFICIENT_ROOM_STOCK, ErrorCode.INSUFFICIENT_ROOM_STOCK.getMessage());
        }
        return available;
    }
}
