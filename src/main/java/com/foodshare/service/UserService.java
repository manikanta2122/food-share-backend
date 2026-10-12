package com.foodshare.service;

import com.foodshare.dto.AuthRequestDTO;
import com.foodshare.dto.AuthResponseDTO;
import com.foodshare.model.Role;
import com.foodshare.model.User;
import com.foodshare.repository.UserRepository;
import com.foodshare.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public AuthResponseDTO register(AuthRequestDTO.Register request) {
        // Enforce password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Prevent duplicate email registrations
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new IllegalArgumentException("An account with email " + request.getEmail() + " already exists");
        }

        // Never trust arbitrary roles from public registration (Only DONOR and NGO allowed; ADMIN prohibited)
        if (request.getRole() == Role.ADMIN || request.getRole() == Role.VOLUNTEER) {
            throw new IllegalArgumentException("Public self-registration for this role is not permitted");
        }

        if (request.getTermsAccepted() == null || !request.getTermsAccepted()) {
            throw new IllegalArgumentException("You must accept the terms of service and food sharing safety policy");
        }

        // BCrypt hash the password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getFullName().trim(),
                request.getEmail().toLowerCase().trim(),
                hashedPassword,
                request.getPhone(),
                request.getRole(),
                request.getProviderCategory(),
                request.getOrganizationName(),
                request.getAddress(),
                request.getCity()
        );

        User savedUser = userRepository.save(user);
        String token = jwtUtils.generateToken(savedUser.getEmail(), savedUser.getRole().name());

        return new AuthResponseDTO(token, savedUser);
    }

    public AuthResponseDTO login(AuthRequestDTO.Login request) {
        String email = request.getEmail().toLowerCase().trim();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponseDTO(token, user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
}
