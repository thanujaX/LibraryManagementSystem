package Controller.Login;

import dto.MemberDTO;
import model.MemberModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ManageMembersController {

    @FXML private TextField searchField;
    @FXML private TableView<MemberDTO> memberTable;
    @FXML private TableColumn<MemberDTO, String> memberIdColumn;
    @FXML private TableColumn<MemberDTO, String> nameColumn;
    @FXML private TableColumn<MemberDTO, String> emailColumn;
    @FXML private TableColumn<MemberDTO, String> phoneColumn;
    @FXML private TableColumn<MemberDTO, String> addressColumn;
    @FXML private TableColumn<MemberDTO, String> createdAtColumn;
    @FXML private TableColumn<MemberDTO, String> updatedAtColumn;
    @FXML private Label memberCountLabel;

    private final ObservableList<MemberDTO> memberList =
            FXCollections.observableArrayList();
    private final MemberModel memberModel = new MemberModel();
    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // =========================================
    // INITIALIZE
    // =========================================
    @FXML
    public void initialize() {

        memberTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        memberIdColumn.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getMemberId())
        );
        nameColumn.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getFullName())
        );
        emailColumn.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getEmail())
        );
        phoneColumn.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getPhone())
        );
        addressColumn.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().getAddress())
        );
        createdAtColumn.setCellValueFactory(
                data -> new SimpleStringProperty(formatDate(data.getValue().getCreatedAt()))
        );
        updatedAtColumn.setCellValueFactory(
                data -> new SimpleStringProperty(formatDate(data.getValue().getUpdatedAt()))
        );

        loadAllMembersFromDB();
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(formatter);
    }

    private void loadAllMembersFromDB() {
        try {
            memberList.setAll(memberModel.getAllMembers());
            memberTable.setItems(memberList);
            updateMemberCount();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unable to load members: " + e.getMessage());
        }
    }

    // =========================================
    // SEARCH MEMBER
    // =========================================
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadAllMembersFromDB();
            return;
        }
        try {
            ObservableList<MemberDTO> searchResults = FXCollections.observableArrayList();
            for (MemberDTO dto : memberModel.getAllMembers()) {
                String id = safeText(dto.getMemberId()).toLowerCase();
                String name = safeText(dto.getFullName()).toLowerCase();
                String email = safeText(dto.getEmail()).toLowerCase();
                String phone = safeText(dto.getPhone()).toLowerCase();

                if (id.contains(searchText)
                        || name.contains(searchText)
                        || email.contains(searchText)
                        || phone.contains(searchText)) {
                    searchResults.add(dto);
                }
            }
            memberTable.setItems(searchResults);
            memberCountLabel.setText("Members: " + searchResults.size());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Search failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        loadAllMembersFromDB();
    }

    // =========================================
    // EDIT MEMBER
    // =========================================
    @FXML
    private void handleEdit() {
        MemberDTO selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showWarning("Please select a member!");
            return;
        }

        TextField memberIdField = new TextField(selectedMember.getMemberId());
        memberIdField.setDisable(true);
        TextField nameField = new TextField(safeText(selectedMember.getFullName()));
        TextField emailField = new TextField(safeText(selectedMember.getEmail()));
        TextField phoneField = new TextField(safeText(selectedMember.getPhone()));
        TextField addressField = new TextField(safeText(selectedMember.getAddress()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(15));
        grid.add(new Label("Member ID:"), 0, 0);
        grid.add(memberIdField, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Member");
        dialog.setHeaderText("Update Member Details");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    String name = nameField.getText().trim();
                    if (name.isEmpty()) {
                        showWarning("Member Name is required!");
                        return;
                    }


                    MemberDTO dto = new MemberDTO(
                            memberIdField.getText().trim(),
                            name,
                            emailField.getText().trim(),
                            phoneField.getText().trim(),
                            addressField.getText().trim(),
                            selectedMember.getCreatedAt(),
                            LocalDateTime.now()
                    );

                    boolean updated = memberModel.updateMember(dto);
                    if (updated) {
                        loadAllMembersFromDB();
                        showInformation("Member Updated Successfully!");
                    } else {
                        showWarning("Member Update Failed!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Update Error: " + e.getMessage());
                }
            }
        });
    }

    // =========================================
    // DELETE MEMBER
    // =========================================
    @FXML
    private void handleDelete() {
        MemberDTO selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showWarning("Please select a member!");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Member");
        confirmation.setHeaderText("Confirm Member Deletion");
        confirmation.setContentText("Are you sure you want to delete " + selectedMember.getFullName() + "?");

        confirmation.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    boolean deleted = memberModel.deleteMember(selectedMember.getMemberId());
                    if (deleted) {
                        loadAllMembersFromDB();
                        showInformation("Member Deleted Successfully!");
                    } else {
                        showWarning("Member Delete Failed!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Delete Error: " + e.getMessage());
                }
            }
        });
    }

    private void updateMemberCount() {
        memberCountLabel.setText("Members: " + memberList.size());
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private void showWarning(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }

    private void showInformation(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}