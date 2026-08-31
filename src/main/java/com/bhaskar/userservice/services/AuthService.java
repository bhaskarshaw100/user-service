package com.bhaskar.userservice.services;

import com.bhaskar.userservice.exceptions.UserNotFoundException;
import com.bhaskar.userservice.exceptions.WrongPasswordException;
import com.bhaskar.userservice.models.User;
import com.bhaskar.userservice.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class AuthService {

    UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
            return createJwtToken(userOptional.get().getId(),
                    new ArrayList<>(),
                    userOptional.get().getEmail());
        } else {
            throw new WrongPasswordException("Incorrect password for user with email " + email);
        }
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
                .signWith(Jwts.SIG.HS256.key().build())
                .compact();
    }
}
