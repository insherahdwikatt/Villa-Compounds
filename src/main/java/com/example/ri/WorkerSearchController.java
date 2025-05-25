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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WorkerSearchController {


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
    private Button showAllButton;

    @FXML
    private Button searchButton;

    @FXML
    private Button reportButton;

    @FXML
    private AnchorPane workerPane;

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
    private ComboBox<String> searchByCombo;
    @FXML
    private TextField searchField;
    @FXML
    private Button deleteButton;
    @FXML

    private TableView<workerTableModel> workerTable;
    @FXML private TableColumn<workerTableModel, Integer> idColumn;
    @FXML private TableColumn<workerTableModel, String> nameColumn;
    @FXML private TableColumn<workerTableModel, String> workerFieldColumn;
    @FXML private TableColumn<workerTableModel, String> phoneColumn;
    @FXML private TableColumn<workerTableModel, String> endOfLaseColumn;
    @FXML private TableColumn<workerTableModel, String> birthdateColumn;
    @FXML private TableColumn<workerTableModel, BigDecimal> salarytColumn;
    @FXML private TableColumn<workerTableModel, Integer> workHourstColumn;



    @FXML
    public void initialize() {
        searchByCombo.setItems(FXCollections.observableArrayList("Worker ID", "Field"));

        workerTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                workerTableModel selected = workerTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/WorkerAdd.fxml"));
                        Scene scene = new Scene(loader.load());

                        WorkerAddController controller = loader.getController();
                        controller.loadOwnerData(selected.getWorkerId());

                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(scene);
                        stage.setTitle("Update Worker");
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
            SELECT w.worker_id,
                   CONCAT(p.first_name, ' ', COALESCE(p.middle_name, ''), ' ', p.last_name) AS full_name,
                   w.field, w.salary, w.work_hours, w.end_of_lease,
                   p.phone_number, p.birthdate
            FROM workers w
            JOIN person p ON w.worker_id = p.person_id
            ORDER BY w.worker_id
        """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            ObservableList<workerTableModel> data = FXCollections.observableArrayList();

            while (rs.next()) {
                data.add(new workerTableModel(
                        rs.getInt("worker_id"),
                        rs.getString("full_name"),
                        rs.getString("field"),
                        rs.getBigDecimal("salary"),
                        rs.getInt("work_hours"),
                        rs.getString("end_of_lease"),
                        rs.getString("phone_number"),
                        rs.getString("birthdate")
                ));
            }

            idColumn.setCellValueFactory(cell -> cell.getValue().workerIdProperty().asObject());
            nameColumn.setCellValueFactory(cell -> cell.getValue().workerNameProperty());
            workerFieldColumn.setCellValueFactory(cell -> cell.getValue().fieldProperty());
            salarytColumn.setCellValueFactory(cell -> cell.getValue().salaryProperty());

            workHourstColumn.setCellValueFactory(cell -> cell.getValue().workHoursProperty().asObject());
            endOfLaseColumn.setCellValueFactory(cell -> cell.getValue().endOfLeaseProperty());
            phoneColumn.setCellValueFactory(cell -> cell.getValue().phoneNumberProperty());
            birthdateColumn.setCellValueFactory(cell -> cell.getValue().birthdateProperty());

            workerTable.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load workers: " + e.getMessage());
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
        String criteria = searchByCombo.getValue();
        String input = searchField.getText().trim();

        if (criteria == null || input.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a search field and enter a value.");
            return;
        }

        String sqlColumn;
        String condition;
        switch (criteria) {
            case "Worker ID" -> {
                sqlColumn = "w.worker_id";
                condition = sqlColumn + " = ?";
            }
            case "National ID" -> {
                sqlColumn = "p.national_id";
                condition = sqlColumn + " ILIKE ?";
            }
            case "Field" -> {
                sqlColumn = "w.field";
                condition = sqlColumn + " ILIKE ?";
            }
            default -> {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid search criteria.");
                return;
            }
        }

        String sql = """
        SELECT w.worker_id,
               CONCAT(p.first_name, ' ', COALESCE(p.middle_name, ''), ' ', p.last_name) AS full_name,
               w.field, w.salary, w.work_hours, w.end_of_lease,
               p.phone_number, p.birthdate
        FROM workers w
        JOIN person p ON w.worker_id = p.person_id
        WHERE %s
        ORDER BY w.worker_id
    """.formatted(condition);

        try (Connection conn = DBConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (criteria.equals("Worker ID")) {
                stmt.setInt(1, Integer.parseInt(input));
            } else {
                stmt.setString(1, "%" + input + "%");
            }

            ResultSet rs = stmt.executeQuery();
            ObservableList<workerTableModel> data = FXCollections.observableArrayList();

            while (rs.next()) {
                data.add(new workerTableModel(
                        rs.getInt("worker_id"),
                        rs.getString("full_name"),
                        rs.getString("field"),
                        rs.getBigDecimal("salary"),
                        rs.getInt("work_hours"),
                        rs.getString("end_of_lease"),
                        rs.getString("phone_number"),
                        rs.getString("birthdate")
                ));
            }

            workerTable.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to search workers: " + e.getMessage());
        }
    }

    @FXML
    private void handaleDeleteWorker(ActionEvent event) {
        workerTableModel selected = workerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a worker to delete.");
            return;
        }

        int workerId = selected.getWorkerId();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this worker?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait();

        if (confirm.getResult() != ButtonType.YES) {
            return;
        }

        try (Connection conn = DBConnector.connect()) {
            // Delete from worker_payment (optional, if cascading isn't configured)
            PreparedStatement deletePayment = conn.prepareStatement(
                    "DELETE FROM worker_payment WHERE worker_id = ?"
            );
            deletePayment.setInt(1, workerId);
            deletePayment.executeUpdate();

            // Delete from workers
            PreparedStatement deleteWorker = conn.prepareStatement(
                    "DELETE FROM workers WHERE worker_id = ?"
            );
            deleteWorker.setInt(1, workerId);
            deleteWorker.executeUpdate();

            // Delete from person (assuming person_id == worker_id)
            PreparedStatement deletePerson = conn.prepareStatement(
                    "DELETE FROM person WHERE person_id = ?"
            );
            deletePerson.setInt(1, workerId);
            deletePerson.executeUpdate();

            workerTable.getItems().remove(selected);
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "Worker deleted successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Deletion failed: " + e.getMessage());
        }
    }
    @FXML
    private void handleReport(ActionEvent event) {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            String url = "jdbc:postgresql://localhost:1234/RealEstate";
            String user = "postgres";
            String password = "raya4039";
            Connection cn = DriverManager.getConnection(url, user, password);
            String jrxmlPath = "C:/Users/DELL/JaspersoftWorkspace/WorkersReport/WorkersReport.jrxml";
            InputStream inp = new FileInputStream(new File(jrxmlPath));
            JasperDesign jsp = JRXmlLoader.load(inp);
            JasperReport report = JasperCompileManager.compileReport(jsp);
            JasperPrint filledReport = JasperFillManager.fillReport(report, null, cn);
            JFrame frame = new JFrame("Workers Report");
            frame.getContentPane().add(new JRViewer(filledReport));
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
