package paymentManagementSystem.dto.response;

import paymentManagementSystem.enums.PaymentStatus;
import paymentManagementSystem.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private PaymentType paymentType;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String referenceNumber;
    private PaymentStatus status;
    private String categoryName;
    private String createdByUsername;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private String clientVendorName;
}