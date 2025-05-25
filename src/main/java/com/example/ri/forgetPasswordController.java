package com.example.ri;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class forgetPasswordController {

    @FXML private TextField idField;
    @FXML private TextField emailField;
    @FXML private TextField codeField;
    @FXML private TextField newPasswordField;

    @FXML
    private AnchorPane sendCodePane;
    @FXML
    private AnchorPane VarificationPane;
    @FXML
    private AnchorPane newPasswordPane;

    private static final Map<String, String> verificationCodes = new HashMap<>();
    private static String idFromLogin;

    // Allow passing the ID from another controller (e.g., login)
    public static void setId(String id) {
        idFromLogin = id;
    }

    @FXML
    public void initialize() {
        if (idFromLogin != null) {
            idField.setText(idFromLogin);
            fetchEmailForId(idFromLogin);
        }
    }

    private void fetchEmailForId(String id) {
        try (Connection conn = DBConnector.connect()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT email FROM person WHERE person_id = ?");
            stmt.setInt(1, Integer.parseInt(id));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                emailField.setText(rs.getString("email"));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleSendCode(ActionEvent event) {
        String id = idField.getText().trim();

        if (id.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter your ID.");
            return;
        }

        try (Connection conn = DBConnector.connect()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT email FROM person WHERE person_id = ?");
            stmt.setInt(1, Integer.parseInt(id));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");
                emailField.setText(email);

                String code = generateCode();
                verificationCodes.put(id, code);

                EmailSender.sendVerificationCode(email, code);

                showAlert(Alert.AlertType.INFORMATION, "Code Sent", "Verification code sent to: " + email);
                VarificationPane.setVisible(true);
                newPasswordPane.setVisible(false);
                sendCodePane.setVisible(false);

            } else {
                showAlert(Alert.AlertType.ERROR, "Not Found", "ID not found.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void handleVerifyCode(ActionEvent event) {
        String id = idField.getText().trim();
        String enteredCode = codeField.getText().trim();

        String correctCode = verificationCodes.get(id);

        if (enteredCode.equals(correctCode)) {
            showAlert(Alert.AlertType.INFORMATION, "Verified", "Code verified. You can now reset your password.");

            VarificationPane.setVisible(false);
            newPasswordPane.setVisible(true);
            sendCodePane.setVisible(false);

        } else {
            showAlert(Alert.AlertType.ERROR, "Incorrect Code", "Verification code is incorrect.");
        }
    }

    @FXML
    private void handleSavePassword() {
        String id = idField.getText().trim();
        String newPassword = newPasswordField.getText().trim();

        if (newPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Password cannot be empty.");
            return;
        }

        try (Connection conn = DBConnector.connect()) {
            // Step 1: Check which table the ID exists in
            String userType = null;

            PreparedStatement checkAdmin = conn.prepareStatement("SELECT 1 FROM admin WHERE id = ?");
            checkAdmin.setInt(1, Integer.parseInt(id));
            ResultSet rsAdmin = checkAdmin.executeQuery();
            if (rsAdmin.next()) userType = "admin";

            if (userType == null) {
                PreparedStatement checkManager = conn.prepareStatement("SELECT 1 FROM manager WHERE id = ?");
                checkManager.setInt(1, Integer.parseInt(id));
                ResultSet rsManager = checkManager.executeQuery();
                if (rsManager.next()) userType = "manager";
            }

            if (userType == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "ID not found in admin or manager.");
                return;
            }

            // Step 2: Perform update on the correct table
            String updateSQL = "UPDATE " + userType + " SET password = ? WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
            updateStmt.setString(1, newPassword);
            updateStmt.setInt(2, Integer.parseInt(id));

            int affectedRows = updateStmt.executeUpdate();

            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Password updated for " + userType + ".");
                verificationCodes.remove(id);
            } else {
                showAlert(Alert.AlertType.ERROR, "Failure", "Password update failed.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handaleGoBack(ActionEvent event) {
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
}
