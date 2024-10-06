package com.example.fianance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FinanceDashboardController {

    @FXML
    private TextField amountField;

    @FXML
    private TextField descriptionField;

    @FXML
    private ChoiceBox<String> transactionTypeChoiceBox;

    @FXML
    private TableView<ObservableList<String>> transactionsTable;

    @FXML
    private TableColumn<ObservableList<String>, String> transactionIdCol;

    @FXML
    private TableColumn<ObservableList<String>, String> amountCol;

    @FXML
    private TableColumn<ObservableList<String>, String> dateCol;

    @FXML
    private TableColumn<ObservableList<String>, String> descriptionCol;

    @FXML
    public void initialize() {
        // Initialize the choice box with "credit" and "debit"
        transactionTypeChoiceBox.setItems(FXCollections.observableArrayList("credit", "debit"));

        // Initialize columns for TableView
        transactionIdCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(0)));
        amountCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(1)));
        dateCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(2)));
        descriptionCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().get(3)));

        // Load initial data
        handleViewTransactions(); // Load last 30 transactions
    }

    @FXML
    private void handleAddTransaction() {
        // Get values from fields
        String amountText = amountField.getText();
        String transactionType = transactionTypeChoiceBox.getValue();
        String description = descriptionField.getText();

        // Validate input
        if (amountText.isEmpty() || transactionType == null || description.isEmpty()) {
            showAlert("Error", "Please fill all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            // Fetch current account balance
            double currentBalance = 0.0;
            String fetchBalanceQuery = "SELECT balance FROM account WHERE account_id = ?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement fetchBalanceStmt = connection.prepareStatement(fetchBalanceQuery)) {

                fetchBalanceStmt.setInt(1, 1);  // Assuming account_id = 1 for now
                ResultSet rs = fetchBalanceStmt.executeQuery();
                if (rs.next()) {
                    currentBalance = rs.getDouble("balance");
                } else {
                    showAlert("Error", "Account not found.");
                    return;
                }
            }

            // Check for debit: If insufficient balance, stop the transaction
            if ("debit".equalsIgnoreCase(transactionType)) {
                if (currentBalance < amount) {
                    showAlert("Error", "Insufficient balance for this debit transaction.");
                    return;
                } else {
                    currentBalance -= amount;  // Deduct the amount for debit
                }
            } else if ("credit".equalsIgnoreCase(transactionType)) {
                currentBalance += amount;  // Add the amount for credit
            }

            // Update the account balance
            String updateBalanceQuery = "UPDATE account SET balance = ? WHERE account_id = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement updateBalanceStmt = connection.prepareStatement(updateBalanceQuery)) {
                updateBalanceStmt.setDouble(1, currentBalance);
                updateBalanceStmt.setInt(2, 1); // Assuming account_id = 1 for now
                updateBalanceStmt.executeUpdate();
            }

            // Insert the transaction into the transaction table
            String insertTransactionQuery = "INSERT INTO transaction (account_id, amount, transaction_type, description) VALUES (?, ?, ?, ?)";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {
                insertTransactionStmt.setInt(1, 1);  // Assuming account_id = 1 for now
                insertTransactionStmt.setDouble(2, amount);
                insertTransactionStmt.setString(3, transactionType);
                insertTransactionStmt.setString(4, description);
                insertTransactionStmt.executeUpdate();
            }

            // Clear input fields
            amountField.clear();
            transactionTypeChoiceBox.getSelectionModel().clearSelection();
            descriptionField.clear();

            // Refresh data after adding the transaction
            handleViewTransactions();
            showAlert("Success", "Transaction added successfully!");

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid amount entered.");
        } catch (SQLException e) {
            showAlert("Error", "Failed to add transaction: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewTransactions() {
        ObservableList<ObservableList<String>> transactions = FXCollections.observableArrayList();
        String query = "SELECT transaction_id, amount, created_at, description FROM transaction ORDER BY created_at DESC LIMIT 30";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ObservableList<String> transactionData = FXCollections.observableArrayList();
                transactionData.add(String.valueOf(resultSet.getInt("transaction_id")));
                transactionData.add(String.valueOf(resultSet.getDouble("amount")));
                transactionData.add(resultSet.getString("created_at"));
                transactionData.add(resultSet.getString("description"));
                transactions.add(transactionData);
            }
        } catch (SQLException e) {
            showAlert("Error", "Unable to load transactions: " + e.getMessage());
        }

        transactionsTable.setItems(transactions); // Set the table items
    }

    @FXML
    private void handleShowAnalytics() {
        // Example method: Show a simple alert with basic analytics
        String query = "SELECT SUM(amount) AS total, transaction_type FROM transaction GROUP BY transaction_type";

        double totalIncome = 0;
        double totalExpenses = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String type = resultSet.getString("transaction_type");
                double total = resultSet.getDouble("total");
                if ("credit".equalsIgnoreCase(type)) {
                    totalIncome += total;
                } else if ("debit".equalsIgnoreCase(type)) {
                    totalExpenses += total;
                }
            }

            showAlert("Analytics", "Total Income: " + totalIncome + "\nTotal Expenses: " + totalExpenses);

        } catch (SQLException e) {
            showAlert("Error", "Unable to load analytics: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowBalance() {
        // Fetch the current balance from the account table
        String query = "SELECT balance FROM account WHERE account_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, 1); // Assuming account_id = 1
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                double currentBalance = resultSet.getDouble("balance");
                showAlert("Account Balance", "Your current balance is: " + currentBalance);
            } else {
                showAlert("Error", "Account not found.");
            }

        } catch (SQLException e) {
            showAlert("Error", "Unable to fetch balance: " + e.getMessage());
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
