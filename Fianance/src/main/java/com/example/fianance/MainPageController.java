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
            String url = "https://www.nseindia.com/market-data/live-equity-market";

            Document document = Jsoup.connect(url).get();

            StockPriceDAO stockPriceDAO = new StockPriceDAO();

            for (Element row : document.select("table tbody tr")) {
                String symbol = row.select("td.symbol").text();
                String openPrice = row.select("td.open").text();
                String highPrice = row.select("td.high").text();
                String lowPrice = row.select("td.low").text();
                String closePrice = row.select("td.prevClose").text();
                String ltp = row.select("td.ltp").text();
                String volume = row.select("td.volume").text();
                String changePercentage = row.select("td.changePercentage").text();

                stockPriceDAO.insertStockPrice(symbol, openPrice, highPrice, lowPrice, closePrice, ltp, volume, changePercentage);
            }

            showAlert("Stock Management", "Data scraped from NSE website and stored successfully.");
        } catch (IOException e) {
            showAlert("Error", "Failed to scrape data from NSE: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
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
