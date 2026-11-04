package com.library.controller;

import dto.ReturnDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.ReturnBookModel;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ReturnBookController {

    @FXML private ComboBox<String> borrowedBookComboBox;
    @FXML private TextField memberField;
    @FXML private TextField bookField;
    @FXML private DatePicker borrowedDatePicker;
    @FXML private DatePicker dueDatePicker;
    @FXML private DatePicker returnDatePicker;
    @FXML private Label statusLabel;
    @FXML private Label messageLabel;

    private Map<String, Integer> issueMap = new HashMap<>();
    private Map<Integer, String> isbnMap = new HashMap<>();
    private final ReturnBookModel returnBookModel = new ReturnBookModel();

    @FXML
    public void initialize() {
        returnDatePicker.setValue(LocalDate.now());
        statusLabel.setText("Not Checked");
        loadBorrowedBooks();
        borrowedBookComboBox.setOnAction(event -> {
            if (borrowedBookComboBox.getValue() != null) loadBookDetails();
        });
        returnDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> checkOverdueStatus());
    }

    private void loadBorrowedBooks() {
        ObservableList<String> list = FXCollections.observableArrayList();
        issueMap.clear();
        isbnMap.clear();
        try {
            var issuedList = returnBookModel.getAllBorrowedBooksForCombo();
            for (var row : issuedList) {
                int issueId = row.getIssueId();
                String display = issueId + " - " + row.getTitle() + " (" + row.getMemberName() + ")";
                list.add(display);
                issueMap.put(display, issueId);
                isbnMap.put(issueId, row.getIsbn());
            }
            borrowedBookComboBox.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    private void loadBookDetails() {
        String selected = borrowedBookComboBox.getValue();
        if (selected == null) return;
        int issueId = issueMap.get(selected);
        try {
            var details = returnBookModel.getBorrowedDetailsById(issueId);
            if (details != null) {
                memberField.setText(details.getMemberId() + " - " + details.getMemberName());
                bookField.setText(details.getIsbn() + " - " + details.getTitle());
                borrowedDatePicker.setValue(details.getIssueDate());
                dueDatePicker.setValue(details.getDueDate());
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
        statusLabel.setText(returnDate.isAfter(dueDate) ? "OVERDUE" : "ON TIME");
    }

    @FXML
    private void handleReturnBook() {
        if (borrowedBookComboBox.getValue() == null) {
            messageLabel.setText("Please select a borrowed book.");
            return;
        }
        if (returnDatePicker.getValue() == null) {
            messageLabel.setText("Please select the return date.");
            return;
        }
        if (borrowedDatePicker.getValue() != null && returnDatePicker.getValue().isBefore(borrowedDatePicker.getValue())) {
            messageLabel.setText("Return date cannot be before borrowed date.");
            return;
        }

        checkOverdueStatus();
        String selected = borrowedBookComboBox.getValue();
        int issueId = issueMap.get(selected);
        String isbn = isbnMap.get(issueId);
        LocalDate returnDate = returnDatePicker.getValue();
        String status = returnDate.isAfter(dueDatePicker.getValue()) ? "OVERDUE" : "RETURNED";

        try {
            ReturnDTO dto = new ReturnDTO(issueId, returnDate, status);
            boolean isReturned = returnBookModel.returnBook(dto);
            if (isReturned) {
                returnBookModel.increaseBookQuantity(isbn);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Book Returned");
                alert.setHeaderText("Book Returned Successfully");
                alert.setContentText("Book: " + bookField.getText() + "\nStatus: " + status);
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