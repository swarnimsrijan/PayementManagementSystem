package paymentManagementSystem.service;

import paymentManagementSystem.dto.request.CreatePaymentRequest;
import paymentManagementSystem.dto.request.UpdatePaymentStatusRequest;
import paymentManagementSystem.dto.request.GenerateReportRequest;
import paymentManagementSystem.dto.response.PaymentResponse;
import paymentManagementSystem.dto.response.ReportDTO;
import paymentManagementSystem.enums.PaymentStatus;
import paymentManagementSystem.enums.PaymentType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {
    PaymentResponse createPayment(CreatePaymentRequest request, String userId);
    PaymentResponse updatePaymentStatus(UpdatePaymentStatusRequest request, String userId);
    List<PaymentResponse> getAllPayments();
    List<PaymentResponse> getPaymentsByType(PaymentType paymentType);
    List<PaymentResponse> getPaymentsByStatus(PaymentStatus status);
    Optional<PaymentResponse> getPaymentById(Long id);
    ReportDTO generateReport(GenerateReportRequest request);
}