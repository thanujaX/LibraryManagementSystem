package com.library.controller;

import com.library.Util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ReturnBookController {

    @FXML
    private ComboBox<String> borrowedBookComboBox;
    @FXML
    private TextField memberField;
    @FXML
    private TextField bookField;
    @FXML
    private DatePicker borrowedDatePicker;
    @FXML
    private DatePicker dueDatePicker;
    @FXML
    private DatePicker returnDatePicker;
    @FXML
    private Label statusLabel;
    @FXML
    private Label messageLabel;

    private Map<String, Integer> issueMap = new HashMap<>();
    private Map<Integer, String> isbnMap = new HashMap<>();

    @FXML
    public void initialize() {
        returnDatePicker.setValue(LocalDate.now());
        statusLabel.setText("Not Checked");
        loadBorrowedBooks();
        borrowedBookComboBox.setOnAction(event -> {
            String selected = borrowedBookComboBox.getValue();
            if (selected != null) {
                loadBookDetails();
            }
        });
        returnDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> checkOverdueStatus());
    }

    private void loadBorrowedBooks() {
        ObservableList<String> list = FXCollections.observableArrayList();
        issueMap.clear();
        isbnMap.clear();
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ib.issue_id, ib.isbn, b.title, m.full_name FROM issued_books ib JOIN book b ON ib.isbn = b.isbn JOIN member m ON ib.member_id = m.member_id WHERE ib.status = 'ISSUED'");
            while (rs.next()) {
                int issueId = rs.getInt("issue_id");
                String title = rs.getString("title");
                String memberName = rs.getString("full_name");
                String display = issueId + " - " + title + " (" + memberName + ")";
                list.add(display);
                issueMap.put(display, issueId);
                isbnMap.put(issueId, rs.getString("isbn"));
            }
            borrowedBookComboBox.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("DB Error: " + e.getMessage());
        }
    }

    private void loadBookDetails() {
        String selected = borrowedBookComboBox.getValue();
        if (selected == null) return;
        int issueId = issueMap.get(selected);
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT ib.issue_date, ib.due_date, m.member_id, m.full_name, b.isbn, b.title FROM issued_books ib JOIN member m ON ib.member_id = m.member_id JOIN book b ON ib.isbn = b.isbn WHERE ib.issue_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, issueId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                memberField.setText(rs.getString("member_id") + " - " + rs.getString("full_name"));
                bookField.setText(rs.getString("isbn") + " - " + rs.getString("title"));
                borrowedDatePicker.setValue(rs.getDate("issue_date").toLocalDate());
                dueDatePicker.setValue(rs.getDate("due_date").toLocalDate());
                checkOverdueStatus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkOverdueStatus() {
        LocalDate dueDate = dueDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();
        if (dueDate == null || returnDate == null) {
            statusLabel.setText("Not Checked");
            return;
        }
        if (returnDate.isAfter(dueDate)) {
            statusLabel.setText("OVERDUE");
        } else {
            statusLabel.setText("ON TIME");
        }
    }

    @FXML
    private void handleReturnBook() {
        if (borrowedBookComboBox.getValue() == null) {
            messageLabel.setText("Please select a borrowed book.");
            return;
        }
        if (borrowedDatePicker.getValue() == null) {
            messageLabel.setText("Please select the borrowed date.");
            return;
        }
        if (dueDatePicker.getValue() == null) {
            messageLabel.setText("Please select the due date.");
            return;
        }
        if (returnDatePicker.getValue() == null) {
            messageLabel.setText("Please select the return date.");
            return;
        }
        if (returnDatePicker.getValue().isBefore(borrowedDatePicker.getValue())) {
            messageLabel.setText("Return date cannot be before borrowed date.");
            return;
        }

        checkOverdueStatus();
        String selected = borrowedBookComboBox.getValue();
        int issueId = issueMap.get(selected);
        String isbn = isbnMap.get(issueId);
        LocalDate returnDate = returnDatePicker.getValue();
        LocalDate dueDate = dueDatePicker.getValue();
        String status = returnDate.isAfter(dueDate) ? "OVERDUE" : "RETURNED ON TIME";

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql1 = "UPDATE issued_books SET return_date = ?, status = ? WHERE issue_id = ?";
            PreparedStatement pst1 = conn.prepareStatement(sql1);
            pst1.setDate(1, java.sql.Date.valueOf(returnDate));
            pst1.setString(2, status);
            pst1.setInt(3, issueId);
            pst1.executeUpdate();

            String sql2 = "UPDATE book SET quantity = quantity + 1 WHERE isbn = ?";
            PreparedStatement pst2 = conn.prepareStatement(sql2);
            pst2.setString(1, isbn);
            pst2.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Book Returned");
            alert.setHeaderText("Book Returned Successfully");
            alert.setContentText("Book: " + bookField.getText() + "\nMember: " + memberField.getText() + "\nReturn Date: " + returnDate + "\nStatus: " + status);
            alert.showAndWait();

            messageLabel.setText("Book returned successfully!");
            handleClear();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("DB Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        borrowedBookComboBox.setValue(null);
        memberField.clear();
        bookField.clear();
        borrowedDatePicker.setValue(null);
        dueDatePicker.setValue(null);
        returnDatePicker.setValue(LocalDate.now());
        statusLabel.setText("Not Checked");
        messageLabel.setText("");
        loadBorrowedBooks();
    }
}