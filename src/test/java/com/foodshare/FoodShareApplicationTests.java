



package com.foodshare;

import com.foodshare.dto.AuthRequestDTO;
import com.foodshare.dto.AuthResponseDTO;
import com.foodshare.model.FoodDonation;
import com.foodshare.model.FoodStatus;
import com.foodshare.model.FoodType;
import com.foodshare.model.Role;
import com.foodshare.model.User;
import com.foodshare.repository.FoodDonationRepository;
import com.foodshare.repository.UserRepository;
import com.foodshare.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FoodShareApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodDonationRepository donationRepository;

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
        assertNotNull(userRepository);
        assertNotNull(donationRepository);
        assertNotNull(userService);
    }

    @Test
    void testUserRegistrationAndPasswordHashing() {
        String testEmail = "testprovider" + System.currentTimeMillis() + "@foodshare.test";

        AuthRequestDTO.Register registerDto = new AuthRequestDTO.Register();
        registerDto.setFullName("Test Provider Kitchen");
        registerDto.setEmail(testEmail);
        registerDto.setPassword("securePassword123");
        registerDto.setConfirmPassword("securePassword123");
        registerDto.setRole(Role.DONOR);
        registerDto.setProviderCategory("RESTAURANT");
        registerDto.setOrganizationName("Test Kitchen Pvt Ltd");
        registerDto.setAddress("MG Road 100");
        registerDto.setCity("Bengaluru");
        registerDto.setTermsAccepted(true);

        AuthResponseDTO response = userService.register(registerDto);
        assertNotNull(response.getToken());
        assertEquals(testEmail, response.getEmail());

        // Verify that password in DB is BCrypt hashed, NOT plain-text
        User userInDb = userRepository.findByEmail(testEmail).orElse(null);
        assertNotNull(userInDb);
        assertNotEquals("securePassword123", userInDb.getPassword());
        assertTrue(userInDb.getPassword().startsWith("$2a$") || userInDb.getPassword().startsWith("$2b$"));

        // Verify duplicate email is rejected
        assertThrows(IllegalArgumentException.class, () -> userService.register(registerDto));

        // Verify login
        AuthRequestDTO.Login loginDto = new AuthRequestDTO.Login();
        loginDto.setEmail(testEmail);
        loginDto.setPassword("securePassword123");
        AuthResponseDTO loginResponse = userService.login(loginDto);
        assertNotNull(loginResponse.getToken());

        // Verify invalid password is rejected
        loginDto.setPassword("wrongPassword");
        assertThrows(IllegalArgumentException.class, () -> userService.login(loginDto));
    }
}
