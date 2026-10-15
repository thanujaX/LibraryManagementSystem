package com.library.controller;

import com.library.Util.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;

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

        String memberId = memberIdField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();


        if (memberId.isEmpty() || fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        // EMAIL VALIDATION
        if (!email.contains("@") || !email.contains(".")) {
            messageLabel.setText("Please enter a valid email address.");
            return;
        }

        // PHONE VALIDATION
        if (!phone.matches("\\d{10}")) {
            messageLabel.setText("Phone number must contain 10 digits.");
            return;
        }


        try {
            Connection conn = DBConnection.getInstance().getConnection();

            String sql = "INSERT INTO member (member_id, full_name, email, phone, address, created_at, updated_at) VALUES (?,?,?,?,?, NOW(), NOW())";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, memberId);
            ps.setString(2, fullName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, address);

            ps.executeUpdate();

            // SUCCESS ALERT
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Member Registered");
            alert.setHeaderText("Member Registered Successfully");
            alert.setContentText("The member has been registered successfully to Database.");
            alert.showAndWait();

            messageLabel.setText("Member registered successfully!");
            handleClear();

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error: " + e.getMessage());
        }
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