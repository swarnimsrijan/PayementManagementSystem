package paymentManagementSystem.enums;


public enum UserRole {
    ADMIN("Administrator"),
    FINANCE_MANAGER("Finance Manager"),
    VIEWER("Viewer");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}