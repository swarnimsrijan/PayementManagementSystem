package paymentManagementSystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import paymentManagementSystem.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    private PaymentType paymentType;
    private BigDecimal amount;
    private String currency;
    private String description;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime paymentDate;
    private String clientVendorName;
    private String accountDetails;
}