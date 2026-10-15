package com.library.controller;

import com.library.Util.DBConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ManageMembersController {

    @FXML private TextField searchField;
    @FXML private TableView<Member> memberTable;
    @FXML private TableColumn<Member, String> memberIdColumn;
    @FXML private TableColumn<Member, String> nameColumn;
    @FXML private TableColumn<Member, String> emailColumn;
    @FXML private TableColumn<Member, String> phoneColumn;
    @FXML private TableColumn<Member, String> addressColumn;
    @FXML private Label memberCountLabel;

    private final ObservableList<Member> memberList = FXCollections.observableArrayList();


    // INITIALIZE//

    @FXML
    public void initialize() {
        memberIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMemberId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));

        loadAllMembersFromDB(); // DB eken load karanne meken
    }

    private void loadAllMembersFromDB() {
        memberList.clear();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM member ORDER BY created_at DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                memberList.add(new Member(
                        rs.getString("member_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }
            memberTable.setItems(memberList);
            updateMemberCount();
        } catch (Exception e) {
            e.printStackTrace();
            showWarning("DB Error: " + e.getMessage());
        }
    }


    // SEARCH//

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadAllMembersFromDB();
            return;
        }

        ObservableList<Member> searchResults = FXCollections.observableArrayList();
        try {
            Connection conn = DBConnection.getInstance().getConnection();

            String sql = "SELECT * FROM member WHERE member_id LIKE ? OR full_name LIKE ? OR email LIKE ? OR phone LIKE ? OR address LIKE ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            String like = "%" + searchText + "%";
            pst.setString(1, like);
            pst.setString(2, like);
            pst.setString(3, like);
            pst.setString(4, like);
            pst.setString(5, like);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                searchResults.add(new Member(
                        rs.getString("member_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }
            memberTable.setItems(searchResults);
            memberCountLabel.setText("Members: " + searchResults.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        loadAllMembersFromDB();
    }

    // EDIT MEMBER//

    @FXML
    private void handleEdit() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Edit Member"); alert.setHeaderText("No Member Selected");
            alert.setContentText("Please select a member from the table."); alert.showAndWait(); return;
        }

        TextField memberIdField = new TextField(selectedMember.getMemberId());
        TextField nameField = new TextField(selectedMember.getFullName());
        TextField emailField = new TextField(selectedMember.getEmail());
        TextField phoneField = new TextField(selectedMember.getPhone());
        TextField addressField = new TextField(selectedMember.getAddress());
        memberIdField.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(12); grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(new Label("Member ID:"), 0, 0); grid.add(memberIdField, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1); grid.add(nameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(emailField, 1, 2);
        grid.add(new Label("Phone Number:"), 0, 3); grid.add(phoneField, 1, 3);
        grid.add(new Label("Address:"), 0, 4); grid.add(addressField, 1, 4);
        nameField.setPrefWidth(300); emailField.setPrefWidth(300); phoneField.setPrefWidth(300); addressField.setPrefWidth(300);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Member"); dialog.setHeaderText("Edit Member Details");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(grid);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            if (name.isEmpty()) { showWarning("Please enter the member name."); event.consume(); return; }
            if (!email.contains("@") || !email.contains(".")) { showWarning("Please enter a valid email address."); event.consume(); return; }
            if (!phone.matches("\\d{10}")) { showWarning("Phone number must contain 10 digits."); event.consume(); return; }
            if (address.isEmpty()) { showWarning("Please enter the address."); event.consume(); }
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    Connection conn = DBConnection.getInstance().getConnection();
                    String sql = "UPDATE member SET full_name = ?, email = ?, phone = ?, address = ?, updated_at = NOW() WHERE member_id = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, nameField.getText().trim());
                    pst.setString(2, emailField.getText().trim());
                    pst.setString(3, phoneField.getText().trim());
                    pst.setString(4, addressField.getText().trim());
                    pst.setString(5, selectedMember.getMemberId());
                    pst.executeUpdate();

                    selectedMember.setFullName(nameField.getText().trim());
                    selectedMember.setEmail(emailField.getText().trim());
                    selectedMember.setPhone(phoneField.getText().trim());
                    selectedMember.setAddress(addressField.getText().trim());
                    memberTable.refresh();

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Member Updated"); success.setHeaderText("Member Updated Successfully");
                    success.setContentText("The member details have been updated in DB."); success.showAndWait();

                } catch (Exception e) {
                    e.printStackTrace();
                    showWarning("DB Update Failed: " + e.getMessage());
                }
            }
        });
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error"); alert.setHeaderText("Invalid Information"); alert.setContentText(message); alert.showAndWait();
    }

    // DELETE MEMBER//

    @FXML
    private void handleDelete() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Delete Member"); alert.setHeaderText("No Member Selected");
            alert.setContentText("Please select a member from the table."); alert.showAndWait(); return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Member"); confirmation.setHeaderText("Delete Member");
        confirmation.setContentText("Are you sure you want to delete\n\n" + selectedMember.getFullName() + " (" + selectedMember.getMemberId() + ")?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Connection conn = DBConnection.getInstance().getConnection();
                    String sql = "DELETE FROM member WHERE member_id = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, selectedMember.getMemberId());
                    pst.executeUpdate();

                    memberList.remove(selectedMember);
                    memberTable.setItems(memberList);
                    updateMemberCount();

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Member Deleted"); success.setHeaderText("Member Deleted Successfully");
                    success.setContentText("The member has been deleted from DB."); success.showAndWait();

                } catch (Exception e) {
                    e.printStackTrace();
                    showWarning("Delete Failed: " + e.getMessage());
                }
            }
        });
    }

    private void updateMemberCount() {
        memberCountLabel.setText("Members: " + memberList.size());
    }


    // MEMBER MODEL//

    public static class Member {
        private final String memberId;
        private String fullName;
        private String email;
        private String phone;
        private String address;

        public Member(String memberId, String fullName, String email, String phone, String address) {
            this.memberId = memberId; this.fullName = fullName; this.email = email; this.phone = phone; this.address = address;
        }
        public String getMemberId() { return memberId; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public void setEmail(String email) { this.email = email; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setAddress(String address) { this.address = address; }
    }
}