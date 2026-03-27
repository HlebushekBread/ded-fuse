package net.softloaf.ded_fuse.service;

import org.springframework.security.authentication.ott.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OneTimeTokenServiceImpl implements OneTimeTokenService {
    private final Map<String, OneTimeToken> ottStorage = new ConcurrentHashMap<>();

    @Override
    public OneTimeToken generate(GenerateOneTimeTokenRequest request) {
        String token = String.format("%06d", new java.util.Random().nextInt(1000000));

        OneTimeToken ott = new DefaultOneTimeToken(
                token,
                request.getUsername(),
                Instant.now().plusSeconds(300)
        );

        ottStorage.put(token, ott);
        return ott;
    }

    @Override
    public OneTimeToken consume(OneTimeTokenAuthenticationToken request) {
        OneTimeToken ott = ottStorage.remove(request.getTokenValue());

        if (ott == null) {
            return null;
        }

        if (ott.getExpiresAt().isBefore(Instant.now())) {
            return null;
        }

        return ott;
    }
}
