package com.bhaskar.userservice.services;

import com.bhaskar.userservice.exceptions.UserAlreadyExistException;
import com.bhaskar.userservice.models.User;
import com.bhaskar.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
            user.setPassword(password);
            userRepository.save(user);
        } catch (Exception e) {
            throw new Exception("Error occurred while saving user: " + e.getMessage());
        }
        return true;
    }

    public String login(String email, String password) {
        // Implement login logic here
        return "token";
    }
}
