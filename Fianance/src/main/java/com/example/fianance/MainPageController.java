package com.example.fianance;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPageController {

    // Assuming this will store the current user's ID for passing to other controllers
    private int loggedInUserId;

    // Method to set the logged-in user's ID, called after login
    public void setLoggedInUserId(int userId) {
        this.loggedInUserId = userId;
    }

    @FXML
    private void handleStockAction() {
        showAlert("Stock Management", "Navigating to Stock Management...");
        // You can add navigation to a new page or scene here for stock management
        // navigateToStockManagement(); // Call your method when implemented
    }

    @FXML
    private void handleFinanceAction(ActionEvent event) {
        try {
            navigateToFinanceDashboard(event);
        } catch (IOException e) {
            showAlert("Error", "Failed to load the Finance Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Show alert for user information or error messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation method to Finance Dashboard
    private void navigateToFinanceDashboard(ActionEvent event) throws IOException {
        // Load the Finance Dashboard FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fianance/FinanceDashboard.fxml"));
        Parent root = loader.load();

        // Get the FinanceDashboardController from the loaded FXML
        FinanceDashboardController controller = loader.getController();

        // Pass the logged-in user ID to the FinanceDashboardController
        controller.setLoggedInUserId(loggedInUserId);

        // Create a new scene with the loaded root
        Scene scene = new Scene(root, 800, 650);

        // Load the CSS and apply it to the scene
        String css = this.getClass().getResource("/com/example/fianance/Dashboard.css").toExternalForm();
        scene.getStylesheets().add(css);

        // Get the current stage and set the new scene
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Finance Dashboard");
        stage.show();
    }

    // If you later implement Stock Management, use a similar pattern for navigation
    private void navigateToStockManagement(ActionEvent event) throws IOException {
        // Load the Stock Management FXML file (Example)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fianance/StockManagement.fxml"));
        Parent root = loader.load();

        // Get the current stage and set the new scene
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 800, 650);

        // Load the CSS and apply it to the scene
        String css = this.getClass().getResource("/com/example/fianance/StockManagement.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setScene(scene);
        stage.setTitle("Stock Management");
        stage.show();
    }
}
