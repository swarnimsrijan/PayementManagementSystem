package paymentManagementSystem.repository;

import paymentManagementSystem.entity.Payment;
import paymentManagementSystem.entity.PaymentCategory;
import paymentManagementSystem.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    List<Payment> findAll();
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByCategory(PaymentCategory category);
    List<Payment> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Payment> findByCreatedBy(Long userId);
    boolean existsByReferenceNumber(String referenceNumber);
    void deleteById(Long id);
}