package paymentManagementSystem.repository;

import paymentManagementSystem.entity.Payment;
import paymentManagementSystem.entity.PaymentCategory;
import paymentManagementSystem.entity.User;
import paymentManagementSystem.enums.PaymentStatus;
import paymentManagementSystem.enums.PaymentType;
import paymentManagementSystem.util.DatabaseConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PaymentRepositoryImpl implements PaymentRepository {

    @Override
    public Payment save(Payment payment) {
        String sql = """
        INSERT INTO payments (payment_type, amount, currency, description, reference_number,
                            status, category_id, created_by, payment_date, created_at, updated_at, category_name,
                            client_vendor_name, account_details)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING id
        """;

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, payment.getPaymentType().toString());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setString(3, payment.getCurrency());
            stmt.setString(4, payment.getDescription());
            stmt.setString(5, payment.getReferenceNumber());
            stmt.setString(6, payment.getStatus().toString());
            stmt.setObject(7, payment.getCategory().getId());
            stmt.setObject(8, payment.getCreatedBy().getId());
            stmt.setTimestamp(9, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(12, payment.getClientVendorName());
            stmt.setString(13, payment.getAccountDetails());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                payment.setId((Long) rs.getObject(1));
            }

            return payment;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving payment", e);
        }
    }

    @Override
    public Optional<Payment> findById(Long id) {
        String sql = """
        SELECT p.*, pc.name as category_name, pc.description as category_description,
               u.username as user_name, u.email as user_email
        FROM payments p
        LEFT JOIN payment_categories pc ON p.category_id = pc.id
        LEFT JOIN users u ON p.created_by = u.id
        WHERE p.id = ?
        """;

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToPayment(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payment by id", e);
        }
    }

    @Override
    public List<Payment> findAll() {
        String sql = """
    SELECT p.*, pc.name as category_name, pc.description as category_description,
           u.username as user_name, u.email as user_email
    FROM payments p
    LEFT JOIN payment_categories pc ON p.category_id = pc.id
    LEFT JOIN users u ON p.created_by = u.id
    ORDER BY p.created_at DESC
    """;

        return executeQuery(sql);
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        String sql = """
        SELECT p.*, pc.name as category_name, pc.description as category_description,
               u.username as user_name, u.email as user_email
        FROM payments p
        LEFT JOIN payment_categories pc ON p.category_id = pc.id
        LEFT JOIN users u ON p.created_by = u.id
        WHERE p.status = ?
        ORDER BY p.created_at DESC
        """;

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.toString());
            return executeQuery(stmt);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payments by status", e);
        }
    }

    @Override
    public List<Payment> findByCategory(PaymentCategory category) {
        String sql = """
        SELECT p.*, pc.name as category_name, pc.description as category_description,
               u.username as user_name, u.email as user_email
        FROM payments p
        LEFT JOIN payment_categories pc ON p.category_id = pc.id
        LEFT JOIN users u ON p.created_by = u.id
        WHERE p.category_id = ?
        ORDER BY p.created_at DESC
        """;

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, category.getId());
            return executeQuery(stmt);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payments by category", e);
        }
    }

    @Override
    public List<Payment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = """
        SELECT p.*, pc.name as category_name, pc.description as category_description,
               u.username as user_name, u.email as user_email
        FROM payments p
        LEFT JOIN payment_categories pc ON p.category_id = pc.id
        LEFT JOIN users u ON p.created_by = u.id
        WHERE p.payment_date BETWEEN ? AND ?
        ORDER BY p.payment_date DESC
        """;

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            return executeQuery(stmt);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payments by date range", e);
        }
    }

    @Override
    public List<Payment> findByCreatedBy(Long userId) {
        String sql = """
        SELECT p.*, pc.name as category_name, pc.description as category_description,
               u.username as user_name, u.email as user_email
        FROM payments p
        LEFT JOIN payment_categories pc ON p.category_id = pc.id
        LEFT JOIN users u ON p.created_by = u.id
        WHERE p.created_by = ?
        ORDER BY p.created_at DESC
        """;

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            return executeQuery(stmt);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payments by created by", e);
        }
    }

    @Override
    public boolean existsByReferenceNumber(String referenceNumber) {
        String sql = "SELECT COUNT(*) FROM payments WHERE reference_number = ?";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, referenceNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking reference number", e);
        }
    }
//    @Override
//    public List<Payment> findByType(PaymentType paymentType) {
//        String sql = """
//        SELECT p.*, pc.name as category_name, pc.description as category_description,
//               u.username as user_name, u.email as user_email
//        FROM payments p
//        LEFT JOIN payment_categories pc ON p.category_id = pc.id
//        LEFT JOIN users u ON p.created_by = u.id
//        WHERE p.payment_type = ?
//        ORDER BY p.created_at DESC
//        """;
//
//        try (Connection conn = DatabaseConnectionManager.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setString(1, paymentType.toString());
//            return executeQuery(stmt);
//        } catch (SQLException e) {
//            throw new RuntimeException("Error finding payments by type", e);
//        }
//    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM payments WHERE id = ?";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting payment failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting payment", e);
        }
    }

    private List<Payment> executeQuery(String sql) {
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
            return payments;
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query", e);
        }
    }

    private List<Payment> executeQuery(PreparedStatement stmt) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        }
        return payments;
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();

        // Handle ID properly - check type and convert if needed
        Object idObj = rs.getObject("id");
        if (idObj instanceof Integer) {
            payment.setId(((Integer) idObj).longValue());
        } else if (idObj instanceof Long) {
            payment.setId((Long) idObj);
        } else if (idObj != null) {
            // Try to parse as Long if not null
            payment.setId(Long.valueOf(idObj.toString()));
        }

        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setCurrency(rs.getString("currency"));
        payment.setDescription(rs.getString("description"));
        payment.setReferenceNumber(rs.getString("reference_number"));
        payment.setClientVendorName(rs.getString("client_vendor_name"));
        payment.setAccountDetails(rs.getString("account_details"));

        payment.setPaymentType(PaymentType.valueOf(rs.getString("payment_type")));
        payment.setStatus(PaymentStatus.valueOf(rs.getString("status")));

        payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            payment.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            payment.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        // Safely handle category - may be null
        Long categoryId = rs.getObject("category_id") != null ?
                ((Number)rs.getObject("category_id")).longValue() : null;
        if (categoryId != null) {
            PaymentCategory category = new PaymentCategory();
            category.setId(categoryId);

            // Safely get category_name and description if they exist
            try {
                category.setName(rs.getString("category_name"));
                category.setDescription(rs.getString("category_description"));
            } catch (SQLException e) {
                // Column not found, set to null
                category.setName(null);
                category.setDescription(null);
            }

            payment.setCategory(category);
        }

        // Safely handle user - may be null
        Object createdById = rs.getObject("created_by");
        if (createdById != null) {
            User user = new User();
            try {
                user.setId(UUID.fromString(createdById.toString()));
                user.setUsername(rs.getString("user_name"));
                user.setEmail(rs.getString("user_email"));
            } catch (SQLException | IllegalArgumentException e) {
                // Handle missing columns or invalid UUID gracefully
            }
            payment.setCreatedBy(user);
        }

        return payment;
    }
}