package paymentManagementSystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import paymentManagementSystem.enums.PaymentStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentStatusRequest {
    private Long paymentId;
    private PaymentStatus newStatus;
    private String changeReason;
    private String additionalNotes;
}
