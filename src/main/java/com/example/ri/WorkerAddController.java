package com.example.ri;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WorkerAddController {

    @FXML
    private Button Update;
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
    private TextField firstNameField;
    @FXML
    private TextField middleNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField streetField;
    @FXML
    private TextField nationalIdField;
    @FXML
    private TextField birthDateField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField bankAccountField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField EndOfleaseField;
    @FXML
    private TextField salaryField;
    @FXML
    private TextField workHoursField;
    @FXML
    private ComboBox<String> fieldBox;




    private int currentWorkerId = -1;

    @FXML
    private Button ADD;
    private String role;
    private String userId;

    @FXML
    public void initialize() {
        fieldBox.setItems(FXCollections.observableArrayList(
                "Electrician",
                "Plumber",
                "Painter",
                "Carpenter",
                "Cleaner",
                "Security"
        ));
    }


    @FXML
    private void handeleADD(ActionEvent event) {
        try (Connection conn = DBConnector.connect()) {
            conn.setAutoCommit(false);

            // جلب بيانات الشخص من الحقول
            String firstName = firstNameField.getText();
            String middleName = middleNameField.getText();
            String lastName = lastNameField.getText();
            String city = cityField.getText();
            String street = streetField.getText();
            String nationalId = nationalIdField.getText();
            String birthDate = birthDateField.getText();
            String phone = phoneNumberField.getText();
            String email = emailField.getText();
            String bank = bankAccountField.getText();

            // بيانات العامل الخاصة
            String field = fieldBox.getValue();
            String endOfLease = EndOfleaseField.getText();
            String salaryStr = salaryField.getText();
            String workHoursStr = workHoursField.getText();

            // تحقق من الحقول المطلوبة
            if (firstName.isEmpty() || lastName.isEmpty() || nationalId.isEmpty() || email.isEmpty() || field == null || salaryStr.isEmpty() || workHoursStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields.");
                return;
            }

            String personSQL = """
            INSERT INTO person(first_name, middle_name, last_name, city, street, national_id, birthdate, phone_number, email, bank_account)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING person_id
        """;

            PreparedStatement stmt1 = conn.prepareStatement(personSQL);
            stmt1.setString(1, firstName);
            stmt1.setString(2, middleName);
            stmt1.setString(3, lastName);
            stmt1.setString(4, city);
            stmt1.setString(5, street);
            stmt1.setString(6, nationalId);
            stmt1.setString(7, birthDate);
            stmt1.setString(8, phone);
            stmt1.setString(9, email);
            stmt1.setString(10, bank);

            ResultSet rs = stmt1.executeQuery();

            if (rs.next()) {
                int personId = rs.getInt("person_id");

                String workerSQL = """
                INSERT INTO workers(worker_id, field, end_of_lease, salary, work_hours)
                VALUES (?, ?, ?, ?, ?)
            """;

                PreparedStatement stmt2 = conn.prepareStatement(workerSQL);
                stmt2.setInt(1, personId);
                stmt2.setString(2, field);
                stmt2.setString(3, endOfLease);
                stmt2.setBigDecimal(4, new java.math.BigDecimal(salaryStr));
                stmt2.setInt(5, Integer.parseInt(workHoursStr));
                stmt2.executeUpdate();

                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Worker added successfully with ID: " + personId);

                // مسح الحقول بعد الإدخال
                firstNameField.clear();
                middleNameField.clear();
                lastNameField.clear();
                cityField.clear();
                streetField.clear();
                nationalIdField.clear();
                birthDateField.clear();
                phoneNumberField.clear();
                emailField.clear();
                bankAccountField.clear();
                fieldBox.getSelectionModel().clearSelection();
                EndOfleaseField.clear();
                salaryField.clear();
                workHoursField.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add worker: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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

    @FXML
    private void handaleAddWorker(ActionEvent event) {
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
    private void handleUpdate(ActionEvent event) {
        if (currentWorkerId == -1) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "No worker selected for update.");
            return;
        }

        try (Connection conn = DBConnector.connect()) {
            conn.setAutoCommit(false);

            String firstName = firstNameField.getText().trim();
            String middleName = middleNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String city = cityField.getText().trim();
            String street = streetField.getText().trim();
            String nationalId = nationalIdField.getText().trim();
            String birthDate = birthDateField.getText().trim();
            String phone = phoneNumberField.getText().trim();
            String email = emailField.getText().trim();
            String bank = bankAccountField.getText().trim();

            String endOfLease = EndOfleaseField.getText().trim();
            String salaryStr = salaryField.getText().trim();
            String workHoursStr = workHoursField.getText().trim();
            String field = fieldBox.getValue();

            if (firstName.isEmpty() || lastName.isEmpty() || nationalId.isEmpty() || email.isEmpty() || salaryStr.isEmpty() || field == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields.");
                return;
            }

            // تحديث جدول person
            String updatePersonSql = """
            UPDATE person
            SET first_name=?, middle_name=?, last_name=?, city=?, street=?, national_id=?, birthdate=?, phone_number=?, email=?, bank_account=?
            WHERE person_id=?
        """;
            try (PreparedStatement ps1 = conn.prepareStatement(updatePersonSql)) {
                ps1.setString(1, firstName);
                ps1.setString(2, middleName);
                ps1.setString(3, lastName);
                ps1.setString(4, city);
                ps1.setString(5, street);
                ps1.setString(6, nationalId);
                ps1.setString(7, birthDate);
                ps1.setString(8, phone);
                ps1.setString(9, email);
                ps1.setString(10, bank);
                ps1.setInt(11, currentWorkerId);
                ps1.executeUpdate();
            }

            // تحديث جدول worker
            String updateWorkerSql = """
            UPDATE workers
            SET salary=?, field=?, end_of_lease=?, work_hours=?
            WHERE worker_id=?
        """;
            try (PreparedStatement ps2 = conn.prepareStatement(updateWorkerSql)) {
                ps2.setBigDecimal(1, new java.math.BigDecimal(salaryStr));
                ps2.setString(2, field);
                ps2.setString(3, endOfLease);
                ps2.setInt(4, Integer.parseInt(workHoursStr));
                ps2.setInt(5, currentWorkerId);
                ps2.executeUpdate();
            }

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Worker updated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update worker: " + e.getMessage());
        }
    }
    public void loadOwnerData(int workerId) {
        this.currentWorkerId = workerId;

        try (Connection conn = DBConnector.connect()) {
            String sql = """
            SELECT p.*, w.field, w.salary, w.work_hours, w.end_of_lease
            FROM person p
            JOIN workers w ON p.person_id = w.worker_id
            WHERE p.person_id = ?
        """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, workerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                firstNameField.setText(rs.getString("first_name"));
                middleNameField.setText(rs.getString("middle_name"));
                lastNameField.setText(rs.getString("last_name"));
                cityField.setText(rs.getString("city"));
                streetField.setText(rs.getString("street"));
                nationalIdField.setText(rs.getString("national_id"));
                birthDateField.setText(rs.getString("birthdate"));
                phoneNumberField.setText(rs.getString("phone_number"));
                emailField.setText(rs.getString("email"));
                bankAccountField.setText(rs.getString("bank_account"));

                fieldBox.setValue(rs.getString("field"));
                salaryField.setText(rs.getString("salary"));
                workHoursField.setText(rs.getString("work_hours"));
                EndOfleaseField.setText(rs.getString("end_of_lease"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
