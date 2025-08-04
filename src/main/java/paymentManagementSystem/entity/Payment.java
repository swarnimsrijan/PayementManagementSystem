package paymentManagementSystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import paymentManagementSystem.enums.PaymentStatus;
import paymentManagementSystem.enums.PaymentType;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private Long id;
    private PaymentType paymentType;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String referenceNumber;
    private PaymentStatus status;
    private String categoryName;
    private PaymentCategory category;
    private User createdBy;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String clientVendorName;
    private String accountDetails;
}
