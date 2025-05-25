package com.example.ri;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PayWorkerController {


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
    private AnchorPane workerPane;

    @FXML
    private Button searchOwnerButton;
    @FXML
    private Button addOwnerButton;

    @FXML
    private Button searchVillsButton;

    // Compound panel buttons
    @FXML
    private Button searchWorkerButton;
    @FXML
    private Button addWorkerButton;
    @FXML
    private TextField SalaryField;
    @FXML
    private TextField dateField;
    @FXML
    private ComboBox<String> workerIDComboBox;
    @FXML
    private ComboBox<String> bankAcountComboBox;


    @FXML
    private Button AddPaymentButton;


    private String role;
    private String userId;


    @FXML
    private void initialize() {
        SalaryField.setEditable(false);
        bankAcountComboBox.setEditable(false);

        try (Connection conn = DBConnector.connect()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT person_id FROM person WHERE person_id IN (SELECT worker_id FROM workers)"
            );
            ResultSet rs = stmt.executeQuery();
            ObservableList<String> workers = FXCollections.observableArrayList();
            while (rs.next()) {
                int id = rs.getInt("person_id");
                workers.add(String.valueOf(id));
            }
            workerIDComboBox.setItems(workers);
            workerIDComboBox.setOnAction(event -> handleWorkerSelection());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load worker IDs: " + e.getMessage());
        }
    }

    private void handleWorkerSelection() {
        String selectedId = workerIDComboBox.getValue();
        if (selectedId != null && !selectedId.trim().isEmpty()) {
            loadWorkerDetails(selectedId);
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a valid Worker ID.");
        }
    }

    private void loadWorkerDetails(String workerId) {
        if (workerId == null || workerId.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Worker ID is missing or invalid.");
            return;
        }

        try (Connection conn = DBConnector.connect()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT p.bank_account, w.salary FROM person p JOIN workers w ON p.person_id = w.worker_id WHERE p.person_id = ?"
            );
            stmt.setInt(1, Integer.parseInt(workerId));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String account = rs.getString("bank_account");
                BigDecimal salary = rs.getBigDecimal("salary");
                bankAcountComboBox.getItems().clear();
                bankAcountComboBox.getItems().add(account);
                bankAcountComboBox.setValue(account);
                SalaryField.setText(String.valueOf(salary));
            } else {
                bankAcountComboBox.getItems().clear();
                bankAcountComboBox.setValue("");
                SalaryField.setText("");
                showAlert(Alert.AlertType.INFORMATION, "No Data", "No details found for this worker.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to fetch worker details: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void handleAddPayWorker(ActionEvent event) {
        String workerId = workerIDComboBox.getValue();
        String amountStr = SalaryField.getText();
        String dateStr = dateField.getText();

        System.out.println("Debug: " + workerId + ", " + amountStr + ", " + dateStr);

        if (workerId == null || amountStr.isEmpty() || dateStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Info", "Please fill all fields.");
            return;
        }

        try (Connection conn = DBConnector.connect()) {
            BigDecimal amount = new BigDecimal(amountStr);  // تأكد أنه رقم بصيغة صحيحة
            Date sqlDate = Date.valueOf(dateStr);           // لازم يكون yyyy-MM-dd

            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM worker_payment WHERE worker_id = ? AND date = ?"
            );
            checkStmt.setInt(1, Integer.parseInt(workerId));
            checkStmt.setDate(2, sqlDate);
            ResultSet checkRs = checkStmt.executeQuery();
            checkRs.next();
            if (checkRs.getInt(1) > 0) {
                showAlert(Alert.AlertType.WARNING, "Duplicate", "This worker has already been paid for this date.");
                return;
            }

            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO worker_payment (worker_id, date, salary) VALUES (?, ?, ?)"
            );
            stmt.setInt(1, Integer.parseInt(workerId));
            stmt.setDate(2, sqlDate);
            stmt.setBigDecimal(3, amount);
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Payment added successfully.");
            // Reset fields
            workerIDComboBox.setOnAction(null);
            workerIDComboBox.setValue("");
            workerIDComboBox.setOnAction(event2 -> handleWorkerSelection());
            SalaryField.setText("");
            dateField.setText("");
            bankAcountComboBox.getItems().clear();
            bankAcountComboBox.setValue("");

        } catch (Exception e) {
            e.printStackTrace();  // أضف هذا
            showAlert(Alert.AlertType.ERROR, "Insert Error", "Failed to add payment.");
        }
    }

    @FXML
    private void handaleAddWorker(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/WorkerAdd.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Add Worker");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Add Worker screen.");
        }
    }



@FXML

    private void handleMyInfo(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/Myinfo.fxml"));
            Scene scene = new Scene(loader.load());

            MyinfoController Controller = loader.getController();
            UserInfo userInfo = UserInfo.getInstance();
            Controller.setRoleAndUser(userInfo.getRole(), userInfo.getUserId());

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

        ownerPane.setVisible(true);
        workerPane.setVisible(false);
        PaymentPane.setVisible(false);
        villaPane.setVisible(false);

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
        villaPane.setVisible(true);
    }

    @FXML
    private void handelWorker(ActionEvent event) {
        ((Button) event.getSource()).setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        paymentButton.setStyle("");
        ownerButton.setStyle("");
        villaButton.setStyle("");

        ownerPane.setVisible(false);
        workerPane.setVisible(true);
        PaymentPane.setVisible(false);
        villaPane.setVisible(false);
    }

    @FXML
    private void handelPayment(ActionEvent event) {

        ((Button) event.getSource()).setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        workerButton.setStyle("");
        ownerButton.setStyle("");
        villaButton.setStyle("");
        ownerPane.setVisible(false);
        workerPane.setVisible(false);
        PaymentPane.setVisible(true);
        villaPane.setVisible(false);
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


}
