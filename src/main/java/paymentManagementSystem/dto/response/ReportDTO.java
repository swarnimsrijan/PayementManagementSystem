package paymentManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import paymentManagementSystem.enums.ReportType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private ReportType reportType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalIncoming;
    private BigDecimal totalOutgoing;
    private BigDecimal netAmount;
    private List<PaymentResponse> payments;
    private String generatedBy;
    private String generatedAt;
}