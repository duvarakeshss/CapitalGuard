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

    // Variable to store the account ID
    private int accountId;
    private int loggedInUserId; // Variable to store logged-in user ID


    public void setLoggedInUserId(int userId) {
        this.loggedInUserId = userId; // Assign the user ID to the variable
    }

    public int getLoggedInUserId() {
        return loggedInUserId; // Getter to access the user ID
    }
    @FXML
    private void handleSignInAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (authenticateUser(username, password)) {
            // Redirect to the main application page
            goToMainPage();
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    private boolean authenticateUser(String username, String password) {
        String query = "SELECT a.account_id FROM users u " +
                "JOIN account a ON u.username = a.username " +
                "WHERE u.username = ? AND u.password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password); // Consider using hashed password comparison
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                accountId = resultSet.getInt("account_id"); // Set accountId from the result
                return true; // User is authenticated
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false; // User not authenticated
    }

    private void goToMainPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/fianance/Main-Page.fxml"));
            Parent mainPageRoot = fxmlLoader.load();

            MainPageController mainPageController = fxmlLoader.getController();
            mainPageController.setLoggedInUserId(accountId); // Pass the account ID to the main page

            Scene scene = new Scene(mainPageRoot, 800, 600);

            scene.getStylesheets().add(getClass().getResource("/com/example/fianance/Main-Page.css").toExternalForm());

            Stage stage = (Stage) usernameField.getScene().getWindow();  // Get the current stage
            stage.setScene(scene);                                       // Set new scene size
            stage.setTitle("Main Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
