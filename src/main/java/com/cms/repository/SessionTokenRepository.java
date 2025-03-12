package com.cms.repository;


import com.cms.models.SessionToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface SessionTokenRepository extends MongoRepository<SessionToken, String> {
    Optional<SessionToken> findByToken(String token);
    Optional<SessionToken> findByUsername(String username);
}
