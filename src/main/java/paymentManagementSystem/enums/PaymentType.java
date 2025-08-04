package paymentManagementSystem.enums;


public enum PaymentType {
    INCOMING("Incoming Payment"),
    OUTGOING("Outgoing Payment");

    private final String description;

    PaymentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
