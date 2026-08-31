package com.bhaskar.userservice.services;

import com.bhaskar.userservice.exceptions.UserNotFoundException;
import com.bhaskar.userservice.exceptions.WrongPasswordException;
import com.bhaskar.userservice.models.Session;
import com.bhaskar.userservice.models.SessionStatus;
import com.bhaskar.userservice.models.User;
import com.bhaskar.userservice.repository.SessionRepository;
import com.bhaskar.userservice.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class AuthService {

    UserRepository userRepository;
    SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
//    private final SecretKey key = Jwts.SIG.HS256.key().build();
    private SecretKey key = Keys.hmacShaKeyFor("BhaskarShawWorkingInJavaProjectForMakingALivingOutOfIt".getBytes(StandardCharsets.UTF_8));

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.sessionRepository = sessionRepository;
    }

    public boolean signUp(String email, String password) throws Exception {
        // Implement sign-up logic here
        if (userRepository.findByEmail(email).isPresent()) {
            log.info("User with email " + email + " already exists");
            return false;
        }
        try {
            User user = new User();
            user.setEmail(email);
            user.setPassword(bCryptPasswordEncoder.encode(password));
            userRepository.save(user);
        } catch (Exception e) {
            throw new Exception("Error occurred while saving user: " + e.getMessage());
        }
        return true;
    }

    public String login(String email, String password) throws Exception {
        // Implement login logic here
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }

        Boolean matches = bCryptPasswordEncoder
                .matches(password, userOptional.get().getPassword());

        if (matches) {
            String token = createJwtToken(userOptional.get().getId(),
                    new ArrayList<>(),
                    userOptional.get().getEmail());

            Session session = new Session();
            session.setToken(token);
            session.setUser(userOptional.get());
            session.setExpiringAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)); // 10 hours
            session.setStatus(SessionStatus.ACTIVE);

            sessionRepository.save(session);

            return token;
        } else {
            throw new WrongPasswordException("Incorrect password for user with email " + email);
        }
    }

    public Boolean validateJwtToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            String email = claimsJws.getPayload().get("email", String.class);
            Date expiration = claimsJws.getPayload().getExpiration();
            if  (expiration.before(new Date())) {
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private String createJwtToken(Long userId, List<String> roles, String email) {
        Map<String, Object> dataInJwt = new HashMap<>();
        dataInJwt.put("userId", userId);
        dataInJwt.put("roles", roles);
        dataInJwt.put("email", email);

        return Jwts.builder()
                .claims(dataInJwt)
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(key)
                .compact();
    }
}
