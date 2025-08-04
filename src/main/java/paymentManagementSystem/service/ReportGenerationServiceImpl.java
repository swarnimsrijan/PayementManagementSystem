package paymentManagementSystem.service;

import paymentManagementSystem.dto.response.ReportDTO;
import paymentManagementSystem.service.ReportGenerationService;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportGenerationServiceImpl implements ReportGenerationService {

    @Override
    public String generatePdfReport(ReportDTO report) {
        String fileName = "report_" + report.getReportType().toString().toLowerCase() + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

        // For now, create a simple text file (you can integrate a PDF library later)
        try (FileWriter writer = new FileWriter(fileName.replace(".pdf", ".txt"))) {
            writer.write("=== " + report.getReportType() + " REPORT ===\n");
            writer.write("Period: " + report.getStartDate() + " to " + report.getEndDate() + "\n");
            writer.write("Total Incoming: $" + report.getTotalIncoming() + "\n");
            writer.write("Total Outgoing: $" + report.getTotalOutgoing() + "\n");
            writer.write("Net Amount: $" + report.getNetAmount() + "\n");
            writer.write("\nGenerated: " + LocalDateTime.now() + "\n");

            return fileName.replace(".pdf", ".txt");
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }

    @Override
    public String generateExcelReport(ReportDTO report) {
        // Placeholder implementation
        return "excel_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
    }

    @Override
    public String generateCsvReport(ReportDTO report) {
        // Placeholder implementation
        return "csv_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
    }
}