package paymentManagementSystem.dao;

import paymentManagementSystem.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDAO {
    User save(User user);
    Optional<User> findById(UUID id);
    List<User> findAll();
    Optional<User> findByEmail(String email);
    void update(User user);
    void delete(UUID id);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
}