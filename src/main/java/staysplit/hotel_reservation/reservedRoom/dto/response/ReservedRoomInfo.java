package staysplit.hotel_reservation.reservedRoom.dto.response;

import lombok.Builder;


@Builder
public record ReservedRoomInfo(
        Integer reservationRoomId,
        Integer roomId,
        String roomType,
        Integer maxOccupancy,
        Integer quantity,
        Integer pricePerNight,
        Integer nights,
        Integer subtotalPrice,
        String roomDescription,
        Integer participantCount
) {}
