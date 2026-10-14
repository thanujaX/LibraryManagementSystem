package com.library.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddMemberController {

    @FXML
    private TextField memberIdField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextArea addressArea;

    @FXML
    private Label messageLabel;


    @FXML
    private void handleRegisterMember() {

        String memberId =
                memberIdField.getText().trim();

        String fullName =
                fullNameField.getText().trim();

        String email =
                emailField.getText().trim();

        String phone =
                phoneField.getText().trim();

        String address =
                addressArea.getText().trim();


        // CHECK EMPTY FIELDS

        if (memberId.isEmpty()
                || fullName.isEmpty()
                || email.isEmpty()
                || phone.isEmpty()
                || address.isEmpty()) {

            messageLabel.setText(
                    "Please fill in all fields."
            );

            return;
        }


        // EMAIL VALIDATION

        if (!email.contains("@")
                || !email.contains(".")) {

            messageLabel.setText(
                    "Please enter a valid email address."
            );

            return;
        }


        // PHONE VALIDATION

        if (!phone.matches("\\d{10}")) {

            messageLabel.setText(
                    "Phone number must contain 10 digits."
            );

            return;
        }


        // SUCCESS ALERT

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.setTitle("Member Registered");

        alert.setHeaderText(
                "Member Registered Successfully"
        );

        alert.setContentText(
                "The member has been registered successfully."
        );

        alert.showAndWait();


        messageLabel.setText(
                "Member registered successfully!"
        );


        handleClear();
    }


    @FXML
    private void handleClear() {

        memberIdField.clear();

        fullNameField.clear();

        emailField.clear();

        phoneField.clear();

        addressArea.clear();

        messageLabel.setText("");

        memberIdField.requestFocus();
    }
}