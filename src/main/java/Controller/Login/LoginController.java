package Controller.Login;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;

import javafx.scene.shape.Ellipse;

import javafx.scene.paint.Color;
import javafx.scene.effect.GaussianBlur;

import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoginController {

    // =====================================
    // LOGIN COMPONENTS
    // =====================================

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;


    // =====================================
    // ANIMATION COMPONENTS
    // =====================================

    @FXML
    private StackPane libraryPane;

    @FXML
    private Label libraryTitle;

    @FXML
    private Pane petalLayer;


    // =====================================
    // FLOWER ANIMATION VARIABLES
    // =====================================

    private final List<Petal> petals = new ArrayList<>();

    private final Random random = new Random();

    private AnimationTimer petalAnimation;


    // =====================================
    // INITIALIZE ALL ANIMATIONS
    // =====================================

    @FXML
    public void initialize() {

        startImageAnimation();

        startTitleAnimation();

        createFlowerPetals();

        startFlowerAnimation();
    }


    // =====================================
    // LIBRARY IMAGE ZOOM ANIMATION
    // =====================================

    private void startImageAnimation() {

        if (libraryPane == null) {
            return;
        }

        ScaleTransition zoom = new ScaleTransition(
                Duration.seconds(12),
                libraryPane
        );

        zoom.setFromX(1.0);
        zoom.setFromY(1.0);

        zoom.setToX(1.06);
        zoom.setToY(1.06);

        zoom.setAutoReverse(true);

        zoom.setCycleCount(
                ScaleTransition.INDEFINITE
        );

        zoom.play();
    }


    // =====================================
    // LIBRARY TITLE ANIMATION
    // =====================================

    private void startTitleAnimation() {

        if (libraryTitle == null) {
            return;
        }

        FadeTransition fade = new FadeTransition(
                Duration.seconds(2),
                libraryTitle
        );

        fade.setFromValue(0);
        fade.setToValue(1);

        fade.play();


        TranslateTransition slide = new TranslateTransition(
                Duration.seconds(1.5),
                libraryTitle
        );

        slide.setFromX(-40);
        slide.setToX(0);

        slide.play();
    }


    // =====================================
    // CREATE PINK AND WHITE FLOWER PETALS
    // =====================================

    private void createFlowerPetals() {

        if (petalLayer == null) {
            return;
        }

        petalLayer.getChildren().clear();

        petals.clear();


        // FLOWER PETAL COLORS

        Color[] flowerColors = {

                Color.web("#FFB6D5"),
                Color.web("#FFC0CB"),
                Color.WHITE,
                Color.web("#FFE4EE"),
                Color.web("#F8A5C2")
        };


        // CREATE 35 FLOWER PETALS

        for (int i = 0; i < 35; i++) {

            Ellipse petal = new Ellipse();

            double width = 3 + random.nextDouble() * 5;

            double height = 2 + random.nextDouble() * 3;

            petal.setRadiusX(width);

            petal.setRadiusY(height);


            // RANDOM FLOWER COLORS

            petal.setFill(
                    flowerColors[
                            random.nextInt(flowerColors.length)
                            ]
            );


            // RANDOM OPACITY

            petal.setOpacity(
                    0.65 + random.nextDouble() * 0.35
            );


            // SOFT BLUR EFFECT

            petal.setEffect(
                    new GaussianBlur(0.5)
            );


            // CREATE PETAL OBJECT

            Petal flower = new Petal();

            flower.shape = petal;

            flower.x = random.nextDouble() * 500;

            flower.y = random.nextDouble() * 650;

            flower.speed = 25 + random.nextDouble() * 35;

            flower.drift = 10 + random.nextDouble() * 25;

            flower.phase = random.nextDouble() * Math.PI * 2;

            flower.rotationSpeed = 30 + random.nextDouble() * 80;


            // SET INITIAL POSITION

            petal.setLayoutX(flower.x);

            petal.setLayoutY(flower.y);


            // ADD PETAL TO LAYER

            petalLayer.getChildren().add(petal);

            petals.add(flower);
        }
    }


    // =====================================
    // FLOWER PETALS FALLING ANIMATION
    // =====================================

    private void startFlowerAnimation() {

        if (petalLayer == null) {
            return;
        }

        petalAnimation = new AnimationTimer() {

            private long lastTime = 0;

            @Override
            public void handle(long now) {

                if (lastTime == 0) {

                    lastTime = now;

                    return;
                }


                // CALCULATE TIME DIFFERENCE

                double deltaTime = Math.min(
                        (now - lastTime) / 1_000_000_000.0,
                        0.05
                );

                lastTime = now;


                for (Petal p : petals) {

                    // FALLING MOVEMENT

                    p.y += p.speed * deltaTime;


                    // NATURAL WIND MOVEMENT

                    p.x += Math.sin(
                            p.y * 0.02 + p.phase
                    ) * p.drift * deltaTime;


                    // ROTATION MOVEMENT

                    p.shape.setRotate(
                            p.shape.getRotate()
                                    + p.rotationSpeed * deltaTime
                    );


                    // RESTART PETAL FROM TOP

                    if (p.y > 660) {

                        p.y = -10;

                        p.x = random.nextDouble() * 500;
                    }


                    // KEEP PETALS INSIDE FRAME

                    if (p.x > 500) {

                        p.x = 0;
                    }

                    if (p.x < 0) {

                        p.x = 500;
                    }


                    // UPDATE PETAL POSITION

                    p.shape.setLayoutX(p.x);

                    p.shape.setLayoutY(p.y);
                }
            }
        };

        petalAnimation.start();
    }


    // =====================================
    // PETAL CLASS
    // =====================================

    private static class Petal {

        Ellipse shape;

        double x;

        double y;

        double speed;

        double drift;

        double phase;

        double rotationSpeed;
    }


    // =====================================
    // LOGIN FUNCTION
    // =====================================

    @FXML
    private void handleLogin() {

        String username = usernameField.getText().trim();

        String password = passwordField.getText();


        // CHECK EMPTY FIELDS

        if (username.isEmpty() || password.isEmpty()) {

            errorLabel.setText(
                    "Please enter username and password."
            );

            return;
        }


        // LOGIN VALIDATION

        if (username.equals("admin") && password.equals("1234")) {

            errorLabel.setText("");

            try {

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/Dashboard.fxml")
                );

                Scene dashboardScene = new Scene(loader.load());

                Stage stage = (Stage) usernameField
                        .getScene()
                        .getWindow();


                // STOP FLOWER ANIMATION

                if (petalAnimation != null) {

                    petalAnimation.stop();
                }


                // OPEN DASHBOARD

                stage.setTitle(
                        "Library Management System - Dashboard"
                );

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
                        "Unable to open Dashboard. Please check Dashboard.fxml."
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


    // =====================================
    // CLEAR FUNCTION
    // =====================================

    @FXML
    private void handleClear() {

        usernameField.clear();

        passwordField.clear();

        errorLabel.setText("");

        usernameField.requestFocus();
    }

}