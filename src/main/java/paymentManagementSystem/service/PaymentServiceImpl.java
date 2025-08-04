package paymentManagementSystem.service;

import paymentManagementSystem.dto.request.CreatePaymentRequest;
import paymentManagementSystem.dto.request.GenerateReportRequest;
import paymentManagementSystem.dto.request.UpdatePaymentStatusRequest;
import paymentManagementSystem.dto.response.PaymentResponse;
import paymentManagementSystem.dto.response.ReportDTO;
import paymentManagementSystem.entity.Payment;
import paymentManagementSystem.entity.PaymentCategory;
import paymentManagementSystem.entity.User;
import paymentManagementSystem.enums.PaymentStatus;
import paymentManagementSystem.enums.PaymentType;
import paymentManagementSystem.repository.PaymentRepository;
import paymentManagementSystem.util.AuditLogger;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AuditLogger auditLogger;

    public PaymentServiceImpl(PaymentRepository paymentRepository, AuditLogger auditLogger) {
        this.paymentRepository = paymentRepository;
        this.auditLogger = auditLogger;
    }

    @Override
    public PaymentResponse createPayment(CreatePaymentRequest request, String userId) {
        // Validate input
        if (request.getCategoryId() <= 0) {
            throw new IllegalArgumentException("Invalid category ID: " + request.getCategoryId());
        }

        // Create Payment entity
        Payment payment = Payment.builder()
                // Don't set ID - let the database generate it
                .paymentType(request.getPaymentType())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .referenceNumber(generateReferenceNumber())
                .status(PaymentStatus.PENDING)
                .paymentDate(request.getPaymentDate())
                .clientVendorName(request.getClientVendorName())
                .accountDetails(request.getAccountDetails())
                .build();

        // Set category
        PaymentCategory category = new PaymentCategory();
        category.setId(request.getCategoryId());
        payment.setCategory(category);

        // Set created by user
        User user = new User();
        user.setId(UUID.fromString(userId));
        payment.setCreatedBy(user);

        // Save payment
        Payment savedPayment = paymentRepository.save(payment);

        // Log audit
        auditLogger.logAction(userId, "PAYMENT_CREATED", savedPayment.getId().toString(),
                null, "Payment created");

        return convertToResponse(savedPayment);
    }

    @Override
    public PaymentResponse updatePaymentStatus(UpdatePaymentStatusRequest request, String userId) {
        // Implementation here
        return PaymentResponse.builder().build();
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByType(PaymentType paymentType) {
        // First, add findByType method to PaymentRepository interface
        // Then implement it in PaymentRepositoryImpl similar to findByStatus

        // For now, let's filter from all payments as a workaround
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getPaymentType() == paymentType)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PaymentResponse> getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(this::convertToResponse);
    }


    @Override
    public ReportDTO generateReport(GenerateReportRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Start and end dates are required");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        // Convert LocalDate to LocalDateTime
        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = request.getEndDate().atTime(23, 59, 59);

        // Get all payments within the date range
        List<Payment> payments = paymentRepository.findAll().stream()
                .filter(payment ->
                        !payment.getPaymentDate().isBefore(startDateTime) &&
                                !payment.getPaymentDate().isAfter(endDateTime))
                .collect(Collectors.toList());

        List<PaymentResponse> paymentResponses = payments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        BigDecimal totalIncoming = payments.stream()
                .filter(payment -> payment.getPaymentType() == PaymentType.INCOMING)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutgoing = payments.stream()
                .filter(payment -> payment.getPaymentType() == PaymentType.OUTGOING)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netAmount = totalIncoming.subtract(totalOutgoing);

        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return ReportDTO.builder()
                .reportType(request.getReportType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalIncoming(totalIncoming)
                .totalOutgoing(totalOutgoing)
                .netAmount(netAmount)
                .payments(paymentResponses)
                .generatedBy("System") // optionally pass actual user info
                .generatedAt(timestamp)
                .build();
    }


    private PaymentResponse convertToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentType(payment.getPaymentType())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .description(payment.getDescription())
                .status(payment.getStatus())
                .clientVendorName(payment.getClientVendorName())
                .paymentDate(payment.getPaymentDate())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    private String generateReferenceNumber() {
        return "PAY-" + System.currentTimeMillis();
    }
}