package com.example.fianance;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.LineChart;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnalyticsController {

    private int accountId; // This will store the logged-in user's account ID

    @FXML
    private BarChart<String, Number> barChart; // Bar chart for transactions
    @FXML
    private PieChart pieChart; // Pie chart for transaction types
    @FXML
    private LineChart<String, Number> lineChart; // Line chart for transaction trends

    // Method to set the logged-in user's account ID
    public void setAccountId(int accountId) {
        this.accountId = accountId;
        loadTransactionData(); // Load the data when the account ID is set
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            // Load the Financial Dashboard FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fianance/FinanceDashboard.fxml"));
            Parent root = loader.load();

            // Get the controller for the FinanceDashboard
            FinanceDashboardController financeDashboardController = loader.getController();
            financeDashboardController.setLoggedInUserId(accountId); // Set the account ID here

            // Create a new scene with the loaded root
            Scene scene = new Scene(root, 800, 650);

            // Load the CSS and apply it to the scene
            String css = this.getClass().getResource("/com/example/fianance/Dashboard.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Get the current stage and set the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Financial Dashboard");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load Financial Dashboard.");
        }
    }

    // Load transaction data and populate the charts
    private void loadTransactionData() {
        String query = "SELECT transaction_type, SUM(amount) AS total FROM transaction WHERE account_id = ? GROUP BY transaction_type";
        String timeSeriesQuery = "SELECT DATE(created_at) AS date, SUM(amount) AS total FROM transaction WHERE account_id = ? GROUP BY DATE(created_at) ORDER BY DATE(created_at)";

        double totalIncome = 0;
        double totalExpenses = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, accountId); // Use the account ID
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String type = resultSet.getString("transaction_type");
                double total = resultSet.getDouble("total");

                // Update the pie chart
                pieChart.getData().add(new PieChart.Data(type, total));

                if ("credit".equalsIgnoreCase(type)) {
                    totalIncome += total;
                } else if ("debit".equalsIgnoreCase(type)) {
                    totalExpenses += total;
                }
            }

            // Update the bar chart
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Total Transactions");
            series.getData().add(new XYChart.Data<>("Income", totalIncome));
            series.getData().add(new XYChart.Data<>("Expenses", totalExpenses));
            barChart.getData().add(series);

            // Load time series data for the line chart
            try (PreparedStatement timeSeriesStatement = connection.prepareStatement(timeSeriesQuery)) {
                timeSeriesStatement.setInt(1, accountId);
                ResultSet timeSeriesResultSet = timeSeriesStatement.executeQuery();

                // Populate the line chart with the time series data
                XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
                lineSeries.setName("Transaction Trends");

                while (timeSeriesResultSet.next()) {
                    String date = timeSeriesResultSet.getString("date");
                    double amount = timeSeriesResultSet.getDouble("total");

                    // Add the date and total amount to the line chart
                    lineSeries.getData().add(new XYChart.Data<>(date, amount));
                }

                lineChart.getData().add(lineSeries); // Add the line series to the line chart
            }

        } catch (SQLException e) {
            showAlert("Error", "Unable to load transaction data: " + e.getMessage());
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
}
