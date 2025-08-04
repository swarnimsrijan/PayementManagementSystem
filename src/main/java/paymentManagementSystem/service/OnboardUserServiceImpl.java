package paymentManagementSystem.service;

import paymentManagementSystem.dao.UserDAO;
import paymentManagementSystem.dto.UserDTO;
import paymentManagementSystem.entity.User;
import paymentManagementSystem.enums.UserRole;
import paymentManagementSystem.util.AuditLogger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class OnboardUserServiceImpl implements OnboardUserService {
    private final UserDAO userDAO;
    private final AuditLogger auditLogger;

    public OnboardUserServiceImpl(UserDAO userDAO, AuditLogger auditLogger) {
        this.userDAO = userDAO;
        this.auditLogger = auditLogger;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User userModel = mapDTOToEntity(userDTO);
        User savedUser = userDAO.save(userModel);
        auditLogger.logUserCreation(savedUser.getId().toString(), "User created");
        return mapEntityToDTO(savedUser);
    }

    @Override
    public Optional<UserDTO> getUserByEmail(String email) {
        return userDAO.findByEmail(email)
                .map(this::mapEntityToDTO);
    }

    @Override
    public Optional<UserDTO> getUserById(UUID id) {
        return userDAO.findById(id)
                .map(this::mapEntityToDTO);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userDAO.findAll().stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersByName(String name) {
        return userDAO.findByUsername(name).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        User userModel = mapDTOToEntity(userDTO);
        userDAO.update(userModel);
        auditLogger.logUserUpdate(userModel.getId().toString(), "User updated");
        return userDTO;
    }

    @Override
    public void deleteUser(UUID id) {
        userDAO.delete(id);
        auditLogger.logUserDeletion(id.toString(), "User deleted");
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userDAO.existsByEmail(email);
    }

    private UserDTO mapEntityToDTO(User entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .city(entity.getCity())
                .build();
    }

    private User mapDTOToEntity(UserDTO dto) {
        return User.builder()
                .id(dto.getId() != null ? dto.getId() : UUID.randomUUID())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .city(dto.getCity())
                .role(UserRole.VIEWER)
                .build();
    }
}