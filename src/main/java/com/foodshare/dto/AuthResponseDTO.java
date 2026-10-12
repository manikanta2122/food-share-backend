package com.foodshare.dto;

import com.foodshare.model.Role;
import com.foodshare.model.User;

public class AuthResponseDTO {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private String providerCategory;
    private String organizationName;
    private String address;
    private String city;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token, User user) {
        this.token = token;
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.providerCategory = user.getProviderCategory();
        this.organizationName = user.getOrganizationName();
        this.address = user.getAddress();
        this.city = user.getCity();
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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
}
