package com.library.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;


    @FXML
    private void handleLogin() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Check empty fields
        if (username.isEmpty() || password.isEmpty()) {

            errorLabel.setText(
                    "Please enter username and password."
            );

            return;
        }

        // Login validation
        if (username.equals("admin") && password.equals("1234")) {

            errorLabel.setText("");

            try {

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/Dashboard.fxml")
                );

                Scene dashboardScene = new Scene(loader.load());

                Stage stage = (Stage) usernameField.getScene().getWindow();

                stage.setTitle("Library Management System - Dashboard");
                stage.setScene(dashboardScene);
                stage.setResizable(true);
                stage.show();

            } catch (Exception e) {

                e.printStackTrace();

                Alert alert = new Alert(
                        Alert.AlertType.ERROR
                );

                alert.setTitle("Error");
                alert.setHeaderText("Dashboard Error");
                alert.setContentText(
                        "Unable to open Dashboard."
                );

                alert.showAndWait();
            }

        } else {

            errorLabel.setText(
                    "Invalid username or password."
            );

            Alert alert = new Alert(
                    Alert.AlertType.ERROR
            );

            alert.setTitle("Login Error");
            alert.setHeaderText("Login Failed");
            alert.setContentText(
                    "Invalid username or password."
            );

            alert.showAndWait();
        }
    }


    @FXML
    private void handleClear() {

        usernameField.clear();
        passwordField.clear();

        errorLabel.setText("");

        usernameField.requestFocus();
    }
}