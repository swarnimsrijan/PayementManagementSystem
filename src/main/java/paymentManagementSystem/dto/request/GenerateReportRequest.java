package paymentManagementSystem.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import paymentManagementSystem.enums.PeriodType;
import paymentManagementSystem.enums.ReportType;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateReportRequest {
    private ReportType reportType;
    private PeriodType periodType;
    private LocalDate startDate;
    private LocalDate endDate;
}
