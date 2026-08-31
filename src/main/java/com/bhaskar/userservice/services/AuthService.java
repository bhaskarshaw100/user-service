package com.bhaskar.userservice.services;

import com.bhaskar.userservice.exceptions.UserAlreadyExistException;
import com.bhaskar.userservice.exceptions.UserNotFoundException;
import com.bhaskar.userservice.exceptions.WrongPasswordException;
import com.bhaskar.userservice.models.User;
import com.bhaskar.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
            return "token";
        } else {
            throw new WrongPasswordException("Incorrect password for user with email " + email);
        }
    }
}
