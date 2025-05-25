package com.example.ri;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PaymentsSearchController {


    @FXML
    private Button btnDelete;

    @FXML
    private Button logoutButton;

    @FXML
    private Button myInfoButton;

    @FXML
    private Button villaButton;

    @FXML
    private Button ownerButton;
    @FXML
    private Button workerButton;

    @FXML
    private Button searchPaymentButton;

    @FXML
    private Button addPaymentButton;
    @FXML
    private Button payWorkerButton;
    @FXML
    private Button paymentButton;
    @FXML
    private AnchorPane PaymentPane;
    @FXML
    private AnchorPane ownerPane;
    @FXML
    private AnchorPane villaPane;
    @FXML
    private AnchorPane ReportPane;
    @FXML
    private AnchorPane workerPane;
    @FXML
    private Button OwnersReport;
    @FXML
    private Button WorkersReport;
    @FXML
    private Button searchOwnerButton;
    @FXML
    private Button addOwnerButton;

    @FXML
    private Button searchVillsButton;
    @FXML
    private Button ReportButton;
    @FXML
    private Button deletPayWorkerButton;
    // Compound panel buttons
    @FXML
    private Button searchWorkerButton;
    @FXML
    private Button addWorkerButton;

    private String role;
    private String userId;
    @FXML
    private Button showAllOwnerButton;

    @FXML
    private Button searchForWorkerButton;
    @FXML
    private Button showAllWorkerButton;

    @FXML
    private Button searchForOwnerButton;

    @FXML
    private ComboBox<String> searchWorkerByCombo;
    @FXML
    private TextField searchOwnerField;
    @FXML
    private ComboBox<String> searchOwnerByCombo;
    @FXML
    private TextField searchWorkerField;


    @FXML private TableView<OwnerPayment> ownerTableView;
    @FXML private TableColumn<OwnerPayment, Integer> ownerIdColumn;
    @FXML private TableColumn<OwnerPayment, String> ownerNameColumn;
    @FXML private TableColumn<OwnerPayment, Integer> villaIdColumn;
    @FXML private TableColumn<OwnerPayment, String> paymentMethodColumn;


    @FXML private TableView<WorkerPayment> workerTableView;
    @FXML private TableColumn<WorkerPayment, Integer> workerIdColumn;
    @FXML private TableColumn<WorkerPayment, String> workerNameColumn;
    @FXML private TableColumn<WorkerPayment, String> bankAccountColumn;
    @FXML private TableColumn<WorkerPayment, String> salaryColumn;
    @FXML private TableColumn<WorkerPayment, String> payDateColumn;

    @FXML private void initialize() {
        searchWorkerByCombo.getItems().addAll("Worker ID", "Worker Name", "Pay Date");
        searchOwnerByCombo.getItems().addAll("Owner ID", "Owner Name", "Villa ID");

        workerIdColumn.setCellValueFactory(new PropertyValueFactory<>("workerId"));
        workerNameColumn.setCellValueFactory(new PropertyValueFactory<>("workerName"));
        bankAccountColumn.setCellValueFactory(new PropertyValueFactory<>("bankAccount"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        payDateColumn.setCellValueFactory(new PropertyValueFactory<>("payDate"));

        ownerIdColumn.setCellValueFactory(new PropertyValueFactory<>("ownerId"));
        ownerNameColumn.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        villaIdColumn.setCellValueFactory(new PropertyValueFactory<>("villaId"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
    }

    @FXML
    private void searchForWorker(ActionEvent event) {
        String criteria = searchWorkerByCombo.getValue();
        String value = searchWorkerField.getText().trim();

        if (criteria == null || value.isEmpty()) {
            showAlert("Validation Error", "Please select a field and enter a value.");
            return;
        }

        String column;
        boolean isNumeric = false;

        switch (criteria) {
            case "Worker ID" -> {
                column = "w.worker_id";
                isNumeric = true;
            }
            case "Worker Name" -> column = "p.first_name || ' ' || p.last_name";
            case "Pay Date" -> column = "wp.date";
            default -> {
                showAlert("Invalid Field", "Please select a valid search field.");
                return;
            }
        }

        ObservableList<WorkerPayment> list = FXCollections.observableArrayList();

        try (Connection conn = DBConnector.connect()) {
            String sql = """
            SELECT 
                w.worker_id,
                p.first_name || ' ' || p.last_name AS name,
                p.bank_account,
                wp.salary,
                wp.date
            FROM workers w
            JOIN person p ON w.worker_id = p.person_id
            JOIN worker_payment wp ON w.worker_id = wp.worker_id
            WHERE """ + column + (isNumeric ? " = ?" : " ILIKE ?");

            // 🛠 Fix: Ensure there is a space before WHERE
            sql = sql.replace("WHERE", " WHERE ");

            PreparedStatement stmt = conn.prepareStatement(sql);
            if (isNumeric) {
                stmt.setInt(1, Integer.parseInt(value));
            } else {
                stmt.setString(1, "%" + value + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new WorkerPayment(
                        rs.getInt("worker_id"),
                        rs.getString("name"),
                        rs.getString("bank_account"),
                        rs.getBigDecimal("salary"),
                        rs.getString("date")
                ));
            }

            workerTableView.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to search workers: " + e.getMessage());
        }
    }


    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void showAllWorker(ActionEvent event) {
        ObservableList<WorkerPayment> list = FXCollections.observableArrayList();

        try (Connection conn = DBConnector.connect()) {
            String sql = """
            SELECT 
                w.worker_id,
                p.first_name || ' ' || p.last_name AS name,
                p.bank_account,
                wp.salary,
                wp.date
            FROM workers w
            JOIN person p ON w.worker_id = p.person_id
            JOIN worker_payment wp ON w.worker_id = wp.worker_id
        """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new WorkerPayment(
                        rs.getInt("worker_id"),
                        rs.getString("name"),
                        rs.getString("bank_account"),
                        rs.getBigDecimal("salary"),
                        rs.getString("date")
                ));
            }

            workerTableView.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load worker payments: " + e.getMessage());
        }
    }


    @FXML
    private void searchForOwner(ActionEvent event) {
        String criteria = searchOwnerByCombo.getValue();
        String value = searchOwnerField.getText().trim();

        if (criteria == null || value.isEmpty()) {
            showAlert("Validation Error", "Please select a field and enter a value.");
            return;
        }

        String column;
        boolean isNumeric = false;

        switch (criteria) {
            case "Owner ID" -> { column = "o.owner_id"; isNumeric = true; }
            case "Owner Name" -> column = "p.first_name || ' ' || p.last_name";
            case "Villa ID" -> { column = "pay.villa_id"; isNumeric = true; }
            default -> {
                showAlert("Invalid Field", "Please select a valid search field.");
                return;
            }
        }

        ObservableList<OwnerPayment> list = FXCollections.observableArrayList();

        try (Connection conn = DBConnector.connect()) {
            String sql = """
            SELECT o.owner_id,
                   p.first_name || ' ' || p.last_name AS owner_name,
                   pay.villa_id,
                   pay.payment_method
            FROM payments pay
            JOIN owners o ON pay.owner_id = o.owner_id
            JOIN person p ON o.owner_id = p.person_id
        """ + " WHERE " + column + (isNumeric ? " = ?" : " ILIKE ?");

            PreparedStatement stmt = conn.prepareStatement(sql);

            if (isNumeric) {
                stmt.setInt(1, Integer.parseInt(value));
            } else {
                stmt.setString(1, "%" + value + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new OwnerPayment(
                        rs.getInt("owner_id"),
                        rs.getString("owner_name"),
                        rs.getInt("villa_id"),
                        rs.getString("payment_method")
                ));
            }

            ownerTableView.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to search owners: " + e.getMessage());
        }
    }

    @FXML
    private void showAllOwner(ActionEvent event) {
        ObservableList<OwnerPayment> list = FXCollections.observableArrayList();

        try (Connection conn = DBConnector.connect()) {
            String sql = """
            SELECT o.owner_id,
                   p.first_name || ' ' || p.last_name AS owner_name,
                   pay.villa_id,
                   pay.payment_method
            FROM payments pay
            JOIN owners o ON pay.owner_id = o.owner_id
            JOIN person p ON o.owner_id = p.person_id
        """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new OwnerPayment(
                        rs.getInt("owner_id"),
                        rs.getString("owner_name"),
                        rs.getInt("villa_id"),
                        rs.getString("payment_method")
                ));
            }

            ownerTableView.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load owner payments: " + e.getMessage());
        }
    }




    @FXML

    private void handleMyInfo(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/Myinfo.fxml"));
            Scene scene = new Scene(loader.load());

            MyinfoController Controller = loader.getController();
            UserInfo userInfo = UserInfo.getInstance();
            Controller.setRoleAndUser(userInfo.getRole(),userInfo.getUserId());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Manager Info");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            UserInfo.getInstance().reset();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handelOwner(ActionEvent event) {
        ((Button) event.getSource()).setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        workerButton.setStyle("");
        paymentButton.setStyle("");
        villaButton.setStyle("");
        ReportButton.setStyle("");

        ownerPane.setVisible(true);
        workerPane.setVisible(false);
        PaymentPane.setVisible(false);
        villaPane.setVisible(false);
        ReportPane.setVisible(false);

    }
    @FXML
    private void handelVilla(ActionEvent event) {


        ((Button) event.getSource()).setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        workerButton.setStyle("");
        ownerButton.setStyle("");
        paymentButton.setStyle("");
        ownerPane.setVisible(false);
        workerPane.setVisible(false);
        PaymentPane.setVisible(false);
        ReportPane.setVisible(false);
        villaPane.setVisible(true);
    }
    @FXML
    private void handelWorker(ActionEvent event) {
        ((Button) event.getSource()).setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        paymentButton.setStyle("");
        ownerButton.setStyle("");
        villaButton.setStyle("");
        ReportButton.setStyle("");

        ownerPane.setVisible(false);
        workerPane.setVisible(true);
        PaymentPane.setVisible(false);
        villaPane.setVisible(false);
        ReportPane.setVisible(false);
    }
    @FXML
    private void handelPayment(ActionEvent event) {

        ((Button) event.getSource()).setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        workerButton.setStyle("");
        ownerButton.setStyle("");
        villaButton.setStyle("");
        ReportButton.setStyle("");

        ownerPane.setVisible(false);
        workerPane.setVisible(false);
        PaymentPane.setVisible(true);
        villaPane.setVisible(false);
        ReportPane.setVisible(false);
    }
    @FXML
    private void handaleReport(ActionEvent event) {
        ((Button) event.getSource()).setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        workerButton.setStyle("");
        ownerButton.setStyle("");
        villaButton.setStyle("");

        ownerPane.setVisible(false);
        workerPane.setVisible(false);
        PaymentPane.setVisible(false);
        villaPane.setVisible(false);
        ReportPane.setVisible(true);
    }
    @FXML
    private void handaleSearchVilla(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/Villas.fxml"));
            Scene scene = new Scene(loader.load());


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Search Villas");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void handaleSearchPayment(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/PaymentsSearch.fxml"));
            Scene scene = new Scene(loader.load());


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Search Payment");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handaleAddPayment(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/PaymentsAdd.fxml"));
            Scene scene = new Scene(loader.load());


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("ADD Payment");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handalePayWorker(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/PayWorker.fxml"));
            Scene scene = new Scene(loader.load());


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Pay Worker");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handaleSearchOwner(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/OwnerSearch.fxml"));
            Scene scene = new Scene(loader.load());


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Search Owner");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handaleAddOwner(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/OwnerAdd.fxml"));
            Scene scene = new Scene(loader.load());


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("ADD Owner");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handaleSearchWorker(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/WorkerSearch.fxml"));
            Scene scene = new Scene(loader.load());


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Search Worker");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handaleAddWorker(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/WorkerAdd.fxml"));
            Scene scene = new Scene(loader.load());


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("ADD Worker");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handaleOwnersReport(ActionEvent event) {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            String url = "jdbc:postgresql://localhost:1234/RealEstate";
            String user = "postgres";
            String password = "raya4039";
            Connection cn = DriverManager.getConnection(url, user, password);
            String jrxmlPath = "C:/Users/DELL/JaspersoftWorkspace/OwnerPaymentReport/OwnerPaymentReport.jrxml";
            InputStream inp = new FileInputStream(new File(jrxmlPath));
            JasperDesign jsp = JRXmlLoader.load(inp);
            JasperReport report = JasperCompileManager.compileReport(jsp);
            JasperPrint filledReport = JasperFillManager.fillReport(report, null, cn);
            JFrame frame = new JFrame("Owners Payment Report");
            frame.getContentPane().add(new JRViewer(filledReport));
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @FXML
    private void handaleWorkersReport(ActionEvent event) {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            String url = "jdbc:postgresql://localhost:1234/RealEstate";
            String user = "postgres";
            String password = "raya4039";
            Connection cn = DriverManager.getConnection(url, user, password);
            String jrxmlPath = "C:/Users/DELL/JaspersoftWorkspace/WorkerPaymentReport/WorkerPaymentReport.jrxml";
            InputStream inp = new FileInputStream(new File(jrxmlPath));
            JasperDesign jsp = JRXmlLoader.load(inp);
            JasperReport report = JasperCompileManager.compileReport(jsp);
            JasperPrint filledReport = JasperFillManager.fillReport(report, null, cn);
            JFrame frame = new JFrame("Workers Payment Report");
            frame.getContentPane().add(new JRViewer(filledReport));
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeletePayment(ActionEvent event) {
        OwnerPayment selected = ownerTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a payment to delete.");
            return;
        }

        int villaId = selected.getVillaId();
        int ownerId = selected.getOwnerId();

        try (Connection conn = DBConnector.connect()) {
            conn.setAutoCommit(false); // Start transaction

            // 🧾 Delete related cheque payment if any
            PreparedStatement chequeStmt = conn.prepareStatement(
                    "DELETE FROM cheque_payment WHERE villa_id = ? AND owner_id = ?"
            );
            chequeStmt.setInt(1, villaId);
            chequeStmt.setInt(2, ownerId);
            chequeStmt.executeUpdate();

            // 💰 Delete payment
            PreparedStatement paymentStmt = conn.prepareStatement(
                    "DELETE FROM payments WHERE villa_id = ? AND owner_id = ?"
            );
            paymentStmt.setInt(1, villaId);
            paymentStmt.setInt(2, ownerId);
            int deletedPayments = paymentStmt.executeUpdate();

            // 🏠 Delete the villa record
            PreparedStatement villaStmt = conn.prepareStatement(
                    "DELETE FROM villas WHERE villa_id = ? AND owner_id = ?"
            );
            villaStmt.setInt(1, villaId);
            villaStmt.setInt(2, ownerId);
            villaStmt.executeUpdate();

            // 🔄 Decrement property count
            PreparedStatement updateOwner = conn.prepareStatement(
                    "UPDATE owners SET property_count = property_count - 1 WHERE owner_id = ? AND property_count > 0"
            );
            updateOwner.setInt(1, ownerId);
            updateOwner.executeUpdate();

            conn.commit(); // ✅ Commit all if successful

            if (deletedPayments > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Payment, villa, and property count updated.");
                ownerTableView.getItems().remove(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Not Found", "No payment found for this villa.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Deletion failed: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleDeletePayWorker(ActionEvent event) {
        WorkerPayment selected = workerTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a worker payment to delete.");
            return;
        }

        int workerId = selected.getWorkerId();
        String payDate = selected.getPayDate(); // Assuming it's stored as a `String` in format YYYY-MM-DD

        try (Connection conn = DBConnector.connect()) {
            PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM worker_payment WHERE worker_id = ? AND date = ?"
            );
            deleteStmt.setInt(1, workerId);
            deleteStmt.setDate(2, java.sql.Date.valueOf(payDate));

            int deleted = deleteStmt.executeUpdate();

            if (deleted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Worker payment deleted successfully.");
                workerTableView.getItems().remove(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Not Found", "No payment found for the selected worker and date.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete worker payment: " + e.getMessage());
        }
    }


}
