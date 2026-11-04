package com.library.controller;

import com.library.Util.DBConnection;
import dto.IssueDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import model.IssueBookModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class IssueBookController {

    @FXML private ComboBox<String> memberComboBox;
    @FXML private ComboBox<String> bookComboBox;
    @FXML private DatePicker issueDatePicker;
    @FXML private DatePicker dueDatePicker;
    @FXML private Label messageLabel;

    private Map<String, String> memberMap = new HashMap<>();
    private Map<String, String> bookMap = new HashMap<>();


    private final IssueBookModel issueBookModel = new IssueBookModel();

    @FXML
    public void initialize() {
        issueDatePicker.setValue(LocalDate.now());
        dueDatePicker.setValue(LocalDate.now().plusDays(14));
        loadMembers();
        loadBooks();
    }

    private void loadMembers() {
        ObservableList<String> list = FXCollections.observableArrayList();
        memberMap.clear();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT member_id, full_name FROM member");
            while (rs.next()) {
                String id = rs.getString("member_id");
                String display = id + " - " + rs.getString("full_name");
                list.add(display);
                memberMap.put(display, id);
            }
            memberComboBox.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading members: " + e.getMessage());
        }
    }

    private void loadBooks() {
        ObservableList<String> list = FXCollections.observableArrayList();
        bookMap.clear();
        try {
            Connection conn = DBConnection.getInstance().getConnection();

            ResultSet rs = conn.createStatement().executeQuery("SELECT isbn, title FROM book WHERE quantity > 0");
            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String display = isbn + " - " + rs.getString("title");
                list.add(display);
                bookMap.put(display, isbn);
            }
            bookComboBox.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleIssueBook() {
        String memberDisplay = memberComboBox.getValue();
        String bookDisplay = bookComboBox.getValue();
        LocalDate issueDate = issueDatePicker.getValue();
        LocalDate dueDate = dueDatePicker.getValue();

        if (memberDisplay == null || bookDisplay == null || issueDate == null || dueDate == null) {
            messageLabel.setText("Please fill all fields!");
            return;
        }
        if (dueDate.isBefore(issueDate)) {
            messageLabel.setText("Due date must be after issue date!");
            return;
        }

        try {
            String memberId = memberMap.get(memberDisplay);
            String isbn = bookMap.get(bookDisplay);


            IssueDTO dto = new IssueDTO(memberId, isbn, issueDate, dueDate, "ISSUED");


            boolean isIssued = issueBookModel.issueBook(dto);

            if (isIssued) {

                Connection conn = DBConnection.getInstance().getConnection();
                PreparedStatement pst = conn.prepareStatement("UPDATE book SET quantity = quantity - 1 WHERE isbn = ?");
                pst.setString(1, isbn);
                pst.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Book Issued Successfully!");
                alert.setContentText("Member: " + memberDisplay + "\nBook: " + bookDisplay);
                alert.showAndWait();

                handleClear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        memberComboBox.setValue(null);
        bookComboBox.setValue(null);
        issueDatePicker.setValue(LocalDate.now());
        dueDatePicker.setValue(LocalDate.now().plusDays(14));
        messageLabel.setText("");
        loadBooks();
    }
}