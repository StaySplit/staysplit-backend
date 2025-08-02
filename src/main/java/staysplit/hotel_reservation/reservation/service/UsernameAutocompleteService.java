package staysplit.hotel_reservation.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsernameAutocompleteService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String USERNAME_KEY = "usernames:";

    public void addUsername(String username) {
        String key = getPrefixKey(username);
        redisTemplate.opsForZSet().add(key, username, 0);
    }

    public void removeUsername(String username) {
        String key = getPrefixKey(username);
        redisTemplate.opsForZSet().remove(key, username);
    }

    public List<String> searchByPrefix(String prefix) {

        String key = getPrefixKey(prefix);
        Set<String> result = redisTemplate.opsForZSet()
                .rangeByLex(key, Range.closed(prefix, prefix + "\uFFFF"));

        log.info("Search result: " + result);
        return result == null ? List.of() : new ArrayList<>(result);
    }

    private String getPrefixKey(String username) {
        return USERNAME_KEY + username.substring(0, 1);
    }
}
