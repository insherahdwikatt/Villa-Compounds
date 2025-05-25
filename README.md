# Villa Compounds Management System

This is a JavaFX-based desktop application designed to manage villa compounds, owners, workers, and related payments. The system supports different user roles like **Admin** and **Manager**, offering distinct functionalities to streamline compound administration.

## 💡 Features

- **Login System** for admins and managers.
- **Owner Management**: Add, search, and update owners.
- **Compound Management**: Manage compounds and villa assignments.
- **Villa Management**: Automatically link villas to owners upon payment.
- **Payment Handling**:
  - Cash or Cheques
  - Automatic villa price retrieval from compound data
- **Worker Payment System**:
  - Auto-fetch bank account based on selected worker ID
- **Report Generation** using JasperReports (for admin role).
- **Search Functionalities** for all modules.

## 🏗️ Technologies Used

- Java 17+
- JavaFX 21
- PostgreSQL
- JDBC
- FXML
- Maven
- JasperReports
- CSS for UI Styling

## 🚧 To Be Added

- Maintenance/Repair table and module for handling repair requests and costs per villa.

## 📦 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/insherahdwikatt/Villa-Compounds.git
   cd Villa-Compounds
