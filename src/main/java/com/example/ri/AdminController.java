package com.example.ri;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.EventObject;

public class AdminController {
    @FXML
    private Label idLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private Button myInfoButton;

    @FXML
    private Button managerButton;

    @FXML
    private Button compoundButton;
    @FXML
    private AnchorPane managerPane;
    @FXML
    private AnchorPane CompoundPane;

    @FXML
    private Button searchManagerButton;
    @FXML
    private Button addManagerButton;

    // Compound panel buttons
    @FXML
    private Button searchCompoundButton;
    @FXML
    private Button addCompoundButton;

    private String role;
    private String userId;



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

        if (nameLabel != null) {
            nameLabel.setText("Admin : "+name);
            idLabel.setText("ID : "+userId);
        }

    }


    @FXML
    private void handleMyInfo(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/AdminInfo.fxml"));
            Scene scene = new Scene(loader.load());

            AdminInfoController AdminInfoController = loader.getController();
            AdminInfoController.setRoleAndUser(this.role, this.userId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Admin Info");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManager(ActionEvent event) {
        ((Button) event.getSource()).setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        compoundButton.setStyle("");
        managerPane.setVisible(true);
        CompoundPane.setVisible(false);
    }

    @FXML
    private void handleCompound(ActionEvent event) {
        ((Button) event.getSource()).setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        managerButton.setStyle("");
        CompoundPane.setVisible(true);
        managerPane.setVisible(false);
    }

     @FXML
    public void handleLogout(ActionEvent event) {
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
    private void handleSearchManager(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/ManagerSearch.fxml"));
            Scene scene = new Scene(loader.load());

            ManagerSearchController managerSearchController = loader.getController();
            managerSearchController.saverole(this.role, this.userId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Search Manager");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void handleAddManager(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/ManagerAdd.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("ADD Manager");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchCompound(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/CompoundSearch.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Search Compound");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void handleAddCompound(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/CompoundAdd.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("ADD Compound");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
