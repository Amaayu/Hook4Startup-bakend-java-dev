package com.cms.controller;

import com.cms.models.SessionToken;
import com.cms.models.User;
import com.cms.repository.CustomerRepo;
import com.cms.repository.SessionTokenRepository;
import com.cms.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/auth") // ✅ Public APIs
public class  AuthController {

    @Autowired
    private CustomerRepo userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SessionTokenRepository sessionTokenRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> requestData,
                                        @CookieValue(name = "session_token", required = false) String sessionToken,
                                        HttpServletResponse response) {

        String username = requestData.get("username");
        String password = requestData.get("password");

        if (username == null || password == null) {
            return ResponseEntity.status(400).body("Username or password is missing");
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        User user = userOpt.get();

        // ✅ Check if session token exists in DB
        Optional<SessionToken> existingTokenOpt = sessionTokenRepository.findById(String.valueOf(user.getSessionTokenId()));

        String token;
        if (existingTokenOpt.isPresent()) {
            SessionToken existingToken = existingTokenOpt.get();

            // ✅ Check if token is still valid
            if (existingToken.getExpiry().isAfter(Instant.now())) {
                token = existingToken.getToken(); // पुराना token valid है, इसे ही use करो
            } else {
                // ✅ Token expired, generate new one
                SessionToken newToken = tokenService.generateToken(username, existingToken.getToken());
                existingToken.setToken(newToken.getToken());
                existingToken.setExpiry(newToken.getExpiry());
                sessionTokenRepository.save(existingToken);
                token = newToken.getToken();
            }
        } else {
            // ✅ No previous token found, create a new one
            SessionToken newSession = tokenService.generateToken(username, user.getId());
            sessionTokenRepository.save(newSession);
            token = newSession.getToken();
        }

        // ✅ Secure cookie set करना
        ResponseCookie cookie = ResponseCookie.from("session_token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 दिन तक valid
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("Session started successfully");
    }


    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Map<String, String> requestData,
                                         HttpServletResponse response) {
        // ✅ Fix: JSON request se data le rahe hain
        String username = requestData.get("username");
        String password = requestData.get("password");
        String email = requestData.get("email");

        if (username == null || password == null) {
            return ResponseEntity.status(400).body("Username or password is missing");
        }
        System.out.println(email);

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(400).body("User already exists");
        }

        User newUser = new User(username, password , email);
        System.out.println(newUser);
        userRepository.save(newUser);

        // ✅ New user ke liye session token generate karo
        SessionToken newToken = tokenService.generateToken(username, newUser.getId());
        newUser.setSessionTokenId(newToken);


        // ✅ Secure cookie response send karo
        ResponseCookie cookie = ResponseCookie.from("session_token", newToken.getToken())
                .httpOnly(true)
                .secure(true)  // ✅ HTTPS ke liye `true`
                .sameSite("None")
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("New session started");

    }
}
