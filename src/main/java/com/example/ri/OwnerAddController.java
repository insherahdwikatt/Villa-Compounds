package com.example.ri;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OwnerAddController {


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

    private int currentOwnerId = -1;




    @FXML
    private Button ADD;
    private String role;
    private String userId;
    @FXML
    private void handeleADD(ActionEvent event) {
        try (Connection conn = DBConnector.connect()) {
            conn.setAutoCommit(false);

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

            if (firstName.isEmpty() || lastName.isEmpty() || nationalId.isEmpty() || email.isEmpty()) {
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
                this.currentOwnerId = personId; // حفظ الرقم لاستخدام لاحق

                String ownerSQL = "INSERT INTO owners(owner_id, property_count) VALUES (?, 0)";
                PreparedStatement stmt2 = conn.prepareStatement(ownerSQL);
                stmt2.setInt(1, personId);
                stmt2.executeUpdate();

                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Owner added successfully with ID: " + personId);
                firstNameField.setText("");
                 middleNameField.setText("");
                lastNameField.setText("");
                cityField.setText("");
                streetField.setText("");
               nationalIdField.setText("");
                 birthDateField.setText("");
                 phoneNumberField.setText("");
               emailField.setText("");
               bankAccountField.setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not add owner.");
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




    public void loadOwnerData(int ownerId) {

        currentOwnerId=ownerId;
        String sql = """
        SELECT p.person_id, p.first_name, p.middle_name, p.last_name, p.city, p.street,
               p.national_id, p.birthdate, p.phone_number, p.email, p.bank_account,
               o.property_count
        FROM person p
        JOIN owners o ON p.person_id = o.owner_id
        WHERE p.person_id = ?
    """;

        try (Connection conn = DBConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ownerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentOwnerId = rs.getInt("person_id");

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

                // يمكنك إضافة عرض propertyCount إذا أردت في واجهة المستخدم
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Not Found", "Owner not found with ID: " + ownerId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load owner data: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateOwner(ActionEvent event) {
        if (currentOwnerId == -1) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "No owner selected for update.");
            return;
        }

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

        // تحقق من الحقول الأساسية
        if (firstName.isEmpty() || lastName.isEmpty() || nationalId.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields.");
            return;
        }

        String sql = """
        UPDATE person
        SET first_name = ?, middle_name = ?, last_name = ?, city = ?, street = ?,
            national_id = ?, birthdate = ?, phone_number = ?, email = ?, bank_account = ?
        WHERE person_id = ?
    """;

        try (Connection conn = DBConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            stmt.setString(1, firstName);
            stmt.setString(2, middleName);
            stmt.setString(3, lastName);
            stmt.setString(4, city);
            stmt.setString(5, street);
            stmt.setString(6, nationalId);
            stmt.setString(7, birthDate);
            stmt.setString(8, phone);
            stmt.setString(9, email);
            stmt.setString(10, bank);
            stmt.setInt(11, currentOwnerId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 1) {
                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Owner updated successfully.");
            } else {
                conn.rollback();
                showAlert(Alert.AlertType.WARNING, "Update Failed", "No owner found with the given ID.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update owner: " + e.getMessage());
        }
    }

}
