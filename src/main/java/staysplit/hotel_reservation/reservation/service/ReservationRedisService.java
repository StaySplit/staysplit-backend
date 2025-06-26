package staysplit.hotel_reservation.reservation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import staysplit.hotel_reservation.reservation.domain.dto.TempReservationDto;
import staysplit.hotel_reservation.reservation.domain.dto.ReservationInvitationDto;

import java.time.Duration;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ReservationRedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveTempReservation(String reservationKey, TempReservationDto dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            redisTemplate.opsForValue().set("temp:reservation:" + reservationKey, json, Duration.ofMinutes(15));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("임시 예약 정보를 Redis에 저장하는 데 실패했습니다.", e);
        }
    }

    public TempReservationDto getTempReservation(String reservationKey) {
        String json = redisTemplate.opsForValue().get("temp:reservation:" + reservationKey);
        if (json == null) return null;

        try {
            return objectMapper.readValue(json, TempReservationDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis에서 임시 예약 정보를 파싱할 수 없습니다.", e);
        }
    }

    public void deleteTempReservation(String reservationKey) {
        redisTemplate.delete("temp:reservation:" + reservationKey);
    }

    public void saveReservationInvitation(Integer reservationId, String email, ReservationInvitationDto dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            String key = "invitation:" + reservationId + ":" + email;
            redisTemplate.opsForValue().set(key, json, Duration.ofHours(24));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("예약 초대 정보를 Redis에 저장하는 데 실패했습니다.", e);
        }
    }

    public ReservationInvitationDto getReservationInvitation(Integer reservationId, String email) {
        String key = "invitation:" + reservationId + ":" + email;
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return null;

        try {
            return objectMapper.readValue(json, ReservationInvitationDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis에서 예약 초대 정보를 파싱할 수 없습니다.", e);
        }
    }

    public void deleteReservationInvitation(Integer reservationId, String email) {
        String key = "invitation:" + reservationId + ":" + email;
        redisTemplate.delete(key);
    }

    public void addUserReservationNotification(Integer userId, String message) {
        String key = "notifications:user:" + userId;
        redisTemplate.opsForList().leftPush(key, message);
        redisTemplate.expire(key, Duration.ofDays(7));
        redisTemplate.opsForList().trim(key, 0, 99);
    }

    public List<String> getUserReservationNotifications(Integer userId) {
        String key = "notifications:user:" + userId;
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public void clearUserReservationNotifications(Integer userId) {
        String key = "notifications:user:" + userId;
        redisTemplate.delete(key);
    }
}
