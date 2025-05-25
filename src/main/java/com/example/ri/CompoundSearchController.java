package com.example.ri;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CompoundSearchController {
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
    @FXML
    private Button showAllButton;

    // Compound panel buttons
    @FXML
    private Button searchCompoundButton;
    @FXML
    private Button addCompoundButton;
    @FXML
    private Button SearchCompoundButton;
    @FXML
    private ComboBox<String> searchByCombo;
    @FXML
    private TextField searchField;

    @FXML
    private TableView<CompoundModel> compoundTable;

    @FXML
    private TableColumn<CompoundModel, Integer> compoundIdColumn;

    @FXML
    private TableColumn<CompoundModel, Integer> managerIdColumn;

    @FXML
    private TableColumn<CompoundModel, String> locationColumn;

    @FXML
    private TableColumn<CompoundModel, Integer> numberOfVillasColumn;

    @FXML
    private TableColumn<CompoundModel, Double> villaPriceColumn;

    @FXML
    private TableColumn<CompoundModel, Double> totalCostColumn;

    @FXML
    private TableColumn<CompoundModel, Double> profitsColumn;
    @FXML
    private Button btnDelete;

    private final ObservableList<CompoundModel> compoundData = FXCollections.observableArrayList();




    private String role;
    private String userId;

    public void initialize() {
        compoundIdColumn.setCellValueFactory(new PropertyValueFactory<>("compoundId"));
        managerIdColumn.setCellValueFactory(new PropertyValueFactory<>("managerId"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        numberOfVillasColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfVillas"));
        villaPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        profitsColumn.setCellValueFactory(new PropertyValueFactory<>("profits"));

        compoundTable.setItems(compoundData);

        searchByCombo.getItems().addAll("Compound ID", "Manager ID", "Location");

        compoundTable.setRowFactory(tv -> {
            TableRow<CompoundModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    CompoundModel selectedCompound = row.getItem();
                    openEditCompoundPage(selectedCompound);
                }
            });
            return row;
        });

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
    private void openEditCompoundPage(CompoundModel compound) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/CompoundAdd.fxml"));
            Scene scene = new Scene(loader.load());

            CompoundAddController controller = loader.getController();
            controller.loadCompoundData(compound); // تعبئة البيانات

            // استرجع الستيج الحالي وبدّل الصفحة فيه
            Stage currentStage = (Stage) compoundTable.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("Edit Compound");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not load compound editing page.");
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
    private void handelShowAll(ActionEvent event) {
        compoundData.clear();
        try (Connection conn = DBConnector.connect()) {
            String sql = """
            SELECT c.compound_id, c.location, c.villa_price, c.profits, 
                   c.total_cost, c.number_of_villas,
                   (SELECT m.id FROM manager m WHERE m.compound_id = c.compound_id LIMIT 1) AS manager_id
            FROM compounds c
        """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                compoundData.add(new CompoundModel(
                        rs.getInt("compound_id"),
                        rs.getObject("manager_id") != null ? rs.getInt("manager_id") : null,
                        rs.getString("location"),
                        rs.getInt("number_of_villas"),
                        rs.getDouble("villa_price"),
                        rs.getDouble("total_cost"),
                        rs.getDouble("profits")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not fetch data.");
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
    private void handelSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();
        String criteria = searchByCombo.getValue();

        if (keyword.isEmpty() || criteria == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter search keyword and select a filter.");
            return;
        }

        compoundData.clear();

        String column;
        switch (criteria) {
            case "Compound ID" -> column = "compound_id";
            case "Manager ID" -> column = "manager_id";
            case "Location" -> column = "location";
            default -> {
                showAlert(Alert.AlertType.ERROR, "Search Error", "Invalid search type.");
                return;
            }
        }

        String query = """
        SELECT c.compound_id, c.location, c.villa_price, c.profits,
               c.total_cost, c.number_of_villas,
               (SELECT m.id FROM manager m WHERE m.compound_id = c.compound_id LIMIT 1) AS manager_id
        FROM compounds c
        WHERE %s::text ILIKE ?
        """.formatted(column);

        try (Connection conn = DBConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                compoundData.add(new CompoundModel(
                        rs.getInt("compound_id"),
                        rs.getObject("manager_id") != null ? rs.getInt("manager_id") : null,
                        rs.getString("location"),
                        rs.getInt("number_of_villas"),
                        rs.getDouble("villa_price"),
                        rs.getDouble("total_cost"),
                        rs.getDouble("profits")
                ));
            }
            searchField.setText("");
            if (compoundData.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Results", "No compound found matching the criteria.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handelDelete(ActionEvent event) {
        CompoundModel selectedCompound = compoundTable.getSelectionModel().getSelectedItem();

        if (selectedCompound == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a compound to delete.");
            return;
        }

        int compoundId = selectedCompound.getCompoundId();

        // Check for villas under this compound
        try (Connection conn = DBConnector.connect();
             PreparedStatement checkVillas = conn.prepareStatement(
                     "SELECT COUNT(*) FROM villas WHERE compound_id = ?")) {

            checkVillas.setInt(1, compoundId);
            ResultSet rs = checkVillas.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.ERROR, "Deletion Blocked",
                        "Cannot delete this compound.\nIt still contains villas.");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to check villas: " + e.getMessage());
            return;
        }

        // Confirm and delete
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Compound ID " + compoundId + "?");
        confirm.setContentText("This will permanently delete the compound.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = DBConnector.connect();
                     PreparedStatement stmt = conn.prepareStatement(
                             "DELETE FROM compounds WHERE compound_id = ?")) {

                    stmt.setInt(1, compoundId);
                    stmt.executeUpdate();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Compound deleted successfully.");
                    handelShowAll(null); // Refresh list

                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete compound: " + e.getMessage());
                }
            }
        });
    }

}
