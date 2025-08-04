package paymentManagementSystem.enums;


public enum ReportType {
    FINANCIAL("Financial Report"),
    SUMMARY("Summary Report"),
    DETAILED("Detailed Report"),
    AUDIT("Audit Report");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
