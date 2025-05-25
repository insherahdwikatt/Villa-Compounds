package com.example.ri;

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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompoundAddController {

    @FXML
    private Button logoutButton;

    @FXML
    private Button myInfoButton;

    @FXML
    private Button managerButton;

    @FXML
    private Button compoundButton;

    @FXML
    private Button Add;


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
    private  Button Update;

    @FXML
    private TextField profitsField;
    @FXML
    private TextField totalCostField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField streetField;

    @FXML
    private TextField numberOfVillasField;

    @FXML
    private TextField villaPriceField;

    private int selectedCompoundId = -1;

    private String role;
    private String userId;

    @FXML
    private void handleManager(ActionEvent event) {
        ((Button) event.getSource()).setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        compoundButton.setStyle("");
        managerPane.setVisible(true);
        CompoundPane.setVisible(false);
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
    private void handleUpdate(ActionEvent event) {
        // تحتاج إلى id للكمباوند المراد تعديله
        if (selectedCompoundId == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "No compound selected.");
            return;
        }

        String city = cityField.getText();
        String street = streetField.getText();
        String profits = profitsField.getText();
        String numVillas = numberOfVillasField.getText();
        String villaPrice = villaPriceField.getText();

        if (city.isEmpty() || street.isEmpty() || profits.isEmpty() || numVillas.isEmpty() || villaPrice.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all fields.");
            return;
        }

        try {
            int numberOfVillas = Integer.parseInt(numVillas);
            BigDecimal pricePerVilla = new BigDecimal(villaPrice);
            BigDecimal totalCost = pricePerVilla.multiply(BigDecimal.valueOf(numberOfVillas));
            totalCostField.setText(totalCost.toPlainString());

            try (Connection conn = DBConnector.connect()) {
                String sql = "UPDATE compounds SET location=?, profits=?, number_of_villas=?, villa_price=?, total_cost=? WHERE compound_id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, city + " - " + street);
                stmt.setBigDecimal(2, new BigDecimal(profits));
                stmt.setInt(3, numberOfVillas);
                stmt.setBigDecimal(4, pricePerVilla);
                stmt.setBigDecimal(5, totalCost);
                stmt.setInt(6, selectedCompoundId);
                stmt.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Compound updated successfully.");
            }

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void handleADD(ActionEvent event) {
        String city = cityField.getText();
        String street = streetField.getText();
        String profits = profitsField.getText();
        String numVillas = numberOfVillasField.getText();
        String villaPrice = villaPriceField.getText();

        if (city.isEmpty() || street.isEmpty() || profits.isEmpty() || numVillas.isEmpty() || villaPrice.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all fields.");
            return;
        }

        try {
            int numberOfVillas = Integer.parseInt(numVillas);
            BigDecimal pricePerVilla = new BigDecimal(villaPrice);
            BigDecimal totalCost = pricePerVilla.multiply(BigDecimal.valueOf(numberOfVillas));
            totalCostField.setText(totalCost.toPlainString());

            try (Connection conn = DBConnector.connect()) {
                conn.setAutoCommit(false); // نستخدم ترانزاكشن

                // 1. أدخل الكمباوند
                String insertCompoundSQL = "INSERT INTO compounds (location, profits, number_of_villas, villa_price, total_cost) VALUES (?, ?, ?, ?, ?) RETURNING compound_id";
                int compoundId;
                try (PreparedStatement stmt = conn.prepareStatement(insertCompoundSQL)) {
                    stmt.setString(1, city + " - " + street);
                    stmt.setBigDecimal(2, new BigDecimal(profits));
                    stmt.setInt(3, numberOfVillas);
                    stmt.setBigDecimal(4, pricePerVilla);
                    stmt.setBigDecimal(5, totalCost);

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        compoundId = rs.getInt("compound_id");
                    } else {
                        conn.rollback();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to insert compound.");
                        return;
                    }
                }

                // 2. أضف الفلل تلقائيًا
                String insertVillaSQL = "INSERT INTO villas (compound_id, status, owner_id) VALUES (?, 'available', NULL)";
                try (PreparedStatement villaStmt = conn.prepareStatement(insertVillaSQL)) {
                    for (int i = 0; i < numberOfVillas; i++) {
                        villaStmt.setInt(1, compoundId);
                        villaStmt.addBatch();
                    }
                    villaStmt.executeBatch();
                }

                conn.commit();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Compound and villas added successfully.");

                // Clear fields
                cityField.setText("");
                streetField.setText("");
                profitsField.setText("");
                numberOfVillasField.setText("");
                villaPriceField.setText("");
                totalCostField.setText("");

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format Error", "Numeric fields contain invalid data.");
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



    public void loadCompoundData(CompoundModel compound) {
        String[] parts = compound.getLocation().split(" - ", 2);
        if (parts.length == 2) {
            cityField.setText(parts[0]);
            streetField.setText(parts[1]);
        } else {
            cityField.setText(compound.getLocation());
            streetField.setText("");
        }

        profitsField.setText(String.valueOf(compound.getProfits()));
        numberOfVillasField.setText(String.valueOf(compound.getNumberOfVillas()));
        villaPriceField.setText(String.valueOf(compound.getPrice()));
        totalCostField.setText(String.valueOf(compound.getTotalCost()));

        this.selectedCompoundId = compound.getCompoundId();

        Add.setDisable(true);
        Update.setDisable(false);
    }

}
