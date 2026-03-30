package net.softloaf.ded_fuse.security;

import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.repository.UserRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ott.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class OneTimeTokenServiceImpl implements OneTimeTokenService {
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "ott:";

    @Override
    public OneTimeToken generate(GenerateOneTimeTokenRequest request) {
        String token = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        int ttl = 300;

        String username = request.getUsername();
        if(!userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя не существует");
        }

        redisTemplate.opsForValue().set(
                PREFIX + token,
                username,
                Duration.ofSeconds(ttl)
        );

        return new DefaultOneTimeToken(token, request.getUsername(), Instant.now().plusSeconds(ttl));
    }

    @Override
    public OneTimeToken consume(OneTimeTokenAuthenticationToken request) {
        String[] requestParts;

        if(request.getTokenValue() == null) {
            return null;
        }

        try {
            requestParts = request.getTokenValue().split(":");
        } catch (Exception e) {
            return null;
        }

        String requestUsername = requestParts[0];
        String requestCode = requestParts[1];

        //СНЕСТИ ПОД ЧИСТУЮ ПРИ ПРОДАКШЕНЕ
        if (requestCode.equals("1337")) {
            return new DefaultOneTimeToken(requestCode, requestUsername, Instant.now().plusSeconds(300));
        }

        String key = PREFIX + requestCode;

        Long remainingTtl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        if (remainingTtl == null || remainingTtl < 0) {
            return null;
        }

        String username = redisTemplate.opsForValue().get(key);

        if (username == null || !username.equals(requestUsername)) {
            return null;
        }

        redisTemplate.opsForValue().getAndDelete(key);

        Instant expiresAt = Instant.now().plusSeconds(remainingTtl);

        return new DefaultOneTimeToken(requestCode, requestUsername, expiresAt);
    }
}
