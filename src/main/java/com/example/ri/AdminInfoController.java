package com.example.ri;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminInfoController {

    @FXML
    private Button logoutButton;

    @FXML
    private Button myInfoButton;

    @FXML
    private Button managerButton;

    @FXML
    private Button compoundButton;

    @FXML
    private Button Update;


    @FXML
    private AnchorPane managerPane;
    @FXML
    private AnchorPane CompoundPane;

    @FXML
    private Button searchManagerButton;
    @FXML
    private Button addManagerButton;

    @FXML
    private Button searchCompoundButton;
    @FXML
    private Button addCompoundButton;

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
    private TextField passwordField;
    @FXML
    private TextField adminIdField;

    private String role1;
    private String userId1;

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
    private void handleMyInfo(ActionEvent event) {
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
    private void handleSearchManager(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/ManagerSearch.fxml"));
            Scene scene = new Scene(loader.load());
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

    @FXML
    private void handleUpdate(ActionEvent event) {


        String id = adminIdField.getText();
        String first = firstNameField.getText();
        String middle = middleNameField.getText();
        String last = lastNameField.getText();
        String city = cityField.getText();
        String street = streetField.getText();
        String national = nationalIdField.getText();
        String birthdate = birthDateField.getText();

        String phone = phoneNumberField.getText();
        String bank = bankAccountField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (id.isEmpty()) {
            showAlert("Error", "Admin ID is required.");
            return;
        }

        try (Connection conn = DBConnector.connect()) {
            if (conn == null) {
                showAlert("DB Error", "Cannot connect to database.");
                return;
            }
            String personUpdate = """
UPDATE person SET
    first_name = ?, middle_name = ?, last_name = ?,
    city = ?, street = ?, national_id = ?, birthdate = ?,
    phone_number = ?, bank_account = ?, email = ?
WHERE person_id = ?
""";

            PreparedStatement pStmt = conn.prepareStatement(personUpdate);
            pStmt.setString(1, first);
            pStmt.setString(2, middle);
            pStmt.setString(3, last);
            pStmt.setString(4, city);
            pStmt.setString(5, street);
            pStmt.setString(6, national);
            pStmt.setString(7, birthdate);
            pStmt.setString(8, phone);
            pStmt.setString(9, bank);
            pStmt.setString(10, email);
            pStmt.setInt(11, Integer.parseInt(id));

            pStmt.executeUpdate();



            String adminUpdate = "UPDATE admin SET password = ? WHERE id = ?";
            PreparedStatement aStmt = conn.prepareStatement(adminUpdate);
            aStmt.setString(1, password);
            aStmt.setInt(2, Integer.parseInt(id));
            aStmt.executeUpdate();

            showAlert("Success", "Admin updated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", e.getMessage());
        }
    }


    private void showAlert(String title, String msg) {
     Alert alert = new Alert(Alert.AlertType.INFORMATION);
     alert.setTitle(title);
     alert.setHeaderText(null);
     alert.setContentText(msg);
     alert.showAndWait();
 }
   public void setData()
   {
       try (Connection conn = DBConnector.connect()) {
           if (conn == null) {
               System.out.println("DB not connected");
               return;
           }

           String personSql = "SELECT * FROM person WHERE person_id = ?";
           PreparedStatement stmt1 = conn.prepareStatement(personSql);
           stmt1.setInt(1, Integer.parseInt(userId1));
           ResultSet rs1 = stmt1.executeQuery();

           if (rs1.next()) {
               firstNameField.setText(rs1.getString("first_name"));
               middleNameField.setText(rs1.getString("middle_name"));
               lastNameField.setText(rs1.getString("last_name"));
               cityField.setText(rs1.getString("city"));
               streetField.setText(rs1.getString("street"));
               nationalIdField.setText(rs1.getString("national_id"));

               birthDateField.setText(rs1.getString("birthdate"));

               phoneNumberField.setText(rs1.getString("phone_number"));
               bankAccountField.setText(rs1.getString("bank_account"));
               emailField.setText(rs1.getString("email"));
               adminIdField.setText(userId1);
           }

           String adminSql = "SELECT password FROM admin WHERE id = ?";
           PreparedStatement stmt2 = conn.prepareStatement(adminSql);
           stmt2.setInt(1, Integer.parseInt(userId1));
           ResultSet rs2 = stmt2.executeQuery();

           if (rs2.next()) {
               passwordField.setText(rs2.getString("password"));
           }

       } catch (Exception e) {
           e.printStackTrace();
       }
   }


    public void setRoleAndUser(String role, String userId) {
        role1 = role;
        userId1 = userId;

        try (Connection conn = DBConnector.connect()) {
            if (conn == null) {
                System.out.println("DB not connected");
                return;
            }

            String personSql = "SELECT * FROM person WHERE person_id = ?";
            PreparedStatement stmt1 = conn.prepareStatement(personSql);
            stmt1.setInt(1, Integer.parseInt(userId));
            ResultSet rs1 = stmt1.executeQuery();

            if (rs1.next()) {
                firstNameField.setText(rs1.getString("first_name"));
                middleNameField.setText(rs1.getString("middle_name"));
                lastNameField.setText(rs1.getString("last_name"));
                cityField.setText(rs1.getString("city"));
                streetField.setText(rs1.getString("street"));
                nationalIdField.setText(rs1.getString("national_id"));
                birthDateField.setText(rs1.getString("birthdate"));
                phoneNumberField.setText(rs1.getString("phone_number"));
                bankAccountField.setText(rs1.getString("bank_account"));
                emailField.setText(rs1.getString("email"));
                adminIdField.setText(userId);
                adminIdField.setEditable(false);
            }

            String adminSql = "SELECT password FROM admin WHERE id = ?";
            PreparedStatement stmt2 = conn.prepareStatement(adminSql);
            stmt2.setInt(1, Integer.parseInt(userId));
            ResultSet rs2 = stmt2.executeQuery();

            if (rs2.next()) {
                passwordField.setText(rs2.getString("password"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


