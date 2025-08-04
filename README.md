# 💰 Payment Management System

A comprehensive fintech solution for managing incoming and outgoing payments, with robust security, audit trails, and financial reporting.

---

## 📋 Table of Contents

- [🎯 Overview](#-overview)
- [✨ Features](#-features)
- [🛠 Technology Stack](#-technology-stack)
- [🗄 Database Schema](#-database-schema)
- [🏗 System Architecture](#-system-architecture)
- [🚀 Installation](#-installation)
- [📖 Usage](#-usage)
- [📈 Reporting](#-reporting)
- [📚 API Documentation](#-api-documentation)
- [🤝 Contributing](#-contributing)
- [🪪 License](#-license)

---

## 🎯 Overview

The **Payment Management System** helps fintech teams efficiently track and manage all financial transactions. It provides a secure and auditable platform for finance managers to handle payments, generate reports, and maintain a detailed change history.

### Problem Statement

This system addresses key needs such as:

- ✅ Tracking incoming payments (e.g., client invoices)  
- ✅ Managing outgoing payments (e.g., salaries, vendor settlements)  
- ✅ Categorizing and monitoring payment statuses  
- ✅ Generating insightful financial reports  
- ✅ Maintaining audit logs for every transaction  
- ✅ Implementing role-based access control  

---

## ✨ Features

### Core Functionalities

- 🔁 **Payment Management** — Create, update, and view payment records  
- 🚦 **Status Tracking** — Track statuses: `Pending`, `Processing`, `Completed`, `Failed`, `Cancelled`  
- 🗂 **Categorization** — Organize payments by type: `Salary`, `Vendor`, `Client`  
- 📊 **Reporting** — Generate monthly and quarterly financial reports  
- 🕵️‍♂️ **Audit Logging** — Log every user and system action  
- 👥 **User Roles** — Role-based access for Admins, Finance Managers, and Viewers  

### Security Features

- 🔐 User authentication & authorization  
- ✅ Data integrity validation  
- 🧾 Full audit history  
- 🔒 Role-based permissions  

---

## 🛠 Technology Stack

- **Language**: Java 11+  
- **Framework**: Spring Boot  
- **Database**: H2 (development), PostgreSQL (production)  
- **Build Tool**: Maven  
- **Architecture**: Clean Architecture adhering to SOLID principles  
- **Design Patterns**: Repository, DTO, Builder  

---

## 🗄 Database Schema

### ER Diagram (Mermaid)

```mermaid
erDiagram
    USERS {
        UUID id PK
        VARCHAR username
        VARCHAR email UK
        VARCHAR password_hash
        VARCHAR phone_number
        TEXT address
        VARCHAR city
        ENUM role
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    PAYMENTS {
        BIGINT id PK
        ENUM payment_type
        DECIMAL amount
        VARCHAR currency
        TEXT description
        ENUM status
        TIMESTAMP payment_date
        TIMESTAMP created_at
        TIMESTAMP updated_at
        UUID user_id FK
        BIGINT category_id FK
        VARCHAR client_vendor_name
        TEXT account_details
    }

    CATEGORIES {
        BIGINT id PK
        VARCHAR name UK
        TEXT description
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    AUDIT_LOGS {
        BIGINT id PK
        VARCHAR entity_type
        VARCHAR entity_id
        VARCHAR action
        TEXT old_values
        TEXT new_values
        UUID user_id FK
        TIMESTAMP timestamp
    }

    REPORTS {
        BIGINT id PK
        ENUM report_type
        ENUM period_type
        DATE start_date
        DATE end_date
        DECIMAL total_incoming
        DECIMAL total_outgoing
        DECIMAL net_amount
        UUID generated_by FK
        TIMESTAMP generated_at
        VARCHAR file_path
    }

    USERS ||--o{ PAYMENTS : creates
    CATEGORIES ||--o{ PAYMENTS : categorizes
    USERS ||--o{ AUDIT_LOGS : performs
    PAYMENTS ||--o{ AUDIT_LOGS : tracked_by
    USERS ||--o{ REPORTS : generates
