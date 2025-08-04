package paymentManagementSystem.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void createTables() {
        try (Connection conn = DatabaseConnectionManager.getConnection()) {
            createUsersTable(conn);
            createPaymentCategoriesTable(conn);
            createPaymentsTable(conn);
            createAuditLogsTable(conn);
            insertDefaultCategories(conn);
            System.out.println("All database tables created successfully!");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create database tables", e);
        }
    }

    private static void createUsersTable(Connection conn) throws SQLException {
        String sql = """
        CREATE TABLE IF NOT EXISTS users (
             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
             username VARCHAR(50) NOT NULL UNIQUE,
             password_hash VARCHAR(255),
             email VARCHAR(100) NOT NULL UNIQUE,
             phone_number VARCHAR(20),
             address TEXT,
             city VARCHAR(100),
             role VARCHAR(20) NOT NULL DEFAULT 'VIEWER' CHECK (role IN ('ADMIN', 'FINANCE_MANAGER', 'VIEWER'))
         )
    """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Users table created/verified.");
        }
    }

    private static void createPaymentCategoriesTable(Connection conn) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS payment_categories (
                id SERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL UNIQUE,
                description TEXT,
                is_active BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Payment categories table created/verified.");
        }
    }

    private static void createPaymentsTable(Connection conn) throws SQLException {
        String sql = """
    CREATE TABLE IF NOT EXISTS payments (
        id BIGSERIAL PRIMARY KEY,
        payment_type VARCHAR(20) NOT NULL CHECK (payment_type IN ('INCOMING', 'OUTGOING')),
        amount DECIMAL(15, 2) NOT NULL,
        currency VARCHAR(10) NOT NULL DEFAULT 'USD',
        description TEXT,
        reference_number VARCHAR(100) UNIQUE,
        status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED')),
        category_id INTEGER REFERENCES payment_categories(id),
        created_by UUID REFERENCES users(id),
        payment_date TIMESTAMP NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        client_vendor_name VARCHAR(255),
        account_details TEXT
    )
    """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Payments table created/verified.");
        }
    }

    // In DatabaseInitializer.java, update the createAuditLogsTable method:
    private static void createAuditLogsTable(Connection conn) throws SQLException {
        String sql = """
    CREATE TABLE IF NOT EXISTS audit_logs (
        id SERIAL PRIMARY KEY,  
        user_id VARCHAR(255) ,
        action VARCHAR(100) ,
        entity_id VARCHAR(255),
        old_value TEXT,
        new_value TEXT,
        timestamp TIMESTAMP NOT NULL,
        metadata JSON,
        additional_info TEXT
    )
    """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Audit logs table created/verified.");
        }
    }

    private static void insertDefaultCategories(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM payment_categories";
        String insertSql = """
            INSERT INTO payment_categories (name, description) VALUES 
            ('Office Supplies', 'General office supplies and equipment'),
            ('Travel & Entertainment', 'Business travel and client entertainment expenses'),
            ('Utilities', 'Office utilities like electricity, internet, phone'),
            ('Professional Services', 'Consulting, legal, accounting services'),
            ('Marketing & Advertising', 'Marketing campaigns and advertising expenses'),
            ('Software & Subscriptions', 'Software licenses and subscription services'),
            ('Maintenance & Repairs', 'Equipment and facility maintenance'),
            ('Employee Benefits', 'Health insurance, bonuses, and other benefits'),
            ('Client Payments', 'Payments received from clients'),
            ('Vendor Payments', 'Payments made to vendors and suppliers')
            """;

        try (Statement stmt = conn.createStatement()) {
            var rs = stmt.executeQuery(checkSql);
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute(insertSql);
                System.out.println("Default payment categories inserted.");
            } else {
                System.out.println("Payment categories already exist.");
            }
        }
    }

    public static void dropAllTables() {
        try (Connection conn = DatabaseConnectionManager.getConnection()) {
            String[] tables = {"audit_logs", "payments", "payment_categories", "users"};

            try (Statement stmt = conn.createStatement()) {
                for (String table : tables) {
                    stmt.execute("DROP TABLE IF EXISTS " + table + " CASCADE");
                    System.out.println("Dropped table: " + table);
                }
            }
            System.out.println("All tables dropped successfully!");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to drop database tables", e);
        }
    }
}