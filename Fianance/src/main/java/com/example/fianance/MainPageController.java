package com.example.fianance;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class MainPageController {

    private int loggedInUserId;

    public void setLoggedInUserId(int userId) {
        this.loggedInUserId = userId;
    }

    @FXML
    private void handleStockAction() {
        try {
            String stockSymbol = "AAPL"; // Example: Apple Inc.
            String stockData = scrapeStockData(stockSymbol);
            showAlert("Stock Management", "Data scraped from Yahoo Finance:\n" + stockData);
        } catch (IOException e) {
            showAlert("Error", "Failed to scrape stock data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String scrapeStockData(String stockSymbol) throws IOException {
        // URL to the Yahoo Finance page for the given stock symbol
        String url = "https://finance.yahoo.com/quote/" + stockSymbol;

        // Connect to the website and parse the HTML
        Document doc = Jsoup.connect(url).get();

        // Select the elements containing stock price and other relevant information
        Element priceElement = doc.selectFirst("fin-streamer[data-field='regularMarketPrice']");
        Element changeElement = doc.selectFirst("fin-streamer[data-field='regularMarketChange']");
        Element changePercentElement = doc.selectFirst("fin-streamer[data-field='regularMarketChangePercent']");

        // Extracting the text
        String price = priceElement != null ? priceElement.text() : "N/A";
        String change = changeElement != null ? changeElement.text() : "N/A";
        String changePercent = changePercentElement != null ? changePercentElement.text() : "N/A";

        // Returning the scraped data as a formatted string
        return String.format("Symbol: %s\nPrice: %s\nChange: %s\nChange Percentage: %s",
                stockSymbol, price, change, changePercent);
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToFinanceDashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fianance/FinanceDashboard.fxml"));
        Parent root = loader.load();

        FinanceDashboardController controller = loader.getController();
        controller.setLoggedInUserId(loggedInUserId);

        Scene scene = new Scene(root, 800, 650);
        String css = this.getClass().getResource("/com/example/fianance/Dashboard.css").toExternalForm();
        scene.getStylesheets().add(css);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Finance Dashboard");
        stage.show();
    }

    // Additional navigation method for stock management if needed
    private void navigateToStockManagement(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fianance/StockManagement.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 800, 650);
        String css = this.getClass().getResource("/com/example/fianance/StockManagement.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setScene(scene);
        stage.setTitle("Stock Management");
        stage.show();
    }
}