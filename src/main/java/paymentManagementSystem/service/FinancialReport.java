package paymentManagementSystem.service;


import paymentManagementSystem.entity.User;
import paymentManagementSystem.enums.PeriodType;
import paymentManagementSystem.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReport {
    private Long id;
    private ReportType reportType;
    private PeriodType periodType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String filePath;
    private User generatedBy;
    private LocalDateTime generatedAt;
    private BigDecimal totalIncoming;
    private BigDecimal totalOutgoing;
    private BigDecimal netAmount;
}
