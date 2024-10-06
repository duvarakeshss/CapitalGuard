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

        String userQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
        String accountQuery = "INSERT INTO account (username, balance) VALUES (?, ?)"; // Inserting into account with a default balance of 0.0

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement userStatement = connection.prepareStatement(userQuery);
             PreparedStatement accountStatement = connection.prepareStatement(accountQuery)) {

            // Disable auto-commit for transaction management
            connection.setAutoCommit(false);

            // Insert the user into the users table
            userStatement.setString(1, username);
            userStatement.setString(2, password); // Consider hashing the password
            int userRowsAffected = userStatement.executeUpdate();

            // Insert the new user into the account table with an initial balance of 0.0
            accountStatement.setString(1, username);
            accountStatement.setDouble(2, 0.0); // Initial balance
            int accountRowsAffected = accountStatement.executeUpdate();

            // If both inserts are successful, commit the transaction
            if (userRowsAffected > 0 && accountRowsAffected > 0) {
                connection.commit();
                return true; // Registration successful
            } else {
                // Rollback transaction if something goes wrong
                connection.rollback();
                return false;
            }

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
