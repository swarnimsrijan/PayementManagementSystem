package paymentManagementSystem.entity;

import paymentManagementSystem.enums.UserRole;

import java.util.UUID;

public class User {
    private UUID id;
    private String username;
    private String passwordHash;
    private String email;
    private UserRole role;
    private String phoneNumber;
    private String address;
    private String city;

    public User() {}

    public User(UUID id, String username, String passwordHash, String email,
                UserRole role, String address, String phoneNumber, String city) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.city = city;
    }

    // Getters
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public String getPhoneNumber() { return phoneNumber; }

    public String getAddress() { return address; }

    public String getCity() { return city;}

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(UserRole role) { this.role = role; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }


    // Builder pattern
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private UUID id;
        private String username;
        private String passwordHash;
        private String email;
        private UserRole role;
        private String phoneNumber;
        private String address;
        private String city;

        public UserBuilder id(UUID id) { this.id = id; return this; }
        public UserBuilder username(String username) { this.username = username; return this; }
        public UserBuilder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder role(UserRole role) { this.role = role; return this; }
        public UserBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber ;return this;}

        public UserBuilder address(String address) { this.address = address;return this;
        }

        public UserBuilder city(String city) {
            this.city = city; return this;
        }

        public User build() {
            return new User(id, username, passwordHash, email, role, address, phoneNumber, city);
        }
    }
}