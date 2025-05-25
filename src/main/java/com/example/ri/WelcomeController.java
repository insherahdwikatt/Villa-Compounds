package com.example.ri;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WelcomeController {

    @FXML
    private Label userLabel;

    private String role;
    private String userId;

    @FXML
    private Button logoutButton;

    @FXML
    private Button continueButton;

    public void setRoleAndUser(String role, String userId) {
        this.role = role;
        this.userId = userId;

        String name = "";
        try (Connection conn = DBConnector.connect()) {
            if (conn != null) {
                String sql = "SELECT first_name, last_name FROM person WHERE person_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(userId));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    name = firstName + " " + lastName;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userLabel != null) {
            userLabel.setText("Hi "+name);
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
    private void handleContinue(ActionEvent event) {
        try {

            UserInfo userInfo = UserInfo.getInstance();
            role = userInfo.getRole();
            userId = userInfo.getUserId();
            if ("manager".equalsIgnoreCase(role)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/Manager.fxml"));
                Scene scene = new Scene(loader.load());


                ManagerController Controller = loader.getController();
                Controller.setLabels(role, userId);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle(role + " Page");
                stage.show();
            } else if ("admin".equalsIgnoreCase(role)) {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/Admin.fxml"));
                Scene scene = new Scene(loader.load());

                AdminController AdminController = loader.getController();
                AdminController.setRoleAndUser(role, userId);


                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle(role + " Page");
                stage.show(); } else {
                System.out.println("Unknown role: " + role);
                return;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class forgetPasswordController {
    }
}
