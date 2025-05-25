package com.example.ri;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.math.BigDecimal;
import java.sql.*;
import java.net.URL;
import java.util.ResourceBundle;


import java.io.IOException;

public class ManagerAddController implements Initializable {

    @FXML
    private Button logoutButton;
    private int selectedManagerId = -1;
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
    @FXML
    private Button add;

    // Compound panel buttons
    @FXML
    private Button searchCompoundButton;
    @FXML
    private Button addCompoundButton;
    @FXML
    private Button Update;

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


    private int managerIdd;
    @FXML
    private ComboBox<String> compoundIdComboBox;

    @FXML
    private TextField phoneField;
    @FXML
    private TextField bankAccountField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField salaryField;

    @FXML
    private TextField passwordField;

    private String role;
    private String userId;

    private int managerId = -1; // ID لاستخدامه لاحقًا في التحديث


    @FXML
    private void handleMyInfo(ActionEvent event) {

        UserInfo userInfo = UserInfo.getInstance();
        role = userInfo.getRole();
        userId = userInfo.getUserId();

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
    private void handleADD(ActionEvent event) {
        String firstName = firstNameField.getText();
        String middleName = middleNameField.getText();
        String lastName = lastNameField.getText();
        String city = cityField.getText();
        String street = streetField.getText();
        String nationalId = nationalIdField.getText();
        String birthDate = birthDateField.getText();
        String compoundId = compoundIdComboBox.getValue();
        String phone = phoneField.getText();
        String bankAccount = bankAccountField.getText();
        String email = emailField.getText();
        String salary = salaryField.getText();
        String password = passwordField.getText();

        // Check for existing manager for the selected compound
        try (Connection checkConn = DBConnector.connect()) {
            String checkSQL = "SELECT COUNT(*) FROM manager WHERE compound_id = ?";
            PreparedStatement checkStmt = checkConn.prepareStatement(checkSQL);
            checkStmt.setInt(1, Integer.parseInt(compoundId));
            ResultSet checkRs = checkStmt.executeQuery();
            if (checkRs.next() && checkRs.getInt(1) > 0) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("A manager is already assigned to this compound!");
                alert.showAndWait();
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        try (Connection conn = DBConnector.connect()) {
            conn.setAutoCommit(false);

            String personSQL = "INSERT INTO person (first_name, middle_name, last_name, birthdate, phone_number, national_id, email, bank_account, city, street) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING person_id";
            try (PreparedStatement psPerson = conn.prepareStatement(personSQL)) {
                psPerson.setString(1, firstName);
                psPerson.setString(2, middleName);
                psPerson.setString(3, lastName);
                psPerson.setString(4, birthDate);
                psPerson.setString(5, phone);
                psPerson.setString(6, nationalId);
                psPerson.setString(7, email);
                psPerson.setString(8, bankAccount);
                psPerson.setString(9, city);
                psPerson.setString(10, street);

                ResultSet rs = psPerson.executeQuery();
                if (rs.next()) {
                    int personId = rs.getInt("person_id");

                    String managerSQL = "INSERT INTO manager (id, password, salary, compound_id) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement psManager = conn.prepareStatement(managerSQL)) {
                        psManager.setInt(1, personId);
                        psManager.setString(2, password);
                        psManager.setBigDecimal(3, new BigDecimal(salary));
                        psManager.setInt(4, Integer.parseInt(compoundId));
                        psManager.executeUpdate();
                    }

                    conn.commit();
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Manager added successfully!");
                    alert.showAndWait();

                    // Clear fields
                    firstNameField.setText("");
                    middleNameField.setText("");
                    lastNameField.setText("");
                    cityField.setText("");
                    streetField.setText("");
                    nationalIdField.setText("");
                    birthDateField.setText("");
                    compoundIdComboBox.setValue("");
                    phoneField.setText("");
                    bankAccountField.setText("");
                    emailField.setText("");
                    salaryField.setText("");
                    passwordField.setText("");

                    compoundIdComboBox.getItems().clear();
                    loadCompoundOptions();
                }
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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
    private void handleUpdateManager(ActionEvent event) {
        if (selectedManagerId == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "No manager selected for update.");
            return;
        }

        String firstName = firstNameField.getText();
        String middleName = middleNameField.getText();
        String lastName = lastNameField.getText();
        String city = cityField.getText();
        String street = streetField.getText();
        String nationalId = nationalIdField.getText();
        String birthDate = birthDateField.getText();
        String phone = phoneField.getText();
        String bankAccount = bankAccountField.getText();
        String email = emailField.getText();
        String salary = salaryField.getText();
        String password = passwordField.getText();

        String compoundValue = compoundIdComboBox.getValue();
        String compoundId;

        if (compoundValue == null || compoundValue.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a compound.");
            return;
        }

        // Safely extract compoundId
        compoundId = compoundValue.contains(" - ") ? compoundValue.split(" - ")[0] : compoundValue;

        try {
            BigDecimal salaryValue = new BigDecimal(salary);

            try (Connection conn = DBConnector.connect()) {
                conn.setAutoCommit(false);

                String personSQL = "UPDATE person SET first_name=?, middle_name=?, last_name=?, birthdate=?, phone_number=?, national_id=?, email=?, bank_account=?, city=?, street=? WHERE person_id=?";
                try (PreparedStatement psPerson = conn.prepareStatement(personSQL)) {
                    psPerson.setString(1, firstName);
                    psPerson.setString(2, middleName);
                    psPerson.setString(3, lastName);
                    psPerson.setString(4, birthDate);
                    psPerson.setString(5, phone);
                    psPerson.setString(6, nationalId);
                    psPerson.setString(7, email);
                    psPerson.setString(8, bankAccount);
                    psPerson.setString(9, city);
                    psPerson.setString(10, street);
                    psPerson.setInt(11, selectedManagerId);
                    psPerson.executeUpdate();
                }

                String managerSQL = "UPDATE manager SET password=?, salary=?, compound_id=? WHERE id=?";
                try (PreparedStatement psManager = conn.prepareStatement(managerSQL)) {
                    psManager.setString(1, password);
                    psManager.setBigDecimal(2, salaryValue);
                    psManager.setInt(3, Integer.parseInt(compoundId));
                    psManager.setInt(4, selectedManagerId);
                    psManager.executeUpdate();
                }

                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Manager updated successfully!");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Salary", "Salary must be a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the manager.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void setManagerData(int managerId, String firstName, String middleName, String lastName, String birthDate,
                               String phone, String nationalId, String email, String bankAccount, String city,
                               String street, String compoundId, String salary, String password) {
        this.selectedManagerId = managerId;
        firstNameField.setText(firstName);
        middleNameField.setText(middleName);
        lastNameField.setText(lastName);
        birthDateField.setText(birthDate);
        phoneField.setText(phone);
        nationalIdField.setText(nationalId);
        emailField.setText(email);
        bankAccountField.setText(bankAccount);
        cityField.setText(city);
        streetField.setText(street);
        salaryField.setText(salary);
        passwordField.setText(password);

        // عرض compoundId الحالي ضمن الخيارات إن لم يكن متاحاً
        if (compoundIdComboBox.getItems().stream().noneMatch(item -> item.startsWith(compoundId + " -"))) {
            compoundIdComboBox.getItems().add(compoundId + " - (current)");
        }
        compoundIdComboBox.setValue(compoundIdComboBox.getItems().stream()
                .filter(item -> item.startsWith(compoundId + " -"))
                .findFirst().orElse(""));
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCompoundOptions();
        Update.setVisible(false);
    }

    public void setSelectedManagerId(int id) {
        this.selectedManagerId = id;
    }

    private void loadCompoundOptions() {
        try (Connection conn = DBConnector.connect()) {
            if (conn == null) return;
            String sql = "SELECT compound_id, location FROM compounds WHERE compound_id NOT IN (SELECT compound_id FROM manager WHERE compound_id IS NOT NULL)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String id = rs.getString("compound_id");
                String location = rs.getString("location");
                compoundIdComboBox.getItems().add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadManager(ManagerTableModel manager) {
        // يجب تعبئة الحقول من model
        firstNameField.setText(manager.getFirstName());
        lastNameField.setText(manager.getLastName());
        phoneField.setText(manager.getPhone());
        emailField.setText(manager.getEmail());
        salaryField.setText(manager.getSalary());
        bankAccountField.setText(manager.getBankAccount());
        compoundIdComboBox.setValue(String.valueOf(manager.getCompoundId()));

        // تستطيع تخزين الـ ID داخل متغير داخلي لتحديثه لاحقًا
        this.managerId = manager.getId();
    }

    public void showData(int id) {
        add.setVisible(false);
        Update.setVisible(true);
        System.out.println("Trying to load manager with ID: " + id);
        managerIdd=id;
        selectedManagerId = id;

        try (Connection conn = DBConnector.connect()) {
            String sql = """
            SELECT p.first_name, p.middle_name, p.last_name, p.city, p.street,
                   p.national_id, p.birthdate, p.phone_number, p.bank_account,
                   p.email, m.salary, m.password, m.compound_id
            FROM person p
            JOIN manager m ON p.person_id = m.id
            WHERE p.person_id = ?
        """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Manager found in DB. Loading data...");
                firstNameField.setText(rs.getString("first_name"));
                middleNameField.setText(rs.getString("middle_name"));
                lastNameField.setText(rs.getString("last_name"));
                cityField.setText(rs.getString("city"));
                streetField.setText(rs.getString("street"));
                nationalIdField.setText(rs.getString("national_id"));
                birthDateField.setText(rs.getString("birthdate"));
                phoneField.setText(rs.getString("phone_number"));
                bankAccountField.setText(rs.getString("bank_account"));
                emailField.setText(rs.getString("email"));
                salaryField.setText(rs.getString("salary"));
                passwordField.setText(rs.getString("password"));
                compoundIdComboBox.setValue(String.valueOf(rs.getInt("compound_id")));
            } else {
                System.out.println("⚠️ Manager not found in the database for ID: " + id);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Not Found");
                alert.setContentText("Manager not found.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleAddManager(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/ManagerAdd.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Add Manager");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load add manager view.");
        }

    }
}
