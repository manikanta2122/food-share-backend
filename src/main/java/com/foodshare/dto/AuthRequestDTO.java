package com.foodshare.dto;

import com.foodshare.model.Role;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AuthRequestDTO {

    public static class Login {
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        public Login() {}
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class Register {
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        private String password;

        @NotBlank(message = "Password confirmation is required")
        private String confirmPassword;

        private String phone;

        @NotNull(message = "Account type is required")
        private Role role; // Only DONOR or NGO allowed for public self-registration

        private String providerCategory; // For DONOR: RESTAURANT, HOTEL, CAFETERIA, BAKERY, OTHER

        private String organizationName;
        private String address;
        private String city;

        private Boolean termsAccepted;

        public Register() {}

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }

        public String getProviderCategory() { return providerCategory; }
        public void setProviderCategory(String providerCategory) { this.providerCategory = providerCategory; }

        public String getOrganizationName() { return organizationName; }
        public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public Boolean getTermsAccepted() { return termsAccepted; }
        public void setTermsAccepted(Boolean termsAccepted) { this.termsAccepted = termsAccepted; }
    }
}
