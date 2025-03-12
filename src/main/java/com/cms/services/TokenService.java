package com.cms.services;

import com.cms.models.SessionToken;
import com.cms.repository.SessionTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    @Autowired
    private SessionTokenRepository sessionTokenRepository;

    public String generateToken(String username) {
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plusSeconds(86400); // 1 din ka expiry

        // Pehle se token exist hai to use update karo, warna naya save karo
        Optional<SessionToken> existingToken = sessionTokenRepository.findByUsername(username);
        if (existingToken.isPresent()) {
            SessionToken tokenObj = existingToken.get();
            tokenObj = new SessionToken(token, username, expiry);
            sessionTokenRepository.save(tokenObj);
        } else {
            sessionTokenRepository.save(new SessionToken(token, username, expiry));
        }

        return token;
    }

    public Optional<String> validateToken(String token) {
        Optional<SessionToken> sessionToken = sessionTokenRepository.findByToken(token);
        if (sessionToken.isPresent() && sessionToken.get().getExpiry().isAfter(Instant.now())) {
            System.out.println(sessionToken.get().getUsername());
            return Optional.of(sessionToken.get().getUsername());
        }


        return Optional.empty();
    }
}
