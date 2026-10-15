package com.library.controller;

import com.library.Util.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddBookController {

    @FXML private TextField bookIdField;
    @FXML private TextField bookTitleField;
    @FXML private TextField authorField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField publishedYearField;
    @FXML private TextField quantityField;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        categoryComboBox.getItems().addAll(
                "Fiction", "Non-Fiction", "Science", "Technology",
                "Education", "History", "Biography", "Children", "Other"
        );
    }

    @FXML
    private void handleAddBook() {
        String bookId = bookIdField.getText().trim();
        String title = bookTitleField.getText().trim();
        String author = authorField.getText().trim();
        String category = categoryComboBox.getValue();
        String yearStr = publishedYearField.getText().trim();
        String qtyStr = quantityField.getText().trim();

        if (bookId.isEmpty() || title.isEmpty() || author.isEmpty() || category == null || yearStr.isEmpty() || qtyStr.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }
        int publishedYear, bookQuantity;
        try {
            publishedYear = Integer.parseInt(yearStr);
            if (publishedYear < 1000 || publishedYear > 2100) {
                messageLabel.setText("Please enter a valid published year."); return;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Published year must be a number."); return;
        }
        try {
            bookQuantity = Integer.parseInt(qtyStr);
            if (bookQuantity <= 0) {
                messageLabel.setText("Quantity must be greater than 0."); return;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Quantity must be a number."); return;
        }

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            // UPDATED QUERY - with created_at and updated_at
            String sql = "INSERT INTO book (isbn, title, author, category, published_year, quantity, created_at, updated_at) VALUES (?,?,?,?,?,?, NOW(), NOW())";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, bookId);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setString(4, category);
            ps.setInt(5, publishedYear);
            ps.setInt(6, bookQuantity);

            ps.executeUpdate();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Book Added");
            alert.setHeaderText("Book Added Successfully");
            alert.setContentText("The book has been added successfully to Database.");
            alert.showAndWait();

            messageLabel.setText("Book added successfully!");
            handleClear();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        bookIdField.clear();
        bookTitleField.clear();
        authorField.clear();
        categoryComboBox.setValue(null);
        publishedYearField.clear();
        quantityField.clear();
        messageLabel.setText("");
        bookIdField.requestFocus();
    }
}