package com.example.fianance;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/fianance/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            scene.getStylesheets().add(HelloApplication.class.getResource("/com/example/fianance/style.css").toExternalForm());
            stage.setTitle("Login Page");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading FXML or CSS file.");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}

