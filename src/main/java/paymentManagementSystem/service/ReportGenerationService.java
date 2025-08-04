package paymentManagementSystem.service;

import paymentManagementSystem.dto.response.ReportDTO;

public interface ReportGenerationService {
    String generatePdfReport(ReportDTO report);
    String generateExcelReport(ReportDTO report);
    String generateCsvReport(ReportDTO report);
}