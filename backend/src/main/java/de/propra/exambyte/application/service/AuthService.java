package de.propra.exambyte.application.service;

import de.propra.exambyte.application.repository.UserRepository;
import de.propra.exambyte.domain.model.user.AppUser;
import de.propra.exambyte.web.dto.AuthRequest;
import de.propra.exambyte.web.dto.AuthResponse;
import de.propra.exambyte.web.dto.RegisterRequest;
import java.util.Optional;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already taken");
        }
        
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Create new user with plain text password for demo purposes
        AppUser user = new AppUser(
            request.username(),
            request.password(),
            request.email()
        );
        
        // Save user
        AppUser savedUser = userRepository.save(user);
        
        return new AuthResponse(savedUser.getId());
    }
    
    public AppUser login(AuthRequest request) {
        // Find user by username
        AppUser user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        
        // Verify password with simple string comparison
        if (!request.password().equals(user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        
        return user;
    }
}