package paymentManagementSystem.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class AuditLogger {

    public void logAction(String userId, String action, String entityId, Object oldValue, Object newValue) {
        String sql = "INSERT INTO audit_logs (user_id, action, entity_id, old_value, new_value, timestamp) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, action);
            stmt.setString(3, entityId);
            stmt.setString(4, oldValue != null ? oldValue.toString() : null);
            stmt.setString(5, newValue != null ? newValue.toString() : null);
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error logging audit action: " + e.getMessage());
        }
    }

    public void logUserAction(String userId, String action, String details) {
        logAction(userId, action, userId, null, details);
    }

    public void logPaymentAction(String userId, String action, String paymentId, String details) {
        logAction(userId, action, paymentId, null, details);
    }

    public void logUserCreation(String userId, String details) {
        logUserAction(userId, "USER_CREATED", details);
    }

    public void logUserUpdate(String userId, String details) {
        logUserAction(userId, "USER_UPDATED", details);
    }

    public void logUserDeletion(String userId, String details) {
        logUserAction(userId, "USER_DELETED", details);
    }
}