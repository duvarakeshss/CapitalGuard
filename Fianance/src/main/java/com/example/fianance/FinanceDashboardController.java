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
import javafx.application.Platform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FinanceDashboardController {

    // Store the current user's ID
    private int loggedInUserId;

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

    // Method to set the logged-in user's ID (called after user logs in)
    public void setLoggedInUserId(int userId) {
        this.loggedInUserId = userId;
    }

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

            // Fetch current balance and validate transaction
            if (!validateTransaction(transactionType, amount)) {
                return; // If validation fails, exit
            }

            // Insert the transaction and update the balance
            executeTransaction(transactionType, amount, description);

            // Clear input fields after successful transaction
            clearFields();

            // Refresh data after adding the transaction
            handleViewTransactions();
            showAlert("Success", "Transaction added successfully!");

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid amount entered.");
        }
    }

    private boolean validateTransaction(String transactionType, double amount) {
        String fetchBalanceQuery = "SELECT balance FROM account WHERE account_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement fetchBalanceStmt = connection.prepareStatement(fetchBalanceQuery)) {

            fetchBalanceStmt.setInt(1, loggedInUserId);  // Use loggedInUserId
            ResultSet rs = fetchBalanceStmt.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");

                // Check for debit: If insufficient balance, stop the transaction
                if ("debit".equalsIgnoreCase(transactionType) && currentBalance < amount) {
                    showAlert("Error", "Insufficient balance for this debit transaction.");
                    return false;
                }
            } else {
                showAlert("Error", "Account not found.");
                return false;
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to validate transaction: " + e.getMessage());
            return false;
        }
        return true;
    }

    private void executeTransaction(String transactionType, double amount, String description) {
        double currentBalance = 0.0;
        String fetchBalanceQuery = "SELECT balance FROM account WHERE account_id = ?";
        String updateBalanceQuery = "UPDATE account SET balance = ? WHERE account_id = ?";
        String insertTransactionQuery = "INSERT INTO transaction (account_id, amount, transaction_type, description) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement fetchBalanceStmt = connection.prepareStatement(fetchBalanceQuery);
             PreparedStatement updateBalanceStmt = connection.prepareStatement(updateBalanceQuery);
             PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {

            // Fetch current balance
            fetchBalanceStmt.setInt(1, loggedInUserId);
            ResultSet rs = fetchBalanceStmt.executeQuery();
            if (rs.next()) {
                currentBalance = rs.getDouble("balance");
            } else {
                throw new SQLException("Account not found for user ID: " + loggedInUserId);
            }

            // Calculate new balance based on transaction type
            if ("debit".equalsIgnoreCase(transactionType)) {
                if (currentBalance < amount) {
                    throw new SQLException("Insufficient funds for debit transaction.");
                }
                currentBalance -= amount;
            } else if ("credit".equalsIgnoreCase(transactionType)) {
                currentBalance += amount;
            } else {
                throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
            }

            // Update account balance
            updateBalanceStmt.setDouble(1, currentBalance);
            updateBalanceStmt.setInt(2, loggedInUserId);
            int rowsUpdated = updateBalanceStmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Failed to update balance for user ID: " + loggedInUserId);
            }

            // Insert transaction record
            insertTransactionStmt.setInt(1, loggedInUserId);
            insertTransactionStmt.setDouble(2, amount);
            insertTransactionStmt.setString(3, transactionType);
            insertTransactionStmt.setString(4, description);
            insertTransactionStmt.executeUpdate();

        } catch (SQLException e) {
            showAlert("Error", "Transaction failed: " + e.getMessage());
            e.printStackTrace(); // For debugging
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }


    @FXML
    private void handleViewTransactions() {
        ObservableList<ObservableList<String>> transactions = FXCollections.observableArrayList();
        String query = "SELECT transaction_id, amount, created_at, description FROM transaction WHERE account_id = ? ORDER BY created_at DESC LIMIT 30";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, loggedInUserId);  // Filter transactions for the logged-in user
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ObservableList<String> transactionData = FXCollections.observableArrayList();
                transactionData.add(String.valueOf(resultSet.getInt("transaction_id")));
                transactionData.add(String.valueOf(resultSet.getDouble("amount")));
                transactionData.add(resultSet.getString("created_at"));
                transactionData.add(resultSet.getString("description"));
                transactions.add(transactionData);
            }

            Platform.runLater(() -> transactionsTable.setItems(transactions)); // Ensure updates happen on the JavaFX Application Thread

        } catch (SQLException e) {
            showAlert("Error", "Unable to load transactions: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowAnalytics() {
        String query = "SELECT SUM(amount) AS total, transaction_type FROM transaction WHERE account_id = ? GROUP BY transaction_type";

        double totalIncome = 0;
        double totalExpenses = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, loggedInUserId); // Filter analytics by the logged-in user
            ResultSet resultSet = statement.executeQuery();

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
        String query = "SELECT balance FROM account WHERE account_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, loggedInUserId); // Fetch balance for the logged-in user
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

    private void clearFields() {
        amountField.clear();
        transactionTypeChoiceBox.getSelectionModel().clearSelection();
        descriptionField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
