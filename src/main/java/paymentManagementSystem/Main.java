package paymentManagementSystem;

import paymentManagementSystem.dao.UserDAO;
import paymentManagementSystem.dao.UserDAOImplementation;
import paymentManagementSystem.dto.UserDTO;
import paymentManagementSystem.dto.request.CreatePaymentRequest;
import paymentManagementSystem.dto.request.UpdatePaymentStatusRequest;
import paymentManagementSystem.dto.request.GenerateReportRequest;
import paymentManagementSystem.dto.response.PaymentResponse;
import paymentManagementSystem.dto.response.ReportDTO;
import paymentManagementSystem.entity.*;
import paymentManagementSystem.enums.PaymentStatus;
import paymentManagementSystem.enums.PaymentType;
import paymentManagementSystem.enums.PeriodType;
import paymentManagementSystem.enums.ReportType;
import paymentManagementSystem.enums.UserRole;
import paymentManagementSystem.service.OnboardUserService;
import paymentManagementSystem.service.PaymentService;
import paymentManagementSystem.service.ReportGenerationService;
import paymentManagementSystem.service.OnboardUserServiceImpl;
import paymentManagementSystem.util.AuditLogger;
import paymentManagementSystem.util.DatabaseConnectionManager;
import paymentManagementSystem.util.DatabaseInitializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static Scanner scanner;
    private static OnboardUserService userService;
    private static PaymentService paymentService;
    private static ReportGenerationService reportService;
    private static User currentUser = null;

    public static void main(String[] args) {
//        DatabaseInitializer.dropAllTables();
        DatabaseInitializer.createTables();
        initialize();
        showLoginMenu();
    }

    private static void initialize() {
        scanner = new Scanner(System.in);

        // Initialize services
        AuditLogger auditLogger = new AuditLogger();
        UserDAO userDAO = new UserDAOImplementation();
        userService = new OnboardUserServiceImpl(userDAO, auditLogger);

        paymentManagementSystem.repository.PaymentRepositoryImpl paymentRepository = new paymentManagementSystem.repository.PaymentRepositoryImpl();
        paymentService = new paymentManagementSystem.service.PaymentServiceImpl(paymentRepository, auditLogger);

        reportService = new paymentManagementSystem.service.ReportGenerationServiceImpl();

        System.out.println("Payment Management System initialized successfully.");
    }

    private static void showLoginMenu() {
        while (true) {
            System.out.println("\n===== Payment Management System =====");
            System.out.println("1. Login");
            System.out.println("2. Register New User");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");

            int choice = getIntInput();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    registerUser();
                    break;
                case 3:
                    System.out.println("Exiting system. Goodbye!");
                    DatabaseConnectionManager.closeConnection();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void login() {
        System.out.println("\n=== Login ===");
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        Optional<UserDTO> userOptional = userService.getUserByEmail(email);
        if (userOptional.isPresent()) {
            // In a real app, you would check the password here
            UserDTO userDTO = userOptional.get();
            currentUser = User.builder()
                    .id(userDTO.getId())
                    .username(userDTO.getUsername())
                    .email(userDTO.getEmail())
                    .role(UserRole.VIEWER)
                    .build();
            System.out.println("Welcome, " + currentUser.getUsername() + "!");
            showMainMenu();
        } else {
            System.out.println("User not found. Please try again or register.");
        }
    }

    private static void registerUser() {
        System.out.println("\n=== Register New User ===");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        // Check if email already exists
        if (userService.isEmailTaken(email)) {
            System.out.println("This email is already registered. Please use another email.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();

        System.out.print("Enter address: ");
        String address = scanner.nextLine();

        System.out.print("Enter city: ");
        String city = scanner.nextLine();

        // Create UserDTO with all required fields matching database schema
        UserDTO newUserDTO = UserDTO.builder()
                .id(UUID.randomUUID())
                .username(username)
                .passwordHash(password)// Maps to username in database (required)
                .email(email)
                .phoneNumber(phone)
                .address(address)
                .city(city)
                .role("VIEWER")  // Default role (maps to role in database)
                .build();

        System.out.println(newUserDTO);

        try {
            userService.createUser(newUserDTO);
            System.out.println("User registered successfully! You can now login.");
        } catch (Exception e) {
            System.out.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static void showMainMenu() {
        while (true) {
            System.out.println("\n===== Payment Management System =====");
            System.out.println("1. Manage Payments");
            System.out.println("2. Generate Reports");
            System.out.println("3. Manage Users");
            System.out.println("4. Logout");
            System.out.print("Select an option: ");

            int choice = getIntInput();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    showPaymentsMenu();
                    break;
                case 2:
                    showReportsMenu();
                    break;
                case 3:
                    showUserManagementMenu();
                    break;
                case 4:
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void showPaymentsMenu() {
        while (true) {
            System.out.println("\n=== Payments Management ===");
            System.out.println("1. Add New Payment");
//            System.out.println("2. Update Payment Status");
            System.out.println("2. View All Payments");
            System.out.println("3. View Payments by Type");
            System.out.println("4. View Payments by Status");
            System.out.println("5. Return to Main Menu");
            System.out.print("Select an option: ");

            int choice = getIntInput();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addNewPayment();
                    break;
//                case 2:
//                    updatePaymentStatus();
//                    break;
                case 2:
                    viewAllPayments();
                    break;
                case 3:
                    viewPaymentsByType();
                    break;
                case 4:
                    viewPaymentsByStatus();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addNewPayment() {
        System.out.println("\n=== Add New Payment ===");

        System.out.print("Enter amount: ");
        BigDecimal amount = BigDecimal.valueOf(getDoubleInput());
        scanner.nextLine(); // Consume newline

        System.out.print("Enter currency (e.g., USD): ");
        String currency = scanner.nextLine();

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter client/vendor name: ");
        String clientVendorName = scanner.nextLine();
//        System.out.print("Enter category name: ");
//        String categoryName = scanner.nextLine();

        System.out.print("Enter account details: ");
        String accountDetails = scanner.nextLine();

        System.out.println("Select payment type:");
        System.out.println("1. Incoming");
        System.out.println("2. Outgoing");
        int typeChoice = getIntInput();
        PaymentType paymentType = (typeChoice == 1) ? PaymentType.INCOMING : PaymentType.OUTGOING;

        System.out.print("Enter category ID: ");
        Long categoryId = (long) getIntInput();

        // Create payment request
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .paymentType(paymentType)
                .amount(amount)
                .currency(currency)
                .description(description)
                .categoryId(categoryId)
                .paymentDate(LocalDateTime.now())
                .clientVendorName(clientVendorName)
                .accountDetails(accountDetails)
                .build();
        System.out.println(request);

        try {
            System.out.println(currentUser.getId().toString());
            PaymentResponse payment = paymentService.createPayment(request, currentUser.getId().toString());
            System.out.println(payment);
            System.out.println("Payment created successfully with ID: " + payment.getId());
        } catch (Exception e) {
            System.out.println("Error creating payment: " + e.getMessage());
        }
    }

    private static void updatePaymentStatus() {
        System.out.println("\n=== Update Payment Status ===");

        System.out.print("Enter payment ID: ");
        Long paymentId = (long) getIntInput();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter reason for status change: ");
        String changeReason = scanner.nextLine();

        System.out.print("Enter additional notes (optional): ");
        String additionalNotes = scanner.nextLine();

        System.out.println("Select new status:");
        System.out.println("1. Pending");
        System.out.println("2. Processing");
        System.out.println("3. Completed");
        System.out.println("4. Failed");
        System.out.println("5. Cancelled");

        int statusChoice = getIntInput();
        PaymentStatus newStatus;

        switch (statusChoice) {
            case 1: newStatus = PaymentStatus.PENDING; break;
            case 2: newStatus = PaymentStatus.PROCESSING; break;
            case 3: newStatus = PaymentStatus.COMPLETED; break;
            case 4: newStatus = PaymentStatus.FAILED; break;
            default: newStatus = PaymentStatus.CANCELLED;
        }

        UpdatePaymentStatusRequest request = UpdatePaymentStatusRequest.builder()
                .paymentId(paymentId)
                .newStatus(newStatus)
                .changeReason(changeReason)
                .additionalNotes(additionalNotes)
                .build();

        try {
            PaymentResponse updatedPayment = paymentService.updatePaymentStatus(request, currentUser.getId().toString());
            System.out.println("Payment status updated successfully to: " + updatedPayment.getStatus());
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllPayments() {
        System.out.println("\n=== All Payments ===");
        List<PaymentResponse> payments = paymentService.getAllPayments();
        displayPayments(payments);
    }

    private static void viewPaymentsByType() {
        System.out.println("\n=== View Payments by Type ===");
        System.out.println("Select payment type:");
        System.out.println("1. Incoming");
        System.out.println("2. Outgoing");

        int typeChoice = getIntInput();
        PaymentType paymentType = (typeChoice == 1) ? PaymentType.INCOMING : PaymentType.OUTGOING;

        List<PaymentResponse> payments = paymentService.getPaymentsByType(paymentType);
        displayPayments(payments);
    }

    private static void viewPaymentsByStatus() {
        System.out.println("\n=== View Payments by Status ===");
        System.out.println("Select status:");
        System.out.println("1. Pending");
        System.out.println("2. Processing");
        System.out.println("3. Completed");
        System.out.println("4. Failed");
        System.out.println("5. Cancelled");

        int statusChoice = getIntInput();
        PaymentStatus status;

        switch (statusChoice) {
            case 1: status = PaymentStatus.PENDING; break;
            case 2: status = PaymentStatus.PROCESSING; break;
            case 3: status = PaymentStatus.COMPLETED; break;
            case 4: status = PaymentStatus.FAILED; break;
            default: status = PaymentStatus.CANCELLED;
        }

        List<PaymentResponse> payments = paymentService.getPaymentsByStatus(status);
        displayPayments(payments);
    }

    private static void showReportsMenu() {
        while (true) {
            System.out.println("\n=== Reports ===");
            System.out.println("1. Generate Monthly Report");
            System.out.println("2. Generate Quarterly Report");
            System.out.println("3. Return to Main Menu");
            System.out.print("Select an option: ");

            int choice = getIntInput();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    generateMonthlyReport();
                    break;
                case 2:
                    generateQuarterlyReport();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void generateMonthlyReport() {
        System.out.println("\n=== Generate Monthly Report ===");

        System.out.print("Enter year (e.g., 2023): ");
        int year = getIntInput();

        System.out.print("Enter month (1-12): ");
        int month = getIntInput();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        GenerateReportRequest request = GenerateReportRequest.builder()
                .reportType(ReportType.FINANCIAL)
                .periodType(PeriodType.MONTHLY)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        try {
            ReportDTO report = paymentService.generateReport(request);
            displayReport(report);

            System.out.println("\nDo you want to save this report as PDF? (y/n): ");
            String choice = scanner.next();

            if (choice.equalsIgnoreCase("y")) {
                String fileName = reportService.generatePdfReport(report);
                System.out.println("Report saved as: " + fileName);
            }
        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    private static void generateQuarterlyReport() {
        System.out.println("\n=== Generate Quarterly Report ===");

        System.out.print("Enter year (e.g., 2023): ");
        int year = getIntInput();

        System.out.print("Enter quarter (1-4): ");
        int quarter = getIntInput();

        // Calculate quarter dates
        LocalDate startDate = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);

        GenerateReportRequest request = GenerateReportRequest.builder()
                .reportType(ReportType.FINANCIAL)
                .periodType(PeriodType.QUARTERLY)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        try {
            ReportDTO report = paymentService.generateReport(request);
            displayReport(report);

            System.out.println("\nDo you want to save this report as PDF? (y/n): ");
            String choice = scanner.next();

            if (choice.equalsIgnoreCase("y")) {
                String fileName = reportService.generatePdfReport(report);
                System.out.println("Report saved as: " + fileName);
            }
        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    private static void showUserManagementMenu() {
        while (true) {
            System.out.println("\n=== User Management ===");
            System.out.println("1. View All Users");
            System.out.println("2. Search Users by Name");
            System.out.println("3. Update My Profile");
            System.out.println("4. Return to Main Menu");
            System.out.print("Select an option: ");

            int choice = getIntInput();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    searchUsersByName();
                    break;
                case 3:
                    updateUserProfile();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void viewAllUsers() {
        System.out.println("\n=== All Users ===");
        List<UserDTO> users = userService.getAllUsers();
        displayUsers(users);
    }

    private static void searchUsersByName() {
        System.out.println("\n=== Search Users by Name ===");
        System.out.print("Enter name to search: ");
        String name = scanner.nextLine();

        List<UserDTO> users = userService.getUsersByName(name);
        displayUsers(users);
    }

    private static void updateUserProfile() {
        System.out.println("\n=== Update My Profile ===");

        // Get current user details
        Optional<UserDTO> userOptional = userService.getUserById(currentUser.getId());
        if (!userOptional.isPresent()) {
            System.out.println("Error: Could not retrieve your profile.");
            return;
        }

        UserDTO userDTO = userOptional.get();

        System.out.println("Current name: " + userDTO.getUsername());
        System.out.print("Enter new name (or press enter to keep current): ");
        String name = scanner.nextLine();
        if (!name.trim().isEmpty()) {
            userDTO.setUsername(name);
        }

        System.out.println("Current phone: " + userDTO.getPhoneNumber());
        System.out.print("Enter new phone (or press enter to keep current): ");
        String phone = scanner.nextLine();
        if (!phone.trim().isEmpty()) {
            userDTO.setPhoneNumber(phone);
        }

        System.out.println("Current address: " + userDTO.getAddress());
        System.out.print("Enter new address (or press enter to keep current): ");
        String address = scanner.nextLine();
        if (!address.trim().isEmpty()) {
            userDTO.setAddress(address);
        }

        System.out.println("Current city: " + userDTO.getCity());
        System.out.print("Enter new city (or press enter to keep current): ");
        String city = scanner.nextLine();
        if (!city.trim().isEmpty()) {
            userDTO.setCity(city);
        }

        try {
            UserDTO updatedUser = userService.updateUser(userDTO);
            System.out.println("Profile updated successfully!");

            // Update current user
            currentUser = User.builder()
                    .id(updatedUser.getId())
                    .username(updatedUser.getUsername())
                    .email(updatedUser.getEmail())
                    .role(currentUser.getRole())
                    .build();
        } catch (Exception e) {
            System.out.println("Error updating profile: " + e.getMessage());
        }
    }

    private static void displayPayments(List<PaymentResponse> payments) {
        if (payments.isEmpty()) {
            System.out.println("No payments found.");
            return;
        }

        System.out.println("\nID | Type | Category | Amount | Status | Created At | Client/Vendor");
        System.out.println("------------------------------------------------------------------------");

        for (PaymentResponse payment : payments) {
            System.out.printf("%-10s | %-8s | %-15s | %s %-8.2f | %-10s | %s | %s\n",
                    payment.getId(),
                    payment.getPaymentType(),
                    payment.getCategoryName(),
                    payment.getCurrency(),
                    payment.getAmount(),
                    payment.getStatus(),
                    payment.getCreatedAt() != null ? payment.getCreatedAt().toString() : "N/A",
                    payment.getClientVendorName()
            );
        }
    }

    private static void displayUsers(List<UserDTO> users) {
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("\nID | Name | Email | Phone | City");
        System.out.println("----------------------------------");

        for (UserDTO user : users) {
            System.out.printf("%-36s | %-20s | %-25s | %-15s | %s\n",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getCity()
            );
        }
    }

    private static void displayReport(ReportDTO report) {
        System.out.println("\n=== " + report.getReportType() + " ===");
        System.out.println("Period: " + report.getStartDate() + " to " + report.getEndDate());
        System.out.println("Total Incoming: $" + String.format("%.2f", report.getTotalIncoming()));
        System.out.println("Total Outgoing: $" + String.format("%.2f", report.getTotalOutgoing()));
        System.out.println("Net Amount: $" + String.format("%.2f", report.getNetAmount()));

        System.out.println("\nPayments in this period:");
        displayPayments(report.getPayments());
    }

    private static int getIntInput() {
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine(); // Clear the invalid input
            return -1;
        }
    }

    private static double getDoubleInput() {
        try {
            return scanner.nextDouble();
        } catch (Exception e) {
            scanner.nextLine(); // Clear the invalid input
            return -1;
        }
    }
}