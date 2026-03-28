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
        String key = PREFIX + request.getTokenValue();

        if(request.getTokenValue() == null) {
            return null;
        }

        Long remainingTtl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        if (remainingTtl == null || remainingTtl < 0) {
            return null;
        }

        String username = redisTemplate.opsForValue().getAndDelete(key);

        if (username == null) {
            return null;
        }

        Instant expiresAt = Instant.now().plusSeconds(remainingTtl);

        return new DefaultOneTimeToken(request.getTokenValue(), username, expiresAt);
    }
}
