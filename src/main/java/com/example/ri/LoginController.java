package com.example.ri;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import javafx.event.ActionEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
public class LoginController {




    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;


    @FXML
    private Button forgetPasswordButton;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Error", "Please enter both username and password.");
            usernameField.clear();
            passwordField.clear();

            return;
        }

        try (Connection conn = DBConnector.connect()) {
            if (conn == null) {
                showAlert("Database Error", "Cannot connect to the database.");
                return;
            }

            String sql = """
            SELECT 'admin' AS role FROM admin WHERE id = ? AND password = ?
            UNION
            SELECT 'manager' AS role FROM manager WHERE id = ? AND password = ?
        """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(username));
            stmt.setString(2, password);
            stmt.setInt(3, Integer.parseInt(username));
            stmt.setString(4, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                System.out.println("Logged in as: " + role);
                showWelcomeScene(role);
            } else {
                showAlert("Login Failed", "Incorrect Password or Username.");
                usernameField.clear();
                passwordField.clear();

            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Exception", e.getMessage());
        }

    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWelcomeScene(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/Welcome.fxml"));
            Scene scene = new Scene(loader.load());

            WelcomeController welcomeController = loader.getController();
            welcomeController.setRoleAndUser(role, usernameField.getText());
            UserInfo userInfo = UserInfo.getInstance();

             userInfo.setUserInfo(role, usernameField.getText());




            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Welcome " + role);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load welcome screen.");
        }
    }



    @FXML
    private void handleForgetPassword(ActionEvent event) {


        try {
            UserInfo.getInstance().reset();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/forgetPassword.fxml"));
            Scene scene = new Scene(loader.load());

            forgetPasswordController controller = loader.getController();
            forgetPasswordController.setId( usernameField.getText());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}