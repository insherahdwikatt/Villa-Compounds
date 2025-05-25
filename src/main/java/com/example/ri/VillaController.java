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

public class VillaController {



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
    private Button deleteButton;

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

    private String role;
    private String userId;

    @FXML
    private Button showAll;
    @FXML
    private Button search;
    @FXML
    private ComboBox<String> searchByCombo;
    @FXML
    private TextField searchField;


    @FXML private TableView<VillaTableModel> villaTableView;
    @FXML private TableColumn<VillaTableModel, Integer> villaIdCol;
    @FXML private TableColumn<VillaTableModel, String> ownerNameCol;
    @FXML private TableColumn<VillaTableModel, Integer> ownerIdCol;
    @FXML private TableColumn<VillaTableModel, String> statusCol;
    @FXML private TableColumn<VillaTableModel, String> paymentMethodCol;
    @FXML private TableColumn<VillaTableModel, String> paymentCompletedCol;


    @FXML
    private void initialize() {
        searchByCombo.setItems(FXCollections.observableArrayList("Villa ID", "Owner ID", "Owner Name", "Status"));

        // Column bindings
        villaIdCol.setCellValueFactory(new PropertyValueFactory<>("villaId"));
        ownerNameCol.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        ownerIdCol.setCellValueFactory(new PropertyValueFactory<>("ownerId"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        paymentMethodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentCompletedCol.setCellValueFactory(new PropertyValueFactory<>("paymentCompleted"));
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


    @FXML
    private void handaleShowAll(ActionEvent event) {
        try (Connection conn = DBConnector.connect()) {
            String sql =
                    """
                    SELECT\s
                        v.villa_id,
                        COALESCE(p.first_name || ' ' || p.last_name, 'No Owner') AS owner_name,
                        o.owner_id,
                        v.status,
                        pay.payment_method,
                        CASE\s
                            WHEN pay.amount IS NOT NULL THEN 'Yes'
                            ELSE 'No'
                        END AS payment_completed
                    FROM villas v
                    LEFT JOIN owners o ON v.owner_id = o.owner_id
                    LEFT JOIN person p ON o.owner_id = p.person_id
                    LEFT JOIN payments pay ON v.villa_id = pay.villa_id """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Replace this with your actual model and table binding
            ObservableList<VillaTableModel> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new VillaTableModel(
                        rs.getInt("villa_id"),
                        rs.getString("owner_name"),
                        rs.getInt("owner_id"),
                        rs.getString("status"),
                        rs.getString("payment_method"),
                        rs.getString("payment_completed")
                ));
            }

            villaTableView.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load villa data: " + e.getMessage());
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handaleSearch(ActionEvent event) {
        String criteria = searchByCombo.getValue();
        String value = searchField.getText().trim();

        if (criteria == null || value.isEmpty()) {
            showAlert("Validation Error", "Please select a search criteria and enter a value.");
            return;
        }

        String column = null;
        boolean isNumeric = false;

        switch (criteria) {
            case "Villa ID" -> {
                column = "v.villa_id";
                isNumeric = true;
            }
            case "Owner ID" -> {
                column = "o.owner_id";
                isNumeric = true;
            }
            case "Owner Name" -> column = "p.first_name || ' ' || p.last_name";
            case "Status" -> column = "v.status";
            default -> {
                showAlert("Invalid Field", "Unknown search field selected.");
                return;
            }
        }

        String sql = """
        SELECT 
            v.villa_id,
            p.first_name || ' ' || p.last_name AS owner_name,
            o.owner_id,
            v.status,
            pay.payment_method,
            CASE 
                WHEN pay.amount IS NOT NULL THEN 'Yes'
                ELSE 'No'
            END AS payment_completed
        FROM villas v
        JOIN owners o ON v.owner_id = o.owner_id
        JOIN person p ON o.owner_id = p.person_id
        LEFT JOIN payments pay ON v.villa_id = pay.villa_id
        """ + " WHERE " + column + (isNumeric ? " = ?" : " ILIKE ?");

        try (Connection conn = DBConnector.connect()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (isNumeric) {
                stmt.setInt(1, Integer.parseInt(value));
            } else {
                stmt.setString(1, "%" + value + "%");
            }

            ResultSet rs = stmt.executeQuery();
            ObservableList<VillaTableModel> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new VillaTableModel(
                        rs.getInt("villa_id"),
                        rs.getString("owner_name"),
                        rs.getInt("owner_id"),
                        rs.getString("status"),
                        rs.getString("payment_method"),
                        rs.getString("payment_completed")
                ));
            }

            villaTableView.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to search villas: " + e.getMessage());
        }
    }

    @FXML
    private void handaleDelete(ActionEvent event) {
        VillaTableModel selectedVilla = villaTableView.getSelectionModel().getSelectedItem();

        if (selectedVilla == null) {
            showAlert("No Selection", "Please select a villa to delete.");
            return;
        }

        int villaId = selectedVilla.getVillaId();
        int ownerId = selectedVilla.getOwnerId();

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete Villa ID " + villaId + "?");
        confirmAlert.setContentText("This will delete the villa and all related payment records.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = DBConnector.connect()) {
                    conn.setAutoCommit(false); // Begin transaction

                    // Step 1: Delete from cheque_payment
                    try (PreparedStatement deleteCheque = conn.prepareStatement(
                            "DELETE FROM cheque_payment WHERE villa_id = ?")) {
                        deleteCheque.setInt(1, villaId);
                        deleteCheque.executeUpdate();
                    }

                    // Step 2: Delete from payments
                    try (PreparedStatement deletePayment = conn.prepareStatement(
                            "DELETE FROM payments WHERE villa_id = ?")) {
                        deletePayment.setInt(1, villaId);
                        deletePayment.executeUpdate();
                    }

                    // Step 3: Delete the villa
                    try (PreparedStatement deleteVilla = conn.prepareStatement(
                            "DELETE FROM villas WHERE villa_id = ?")) {
                        deleteVilla.setInt(1, villaId);
                        deleteVilla.executeUpdate();
                    }

                    // Step 4: Decrement property count of the owner
                    try (PreparedStatement updateOwner = conn.prepareStatement(
                            "UPDATE owners SET property_count = property_count - 1 WHERE owner_id = ? AND property_count > 0")) {
                        updateOwner.setInt(1, ownerId);
                        updateOwner.executeUpdate();
                    }

                    conn.commit(); // All good

                    showAlert("Success", "Villa deleted and owner's property count updated.");
                    handaleShowAll(null); // Refresh table

                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to delete villa: " + e.getMessage());
                }
            }
        });
    }


}
