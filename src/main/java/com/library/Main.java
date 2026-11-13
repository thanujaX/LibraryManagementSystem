package com.library;

import Util.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        System.out.println("=================================");
        System.out.println("STEP 1: JavaFX Application Started");
        System.out.println("=================================");

        // Initialize Hibernate
        try {

            System.out.println("STEP 2: Starting Hibernate...");

            HibernateUtil.getSessionFactory();

            System.out.println("STEP 3: HIBERNATE CONNECTED SUCCESSFULLY!");

        } catch (Throwable e) {

            System.err.println("HIBERNATE INITIALIZATION FAILED!");
            e.printStackTrace();

        }

        // Load Login Page
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Login.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        System.out.println("STEP 4: Login Page Opened Successfully");
    }

    @Override
    public void stop() {

        System.out.println("Closing Hibernate...");

        HibernateUtil.shutdown();

        System.out.println("Hibernate Closed!");
    }

    public static void main(String[] args) {

        launch(args);

    }
}