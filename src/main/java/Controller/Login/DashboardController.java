package Controller.Login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;

public class DashboardController {

    @FXML
    private Label totalBooksLabel;

    @FXML
    private Label totalMembersLabel;

    @FXML
    private Label borrowedBooksLabel;

    @FXML
    private Label overdueBooksLabel;


    @FXML
    private void initialize() {

        totalBooksLabel.setText("0");
        totalMembersLabel.setText("0");
        borrowedBooksLabel.setText("0");
        overdueBooksLabel.setText("0");
    }


    @FXML
    private void handleDashboard() {
        // Already on Dashboard
    }


    @FXML
    private void handleAddBook() {

        openPage(
                "/fxml/AddBook.fxml",
                "Add Book"
        );
    }


    @FXML
    private void handleAddMember() {

        openPage(
                "/fxml/AddMember.fxml",
                "Add Member"
        );
    }


    @FXML
    private void handleManageMembers() {

        openPage(
                "/fxml/ManageMembers.fxml",
                "Manage Members"
        );
    }


    @FXML
    private void handleIssueBook() {

        openPage(
                "/fxml/IssueBook.fxml",
                "Issue Book"
        );
    }


    @FXML
    private void handleReturnBook() {

        openPage(
                "/fxml/ReturnBook.fxml",
                "Return Book"
        );
    }


    @FXML
    private void handleBorrowingHistory() {

        openPage(
                "/fxml/BorrowingHistory.fxml",
                "Borrowing History"
        );
    }


    // ================= OPEN PAGE =================

    private void openPage(
            String fxmlFile,
            String pageTitle
    ) {

        try {

            URL fxmlUrl =
                    getClass().getResource(fxmlFile);



            if (fxmlUrl == null) {

                Alert alert =
                        new Alert(Alert.AlertType.ERROR);

                alert.setTitle(
                        "Navigation Error"
                );

                alert.setHeaderText(
                        "FXML File Not Found"
                );

                alert.setContentText(
                        "Cannot find:\n\n"
                                + fxmlFile
                                + "\n\n"
                                + "Check that the file is located in:\n"
                                + "src/main/resources"
                                + fxmlFile
                );

                alert.showAndWait();

                return;
            }


            FXMLLoader loader =
                    new FXMLLoader(fxmlUrl);


            Parent root =
                    loader.load();


            Scene scene =
                    new Scene(root);


            Stage stage =
                    new Stage();


            stage.setTitle(
                    "Library Management System - "
                            + pageTitle
            );

            stage.setScene(scene);

            stage.setResizable(true);

            stage.show();


        } catch (Exception e) {

            e.printStackTrace();


            Alert alert =
                    new Alert(
                            Alert.AlertType.ERROR
                    );

            alert.setTitle(
                    "Navigation Error"
            );

            alert.setHeaderText(
                    "Unable to Open Page"
            );

            alert.setContentText(
                    "FXML: "
                            + fxmlFile
                            + "\n\n"
                            + "Error:\n"
                            + e.getMessage()
            );

            alert.showAndWait();
        }
    }


    // ================= LOGOUT =================

    @FXML
    private void handleLogout() {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Logout"
        );

        confirmation.setHeaderText(
                "Logout Confirmation"
        );

        confirmation.setContentText(
                "Are you sure you want to logout?"
        );


        confirmation.showAndWait()
                .ifPresent(response -> {

                    if (response == ButtonType.OK) {

                        openLoginPage();
                    }
                });
    }


    // ================= LOGIN PAGE =================

    private void openLoginPage() {

        try {

            URL loginUrl =
                    getClass().getResource(
                            "/fxml/Login.fxml"
                    );


            if (loginUrl == null) {

                Alert alert =
                        new Alert(
                                Alert.AlertType.ERROR
                        );

                alert.setTitle(
                        "Logout Error"
                );

                alert.setHeaderText(
                        "Login.fxml Not Found"
                );

                alert.setContentText(
                        "Please check:\n"
                                + "src/main/resources/fxml/Login.fxml"
                );

                alert.showAndWait();

                return;
            }


            FXMLLoader loader =
                    new FXMLLoader(loginUrl);


            Parent root =
                    loader.load();


            Scene scene =
                    new Scene(root);


            Stage currentStage =
                    (Stage) totalBooksLabel
                            .getScene()
                            .getWindow();


            currentStage.setScene(scene);

            currentStage.setTitle(
                    "Library Management System - Login"
            );

            currentStage.setResizable(false);

            currentStage.show();


        } catch (Exception e) {

            e.printStackTrace();


            Alert error =
                    new Alert(
                            Alert.AlertType.ERROR
                    );

            error.setTitle(
                    "Logout Error"
            );

            error.setHeaderText(
                    "Unable to open Login page"
            );

            error.setContentText(
                    e.getMessage()
            );

            error.showAndWait();
        }
    }
}