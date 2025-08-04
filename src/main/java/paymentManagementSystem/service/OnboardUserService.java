package paymentManagementSystem.service;

import paymentManagementSystem.dto.UserDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OnboardUserService {
    UserDTO createUser(UserDTO userDTO);
    Optional<UserDTO> getUserByEmail(String email);
    Optional<UserDTO> getUserById(UUID id);
    List<UserDTO> getAllUsers();
    List<UserDTO> getUsersByName(String name);
    UserDTO updateUser(UserDTO userDTO);
    void deleteUser(UUID id);
    boolean isEmailTaken(String email);
}