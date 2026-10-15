package com.library.controller;

import com.library.Util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class IssueBookController {

    @FXML
    private ComboBox<String> memberComboBox;
    @FXML
    private ComboBox<String> bookComboBox;
    @FXML
    private DatePicker issueDatePicker;
    @FXML
    private DatePicker dueDatePicker;
    @FXML
    private Label messageLabel;

    private Map<String, String> memberMap = new HashMap<>();
    private Map<String, String> bookMap = new HashMap<>();

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
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT member_id, full_name FROM member");
            while (rs.next()) {
                String id = rs.getString("member_id");
                String name = rs.getString("full_name");
                String display = id + " - " + name;
                list.add(display);
                memberMap.put(display, id);
            }
            memberComboBox.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBooks() {
        ObservableList<String> list = FXCollections.observableArrayList();
        bookMap.clear();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT isbn, title FROM book WHERE quantity > 0");
            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                String display = isbn + " - " + title;
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
        String member = memberComboBox.getValue();
        String book = bookComboBox.getValue();
        LocalDate issueDate = issueDatePicker.getValue();
        LocalDate dueDate = dueDatePicker.getValue();

        if (member == null) {
            messageLabel.setText("Please select a member.");
            return;
        }
        if (book == null) {
            messageLabel.setText("Please select a book.");
            return;
        }
        if (issueDate == null) {
            messageLabel.setText("Please select the issue date.");
            return;
        }
        if (dueDate == null) {
            messageLabel.setText("Please select the due date.");
            return;
        }
        if (dueDate.isBefore(issueDate)) {
            messageLabel.setText("Due date must be after the issue date.");
            return;
        }

        String memberId = memberMap.get(member);
        String isbn = bookMap.get(book);

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO issued_books (member_id, isbn, issue_date, due_date, status) VALUES (?, ?, ?, ?, 'ISSUED')";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, memberId);
            pst.setString(2, isbn);
            pst.setDate(3, java.sql.Date.valueOf(issueDate));
            pst.setDate(4, java.sql.Date.valueOf(dueDate));
            pst.executeUpdate();

            String updateSql = "UPDATE book SET quantity = quantity - 1 WHERE isbn = ?";
            PreparedStatement pst2 = conn.prepareStatement(updateSql);
            pst2.setString(1, isbn);
            pst2.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Book Issued");
            alert.setHeaderText("Book Issued Successfully");
            alert.setContentText("Member: " + member + "\nBook: " + book + "\nIssue Date: " + issueDate + "\nDue Date: " + dueDate);
            alert.showAndWait();

            messageLabel.setText("Book issued successfully!");
            handleClear();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("DB Error: " + e.getMessage());
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