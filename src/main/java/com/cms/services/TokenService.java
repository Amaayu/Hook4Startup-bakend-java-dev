package com.cms.services;

import com.cms.models.SessionToken;
import com.cms.models.User;
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

    public SessionToken generateToken(String username , String id) {
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plusSeconds(86400); // 1 din ka expiry

        // Pehle se token exist hai to use update karo, warna naya save karo
        Optional<SessionToken> existingToken = sessionTokenRepository.findById(id);
        System.out.println(username);
        System.out.println(token);
        System.out.println(expiry);
        if (existingToken.isPresent()) {
            SessionToken tokenObj = existingToken.get();
            tokenObj = new SessionToken(token, username, expiry);
            sessionTokenRepository.save(tokenObj);
            return tokenObj;
        }
        SessionToken tokenObj = new SessionToken(token, username, expiry);
            sessionTokenRepository.save(tokenObj);



        return tokenObj;
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
