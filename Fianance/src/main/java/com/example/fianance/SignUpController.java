package com.example.fianance;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.control.Alert; // Import the Alert class

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpController {

    @FXML
    private TextField newUsernameField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void handleSignUpAction() {
        String username = newUsernameField.getText();
        String password = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate input
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            System.out.println("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        // Save user to the database
        if (registerUser(username, password)) {
            System.out.println("User registered successfully.");
            // Redirect to the login screen
            goToLoginView();
        } else {
            // Show alert if registration failed
            showAlert("Registration Failed", "Username may already exist.");
        }
    }

    private boolean registerUser(String username, String password) {
        // Check if the username already exists
        if (isUsernameTaken(username)) {
            return false; // Username already exists
        }

        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password); // Consider hashing the password
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // Return true if user was added
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isUsernameTaken(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            // If a record is found, the username is already taken
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void goToLoginView() {
        try {
            // Load the login view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/fianance/login-view.fxml"));
            Parent loginRoot = fxmlLoader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) newUsernameField.getScene().getWindow();  // Get the current stage

            // Create the scene with specified dimensions
            Scene scene = new Scene(loginRoot, 800, 600);

            // Include the stylesheet
            scene.getStylesheets().add(getClass().getResource("/com/example/fianance/style.css").toExternalForm());

            // Set the scene and title
            stage.setScene(scene);
            stage.setTitle("Login");
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
