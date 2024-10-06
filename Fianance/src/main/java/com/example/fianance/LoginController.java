package com.example.fianance;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleSignInAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check if the user exists in the database
        if (authenticateUser(username, password)) {
            //showAlert("Login Successful", "Welcome, " + username + "!");
            // Redirect to the main application page
            goToMainPage();
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    private boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password); // Consider using hashed password comparison
            ResultSet resultSet = statement.executeQuery();

            // If a record is found, the user is authenticated
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void goToMainPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/fianance/Main-Page.fxml"));
            Parent mainPageRoot = fxmlLoader.load();

            Scene scene = new Scene(mainPageRoot, 800, 600);

            // Apply stylesheet
            scene.getStylesheets().add(getClass().getResource("/com/example/fianance/Main-Page.css").toExternalForm());

            Stage stage = (Stage) usernameField.getScene().getWindow();  // Get the current stage
            stage.setScene(scene);                                       // Set new scene size
            stage.setTitle("Main Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an error message if loading fails
        }
    }


    @FXML
    private void handleSignUpAction() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/fianance/signup.fxml"));
            Parent signUpRoot = fxmlLoader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();  // Get the current stage
            stage.setScene(new Scene(signUpRoot, 800, 600));
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an error message if loading fails
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
