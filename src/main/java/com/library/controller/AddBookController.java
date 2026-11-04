package com.library.controller;

import dto.BookDTO;
import model.BookModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddBookController {
    @FXML private TextField bookIdField;
    @FXML private TextField bookTitleField;
    @FXML private TextField authorField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField publishedYearField;
    @FXML private TextField quantityField;
    @FXML private Label messageLabel;

    @FXML public void initialize() {
        categoryComboBox.getItems().addAll("Fiction","Non-Fiction","Science","Technology","Education","History","Biography","Children","Other");
    }

    @FXML private void handleAddBook() {
        String bookId = bookIdField.getText().trim();
        String title = bookTitleField.getText().trim();
        String author = authorField.getText().trim();
        String category = categoryComboBox.getValue();
        String yearStr = publishedYearField.getText().trim();
        String qtyStr = quantityField.getText().trim();

        if (bookId.isEmpty() || title.isEmpty() || author.isEmpty() || category == null || yearStr.isEmpty() || qtyStr.isEmpty()) {
            messageLabel.setText("Please fill in all fields."); return;
        }
        try {
            int qty = Integer.parseInt(qtyStr);
            BookDTO dto = new BookDTO(bookId, title, author, category, yearStr, qty);
            boolean isSaved = BookModel.saveBook(dto);
            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "Book Added Successfully!").showAndWait();
                handleClear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error: " + e.getMessage());
        }
    }
    @FXML private void handleClear() {
        bookIdField.clear(); bookTitleField.clear(); authorField.clear();
        categoryComboBox.setValue(null); publishedYearField.clear(); quantityField.clear();
        messageLabel.setText(""); bookIdField.requestFocus();
    }
}