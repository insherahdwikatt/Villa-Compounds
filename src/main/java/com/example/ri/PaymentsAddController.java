package com.example.ri;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PaymentsAddController {


    @FXML
    private Button ADD_PAYMENT;
    @FXML
    private Button UPDATE_PAYMENT;

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
     private  Button deleteButton;
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
    @FXML
    private ComboBox<String> paymentMethodComboBox;
    @FXML
    private ComboBox<String>VillaIDComboBox;
    // Compound panel buttons
    @FXML
    private Button searchWorkerButton;
    @FXML
    private Button addWorkerButton;

    private String role;
    private String userId;

    @FXML
    private ComboBox<String> ownerIDComboBox;
    @FXML
    private ComboBox<String> compoundIDComboBox;
    @FXML
    private TextField numberOfChequesField;

    @FXML
    private TextField dueDateField;

    public void initialize() {
        paymentMethodComboBox.setItems(FXCollections.observableArrayList("Cash", "Cheques"));

        compoundIDComboBox.setOnAction(e -> loadAvailableVillas());
        VillaIDComboBox.setOnAction(e -> {
            String selectedVilla = VillaIDComboBox.getValue();

        });


        loadOwnerIds();
        loadCompoundIds();

    }


    private void loadCompoundIds() {
        try (Connection conn = DBConnector.connect()) {
            String sql = "SELECT compound_id FROM manager WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(UserInfo.getInstance().getUserId()));
            ResultSet rs = stmt.executeQuery();
            boolean first = true;
            while (rs.next()) {
                String id = String.valueOf(rs.getInt("compound_id"));
                compoundIDComboBox.getItems().add(id);
                if (first) {
                    compoundIDComboBox.setValue(id); // اختياره تلقائيًا
                    loadAvailableVillas();           // تحميل الفلل بعد اختيار الكومباوند
                    first = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load compounds: " + e.getMessage());
        }
    }


    private void loadAvailableVillas() {
        VillaIDComboBox.getItems().clear();
        String selectedCompoundId = compoundIDComboBox.getValue();

        if (selectedCompoundId != null && selectedCompoundId.matches("\\d+")) {
            try (Connection conn = DBConnector.connect()) {
                String sql = "SELECT villa_id FROM villas WHERE compound_id = ? AND status = 'available'";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(selectedCompoundId));
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    VillaIDComboBox.getItems().add(String.valueOf(rs.getInt("villa_id")));
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load available villas: " + e.getMessage());
            }
        }
    }


    private void loadOwnerIds() {
        try (Connection conn = DBConnector.connect()) {
            String sql = "SELECT owner_id FROM owners ORDER BY owner_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ownerIDComboBox.getItems().add(String.valueOf(rs.getInt("owner_id")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load owners: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
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
    @FXML
    private void handaleAddNewPayment(ActionEvent event) {
        String ownerId = ownerIDComboBox.getValue();
        String villaId = VillaIDComboBox.getValue();
        String method = paymentMethodComboBox.getValue();

        if (ownerId == null || villaId == null || method == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill all fields");
            return;
        }

        try (Connection conn = DBConnector.connect()) {
            PreparedStatement priceStmt = conn.prepareStatement(
                    "SELECT compound_id, villa_price FROM compounds WHERE compound_id = " +
                            "(SELECT compound_id FROM villas WHERE villa_id = ?)"
            );
            priceStmt.setInt(1, Integer.parseInt(villaId));
            ResultSet priceRs = priceStmt.executeQuery();
            if (!priceRs.next()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Compound not found for the selected villa.");
                return;
            }
            BigDecimal villaPrice = priceRs.getBigDecimal("villa_price");

            // Assign villa to owner and update status to 'Sold'
            PreparedStatement assignVilla = conn.prepareStatement(
                    "UPDATE villas SET owner_id = ?, status = 'Sold' WHERE villa_id = ?"
            );
            assignVilla.setInt(1, Integer.parseInt(ownerId));
            assignVilla.setInt(2, Integer.parseInt(villaId));
            assignVilla.executeUpdate();

            PreparedStatement insertPayment = conn.prepareStatement(
                    "INSERT INTO payments (villa_id, owner_id, amount, payments_date, payment_method) " +
                            "VALUES (?, ?, ?, CURRENT_DATE, ?)"
            );
            insertPayment.setInt(1, Integer.parseInt(villaId));
            insertPayment.setInt(2, Integer.parseInt(ownerId));
            insertPayment.setBigDecimal(3, villaPrice);
            insertPayment.setString(4, method);
            insertPayment.executeUpdate();

            PreparedStatement updatePropertyCount = conn.prepareStatement(
                    "UPDATE owners SET property_count = property_count + 1 WHERE owner_id = ?"
            );
            updatePropertyCount.setInt(1, Integer.parseInt(ownerId));
            updatePropertyCount.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Payment and villa registered successfully.");

            ownerIDComboBox.setValue("");
            VillaIDComboBox.setValue("");
            paymentMethodComboBox.setValue("");

            loadAvailableVillas(); // reload available villas after assignment

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong: " + e.getMessage());
        }
    }
}






