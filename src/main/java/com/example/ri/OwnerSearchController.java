package com.example.ri;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OwnerSearchController {

    @FXML
    private Button showAllButton;

    @FXML
    private Button searchButton;

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
    private Button searchOwnerButton;
    @FXML
    private Button addOwnerButton;

    @FXML
    private Button searchVillsButton;
    @FXML
    private Button reportButton;

    // Compound panel buttons
    @FXML
    private Button searchWorkerButton;
    @FXML
    private Button addWorkerButton;

    private String role;
    private String userId;


    @FXML
    private ComboBox<String> searchByCombo;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<ownerTableModel> ownerTable;
    @FXML private TableColumn<ownerTableModel, Integer> idColumn;
    @FXML private TableColumn<ownerTableModel, String> nameColumn;
    @FXML private TableColumn<ownerTableModel, String> nationalIdColumn;
    @FXML private TableColumn<ownerTableModel, String> phoneColumn;
    @FXML private TableColumn<ownerTableModel, String> emailColumn;
    @FXML private TableColumn<ownerTableModel, String> birthdateColumn;
    @FXML private TableColumn<ownerTableModel, Integer> propertyCountColumn;


    @FXML
    private Button btnDelete;

    @FXML
    public void initialize() {
        // إعداد قائمة خيارات البحث
        searchByCombo.setItems(FXCollections.observableArrayList("Owner ID", "National ID"));

        // إضافة RowFactory للجدول للسماح بالنقر المزدوج على الصفوف
        ownerTable.setRowFactory(tv -> {
            TableRow<ownerTableModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ownerTableModel rowData = row.getItem();
                    openOwnerDetails(rowData.getOwnerId());
                }
            });
            return row;
        });
    }

    private void openOwnerDetails(int ownerId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/OwnerAdd.fxml"));
            Scene scene = new Scene(loader.load());

            // فرضاً لديك Controller خاص بصفحة OwnerDetails اسمها OwnerDetailsController
            OwnerAddController controller = loader.getController();
            controller.loadOwnerData(ownerId);  // دالة لتحميل بيانات Owner حسب الـ ID

            Stage stage = (Stage) ownerTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Owner Details");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load Owner Details.");
        }
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
            String sql = """
            SELECT p.person_id, 
                   CONCAT(p.first_name, ' ', COALESCE(p.middle_name, ''), ' ', p.last_name) AS owner_name,
                   p.phone_number, p.email, p.birthdate, p.national_id, o.property_count
            FROM person p
            JOIN owners o ON p.person_id = o.owner_id
            ORDER BY p.person_id
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            javafx.collections.ObservableList<ownerTableModel> ownerList = javafx.collections.FXCollections.observableArrayList();

            while (rs.next()) {
                int id = rs.getInt("person_id");
                String name = rs.getString("owner_name").trim();
                String phone = rs.getString("phone_number");
                String email = rs.getString("email");
                String birthdate = rs.getString("birthdate");
                String nationalId = rs.getString("national_id");
                int propertyCount = rs.getInt("property_count");

                ownerList.add(new ownerTableModel(id, name, phone, email, birthdate, nationalId, propertyCount));
            }

            // Bind columns to model properties (only once, or add a guard if called multiple times)
            idColumn.setCellValueFactory(cellData -> cellData.getValue().ownerIdProperty().asObject());
            nameColumn.setCellValueFactory(cellData -> cellData.getValue().ownerNameProperty());
            phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
            emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
            birthdateColumn.setCellValueFactory(cellData -> cellData.getValue().birthdateProperty());
            nationalIdColumn.setCellValueFactory(cellData -> cellData.getValue().nationalIdProperty());
            propertyCountColumn.setCellValueFactory(cellData -> cellData.getValue().propertyCountProperty().asObject());

            ownerTable.setItems(ownerList);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load owners data.");
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
    private void handaleSearch(ActionEvent event) {
        String selectedField = searchByCombo.getValue();
        String searchValue = searchField.getText().trim();

        if (selectedField == null || searchValue.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a search field and enter a value.");
            return;
        }

        String column = switch (selectedField) {
            case "Owner ID" -> "CAST(owner.owner_id AS TEXT)";
            case "National ID" -> "p.national_id";
            default -> null;
        };

        if (column == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid search field.");
            return;
        }

        try (Connection conn = DBConnector.connect()) {
            String sql = String.format("""
            SELECT owner.owner_id,
                   p.first_name || ' ' || p.last_name AS full_name,
                   p.phone_number,
                   p.email,
                   p.birthdate,
                   p.national_id,
                   owner.property_count
            FROM owners owner
            JOIN person p ON owner.owner_id = p.person_id
            WHERE %s ILIKE ?
        """, column);

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + searchValue + "%");

            ResultSet rs = stmt.executeQuery();
            ObservableList<ownerTableModel> results = FXCollections.observableArrayList();

            while (rs.next()) {
                results.add(new ownerTableModel(
                        rs.getInt("owner_id"),
                        rs.getString("full_name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("birthdate"),
                        rs.getString("national_id"),
                        rs.getInt("property_count")
                ));
            }

            if (results.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Search", "No results found.");
            }

            ownerTable.setItems(results);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to retrieve data: " + e.getMessage());
        }
    }



    @FXML
    private void handaleDelete(ActionEvent event) {
        ownerTableModel selectedOwner = ownerTable.getSelectionModel().getSelectedItem();

        if (selectedOwner == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an owner to delete.");
            return;
        }

        int ownerId = selectedOwner.getOwnerId();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Confirmation");
        confirm.setHeaderText("Are you sure you want to delete owner ID: " + ownerId + "?");
        confirm.setContentText("This will also delete all villas, payments, and person record associated with the owner.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = DBConnector.connect()) {
                    conn.setAutoCommit(false); // 🔐 Begin transaction

                    // 1. Delete related cheques
                    try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM cheque_payment WHERE owner_id = ?")) {
                        stmt.setInt(1, ownerId);
                        stmt.executeUpdate();
                    }

                    // 2. Delete related payments
                    try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM payments WHERE owner_id = ?")) {
                        stmt.setInt(1, ownerId);
                        stmt.executeUpdate();
                    }

                    // 3. Delete villas owned
                    try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM villas WHERE owner_id = ?")) {
                        stmt.setInt(1, ownerId);
                        stmt.executeUpdate();
                    }

                    // 4. Delete from owners
                    try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM owners WHERE owner_id = ?")) {
                        stmt.setInt(1, ownerId);
                        stmt.executeUpdate();
                    }

                    // 5. Delete person
                    try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM person WHERE person_id = ?")) {
                        stmt.setInt(1, ownerId);
                        stmt.executeUpdate();
                    }

                    conn.commit();
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Owner and related data deleted successfully.");
                    handaleShowAll(null); // Refresh table

                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Deletion failed: " + e.getMessage());
                }
            }
        });
    }
    @FXML
    private void handleReport(ActionEvent event) {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            String url = "jdbc:postgresql://localhost:1234/RealEstate";
            String user = "postgres";
            String password = "raya4039";
            Connection cn = DriverManager.getConnection(url, user, password);
            String jrxmlPath = "C:/Users/DELL/JaspersoftWorkspace/OwnersReport/OwnersReport.jrxml";
            InputStream inp = new FileInputStream(new File(jrxmlPath));
            JasperDesign jsp = JRXmlLoader.load(inp);
            JasperReport report = JasperCompileManager.compileReport(jsp);
            JasperPrint filledReport = JasperFillManager.fillReport(report, null, cn);
            JFrame frame = new JFrame("Owners Report");
            frame.getContentPane().add(new JRViewer(filledReport));
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }

