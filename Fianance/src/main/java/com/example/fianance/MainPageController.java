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

    @FXML
    private void handleStockAction() {
        showAlert("Stock Management", "Navigating to Stock Management...");
        // You can add navigation to a new page or scene here for stock management
    }

    @FXML
    private void handleFinanceAction(ActionEvent event) {
        // Navigate to FinanceDashboard
        try {
            navigateToFinanceDashboard(event);
        } catch (IOException e) {
            showAlert("Error", "Failed to load the Finance Dashboard: " + e.getMessage());
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

    private void navigateToFinanceDashboard(ActionEvent event) throws IOException {
        // Load the Finance Dashboard FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fianance/FinanceDashboard.fxml"));
        Parent root = loader.load();

        // Get the current stage (window) using the event source
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Create a new scene with the loaded root
        Scene scene = new Scene(root,800,600);

        // Load the Dashboard.css and apply it to the scene
        String css = this.getClass().getResource("/com/example/fianance/Dashboard.css").toExternalForm();
        scene.getStylesheets().add(css);

        // Set the scene and show the stage
        stage.setScene(scene);
        stage.setTitle("Finance Dashboard");
        stage.show();
    }
}
