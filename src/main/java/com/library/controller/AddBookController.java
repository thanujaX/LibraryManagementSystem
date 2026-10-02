package com.library.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddBookController {

    @FXML
    private TextField bookIdField;

    @FXML
    private TextField bookTitleField;

    @FXML
    private TextField authorField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextField publishedYearField;

    @FXML
    private TextField quantityField;

    @FXML
    private Label messageLabel;


    @FXML
    public void initialize() {

        categoryComboBox.getItems().addAll(
                "Fiction",
                "Non-Fiction",
                "Science",
                "Technology",
                "Education",
                "History",
                "Biography",
                "Children",
                "Other"
        );
    }


    @FXML
    private void handleAddBook() {

        String bookId = bookIdField.getText().trim();
        String title = bookTitleField.getText().trim();
        String author = authorField.getText().trim();
        String category = categoryComboBox.getValue();
        String year = publishedYearField.getText().trim();
        String quantity = quantityField.getText().trim();


        // VALIDATION

        if (bookId.isEmpty()
                || title.isEmpty()
                || author.isEmpty()
                || category == null
                || year.isEmpty()
                || quantity.isEmpty()) {

            messageLabel.setText(
                    "Please fill in all fields."
            );

            return;
        }


        // YEAR VALIDATION

        try {

            int publishedYear = Integer.parseInt(year);

            if (publishedYear < 1000
                    || publishedYear > 2100) {

                messageLabel.setText(
                        "Please enter a valid published year."
                );

                return;
            }

        } catch (NumberFormatException e) {

            messageLabel.setText(
                    "Published year must be a number."
            );

            return;
        }


        // QUANTITY VALIDATION

        try {

            int bookQuantity = Integer.parseInt(quantity);

            if (bookQuantity <= 0) {

                messageLabel.setText(
                        "Quantity must be greater than 0."
                );

                return;
            }

        } catch (NumberFormatException e) {

            messageLabel.setText(
                    "Quantity must be a number."
            );

            return;
        }


        // SUCCESS MESSAGE

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.setTitle("Book Added");
        alert.setHeaderText("Book Added Successfully");

        alert.setContentText(
                "The book has been added successfully."
        );

        alert.showAndWait();


        messageLabel.setText(
                "Book added successfully!"
        );


        handleClear();
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