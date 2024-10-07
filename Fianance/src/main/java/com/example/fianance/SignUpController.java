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

        
        if (registerUser(username, password)) {
            System.out.println("User registered successfully.");
           
            goToLoginView();
        } else {
        
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

            connection.setAutoCommit(false);

            // Insert the user into the users table
            userStatement.setString(1, username);
            userStatement.setString(2, password); // Consider hashing the password
            int userRowsAffected = userStatement.executeUpdate();

            accountStatement.setString(1, username);
            accountStatement.setDouble(2, 0.0); // Initial balance
            int accountRowsAffected = accountStatement.executeUpdate();

            if (userRowsAffected > 0 && accountRowsAffected > 0) {
                connection.commit();
                return true; // Registration successful
            } else {
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

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void goToLoginView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/fianance/login-view.fxml"));
            Parent loginRoot = fxmlLoader.load();

            Stage stage = (Stage) newUsernameField.getScene().getWindow();  // Get the current stage

            Scene scene = new Scene(loginRoot, 800, 600);

            scene.getStylesheets().add(getClass().getResource("/com/example/fianance/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
