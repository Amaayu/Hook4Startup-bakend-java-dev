package com.cms.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "session_tokens")
public class SessionToken {
    @Id
    private String token;
    private String username;
    private Instant expiry;

    public SessionToken(String token, String username, Instant expiry) {
        this.token = token;
        this.username = username;
        this.expiry = expiry;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public Instant getExpiry() { return expiry; }
}
