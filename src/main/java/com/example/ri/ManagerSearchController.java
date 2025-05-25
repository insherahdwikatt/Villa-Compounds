package com.example.ri;


import net.sf.jasperreports.engine.JasperPrint;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManagerSearchController {


    @FXML
    private Button logoutButton;

    @FXML
    private Button ShowAll;

    @FXML
    private Button myInfoButton;

    @FXML
    private Button deleteButton;
    @FXML
    private Button managerButton;

    @FXML
    private Button Search;

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

    @FXML
    private Button reportButton;


    @FXML
    private ComboBox<String> searchByCombo;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<ManagerTableModel> managerTable;


    @FXML
    private TableColumn<ManagerTableModel, Integer> idColumn;
    @FXML
    private TableColumn<ManagerTableModel, String> nameColumn;
    @FXML
    private TableColumn<ManagerTableModel, Integer> compoundIdColumn;
    @FXML
    private TableColumn<ManagerTableModel, String> phoneColumn;
    @FXML
    private TableColumn<ManagerTableModel, String> emailColumn;
    @FXML
    private TableColumn<ManagerTableModel, String> salaryColumn;
    @FXML
    private TableColumn<ManagerTableModel, String> bankColumn;

    private String role;
    private String userId;


    private int selectedManagerId;


    @FXML
    public void initialize() {
        searchByCombo.setItems(FXCollections.observableArrayList("Manager ID", "Manager Name", "Compound ID", "Salary"));

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        compoundIdColumn.setCellValueFactory(new PropertyValueFactory<>("compoundId"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        bankColumn.setCellValueFactory(new PropertyValueFactory<>("bankAccount"));

        managerTable.setEditable(true);

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            ManagerTableModel manager = event.getRowValue();
            String newFullName = event.getNewValue().trim();

            if (!newFullName.contains(" ")) {
                showAlert("Validation Error", "Please enter full name (First Last)");
                return;
            }

            manager.setName(newFullName);  // يقسم داخلياً إلى first + last
        });

        managerTable.setRowFactory(tv -> {
            TableRow<ManagerTableModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    ManagerTableModel clickedManager = row.getItem();
                    openManagerDetails(clickedManager);
                }
            });
            return row;
        });


        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setOnEditCommit(event -> updateField("person", "email", event.getNewValue(), event.getRowValue().getId()));

        phoneColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        phoneColumn.setOnEditCommit(event -> updateField("person", "phone_number", event.getNewValue(), event.getRowValue().getId()));

        salaryColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        salaryColumn.setOnEditCommit(event -> updateField("manager", "salary", event.getNewValue(), event.getRowValue().getId()));

        bankColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        bankColumn.setOnEditCommit(event -> updateField("person", "bank_account", event.getNewValue(), event.getRowValue().getId()));

        ObservableList<Integer> compoundIds = getCompoundIds();
        compoundIdColumn.setCellFactory(ComboBoxTableCell.forTableColumn(compoundIds));
        compoundIdColumn.setOnEditCommit(event -> updateField("manager", "compound_id", String.valueOf(event.getNewValue()), event.getRowValue().getId()));
    }

    private void updateName(int id, String fullName) {
        String[] parts = fullName.split(" ", 2);
        try (Connection conn = DBConnector.connect()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE person SET first_name=?, last_name=? WHERE person_id=?");
            stmt.setString(1, parts[0]);
            stmt.setString(2, parts[1]);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void updateField(String table, String column, String value, int id) {
        String key = table.equals("person") ? "person_id" : "id";

        try (Connection conn = DBConnector.connect()) {
            PreparedStatement stmt;

            if (column.equals("salary")) {
                stmt = conn.prepareStatement("UPDATE " + table + " SET " + column + "=? WHERE " + key + "=?");
                stmt.setBigDecimal(1, new BigDecimal(value));
                stmt.setInt(2, id);
            } else if (column.equals("compound_id")) {
                stmt = conn.prepareStatement("UPDATE " + table + " SET " + column + "=? WHERE " + key + "=?");
                stmt.setInt(1, Integer.parseInt(value));  // 🔧 هنا الحل
                stmt.setInt(2, id);
            } else {
                stmt = conn.prepareStatement("UPDATE " + table + " SET " + column + "=? WHERE " + key + "=?");
                stmt.setString(1, value);
                stmt.setInt(2, id);
            }

            stmt.executeUpdate();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private ObservableList<Integer> getCompoundIds() {
        ObservableList<Integer> ids = FXCollections.observableArrayList();
        try (Connection conn = DBConnector.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT compound_id FROM compounds");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("compound_id"));
            }
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
        return ids;
    }


    @FXML
    private void handleMyInfo(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/AdminInfo.fxml"));
            Scene scene = new Scene(loader.load());

            AdminInfoController AdminInfoController = loader.getController();
            UserInfo userInfo = UserInfo.getInstance();

            AdminInfoController.setRoleAndUser(userInfo.getRole(), userInfo.getUserId());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Admin Info");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        managerTable.edit(-1, null); // تأكيد commit لأي edits مفتوحة

        ManagerTableModel selected = managerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a manager from the table first.");
            return;
        }

        String[] fullName = selected.getName().trim().split(" ", 2);
        if (fullName.length < 2) {
            showAlert("Validation Error", "Please enter full name (First Last)");
            return;
        }
        String firstName = selected.getFirstName();
        String lastName = selected.getLastName();


        String phone = selected.getPhone();
        String email = selected.getEmail();
        String bank = selected.getBankAccount();
        String salaryStr = selected.getSalary();
        int compoundId = selected.getCompoundId();
        int id = selected.getId();

        try (Connection conn = DBConnector.connect()) {
            conn.setAutoCommit(false);

            String updatePerson = """
                        UPDATE person 
                        SET first_name=?, last_name=?, phone_number=?, email=?, bank_account=? 
                        WHERE person_id=?
                    """;
            try (PreparedStatement stmt1 = conn.prepareStatement(updatePerson)) {
                stmt1.setString(1, firstName);
                stmt1.setString(2, lastName);
                stmt1.setString(3, phone);
                stmt1.setString(4, email);
                stmt1.setString(5, bank);
                stmt1.setInt(6, id);
                stmt1.executeUpdate();
            }

            String updateManager = "UPDATE manager SET salary=?, compound_id=? WHERE id=?";
            try (PreparedStatement stmt2 = conn.prepareStatement(updateManager)) {
                stmt2.setBigDecimal(1, new BigDecimal(salaryStr));
                stmt2.setInt(2, compoundId);
                stmt2.setInt(3, id);
                stmt2.executeUpdate();
            }

            conn.commit();
            showAlert("Success", "Manager updated successfully.");
            handleShowAll(new ActionEvent());

        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Salary must be a valid number.");
        } catch (Exception e) {
            showAlert("Database Error", "Update failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String selectedField = searchByCombo.getValue();
        String searchValue = searchField.getText().trim();

        if (selectedField == null || searchValue.isEmpty()) {
            showAlert("Validation Error", "Please select a search field and enter a value.");
            return;
        }

        String sqlColumn;
        switch (selectedField) {
            case "Manager ID" -> sqlColumn = "manager.id";
            case "Manager Name" -> sqlColumn = "p.first_name || ' ' || p.last_name";
            case "Compound ID" -> sqlColumn = "manager.compound_id";
            case "Salary" -> sqlColumn = "manager.salary";
            default -> {
                showAlert("Validation Error", "Invalid search field selected.");
                return;
            }
        }

        String baseQuery = """
                    SELECT manager.id,
                           p.first_name || ' ' || p.last_name AS full_name,
                           manager.compound_id,
                           p.phone_number,
                           p.email,
                           manager.salary,
                           p.bank_account
                    FROM manager
                    JOIN person p ON manager.id = p.person_id
                """;

        try (Connection conn = DBConnector.connect()) {
            PreparedStatement stmt;
            String finalQuery;

            if (selectedField.equals("Manager ID") || selectedField.equals("Compound ID") || selectedField.equals("Salary")) {
                finalQuery = baseQuery + " WHERE " + sqlColumn + " = ?";
                stmt = conn.prepareStatement(finalQuery);
                stmt.setInt(1, Integer.parseInt(searchValue));
            } else {
                finalQuery = baseQuery + " WHERE " + sqlColumn + " ILIKE ?";
                stmt = conn.prepareStatement(finalQuery);
                stmt.setString(1, "%" + searchValue + "%");
            }

            ResultSet rs = stmt.executeQuery();
            ObservableList<ManagerTableModel> results = FXCollections.observableArrayList();

            while (rs.next()) {
                results.add(new ManagerTableModel(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getInt("compound_id"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("salary"),
                        rs.getString("bank_account")
                ));
            }

            if (results.isEmpty()) {
                showAlert("No Results", "No matching managers found.");
            }

            managerTable.setItems(results);

        } catch (NumberFormatException e) {
            showAlert("Input Error", "ID and numeric fields must be numbers.");
        } catch (Exception e) {
            showAlert("Database Error", "Failed to execute search: " + e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
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
    private void handleShowAll(ActionEvent event) {
        try (Connection conn = DBConnector.connect()) {
            String sql = """
                        SELECT manager.id,
                               p.first_name || ' ' || p.last_name AS full_name,
                               manager.compound_id,
                               p.phone_number,
                               p.email,
                               manager.salary,
                               p.bank_account
                        FROM manager
                        JOIN person p ON manager.id = p.person_id
                    """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            ObservableList<ManagerTableModel> results = FXCollections.observableArrayList();

            while (rs.next()) {
                results.add(new ManagerTableModel(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getInt("compound_id"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("salary"),
                        rs.getString("bank_account")
                ));
            }

            if (results.isEmpty()) {
                showAlert("Info", "No managers found.");
            }

            managerTable.setItems(results);

        } catch (Exception e) {
            showAlert("Database Error", "Failed to load data: " + e.getMessage());
        }
    }

    public void saverole(String role, String userId) {
        this.role = role;
        this.userId = userId;
    }

    private void openManagerDetails(ManagerTableModel manager) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ri/ManagerAdd.fxml"));
            Scene scene = new Scene(loader.load());

            ManagerAddController controller = loader.getController();
            controller.showData(manager.getId());


            Stage stage = (Stage) managerTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Manager Details");
            stage.show();
            addManagerButton.setVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load manager details view.");
        }
    }

    @FXML
    private void handleDeleteManager(ActionEvent event) {
        ManagerTableModel selected = managerTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("No Selection", "Please select a manager to delete.");
            return;
        }

        int managerId = selected.getId();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Manager");
        confirm.setContentText("Are you sure you want to delete manager ID " + managerId + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = DBConnector.connect()) {
                    conn.setAutoCommit(false);

                    // Step 1: Delete from manager table
                    try (PreparedStatement deleteManager = conn.prepareStatement("DELETE FROM manager WHERE id = ?")) {
                        deleteManager.setInt(1, managerId);
                        deleteManager.executeUpdate();
                    }

                    // Step 2: Delete from person table
                    try (PreparedStatement deletePerson = conn.prepareStatement("DELETE FROM person WHERE person_id = ?")) {
                        deletePerson.setInt(1, managerId);
                        deletePerson.executeUpdate();
                    }

                    conn.commit();
                    showAlert("Success", "Manager deleted successfully.");
                    handleShowAll(null); // Refresh table

                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error", "Failed to delete manager: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleReport(ActionEvent event) {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            String url = "jdbc:postgresql://localhost:5432/RealEstate";
            String user = "postgres";
            String password = "1234";
            Connection cn = DriverManager.getConnection(url, user, password);
            String jrxmlPath = "C:/Users/inshe/JaspersoftWorkspace/ManagerReport/managerReport.jrxml";
            InputStream inp = new FileInputStream(new File(jrxmlPath));
            JasperDesign jsp = JRXmlLoader.load(inp);
            JasperReport report = JasperCompileManager.compileReport(jsp);
            JasperPrint filledReport = JasperFillManager.fillReport(report, null, cn);
            JFrame frame = new JFrame("Manager Report");
            frame.getContentPane().add(new JRViewer(filledReport));
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}