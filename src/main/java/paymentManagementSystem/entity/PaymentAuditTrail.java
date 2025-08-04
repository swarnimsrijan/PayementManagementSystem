package paymentManagementSystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import paymentManagementSystem.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAuditTrail {
    private UUID id;
    private Payment payment;
    private PaymentStatus oldStatus;
    private PaymentStatus newStatus;
    private String changedByUsername;
    private String changeReason;
    private LocalDateTime changedAt;
    private String additionalNotes;
}


