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
import javafx.scene.control.TextArea; 
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainPageController {

    private int loggedInUserId;
    private StockService stockService;  // StockService instance

    public void setLoggedInUserId(int userId) {
        this.loggedInUserId = userId;
        this.stockService = new StockService(); // Initialize StockService
    }

    @FXML
    private void handleStockAction() {
        try {
            String[] stockSymbols = {"AAPL", "NFLX", "ETSY", "KO", "BTC-USD"};
            List<StockData> stockDataList = new ArrayList<>();

            for (String stockSymbol : stockSymbols) {
                StockData stockData = scrapeStockData(stockSymbol);
                stockDataList.add(stockData);
                Thread.sleep(2000); // Sleep for 2 seconds to avoid getting blocked
            }

            // Convert stock data into CSV file
            String csvFilePath = "stocks_data.csv";
            convertToCSV(stockDataList, csvFilePath);
            showAlert("Stock Management", "Stock data scraped and saved to CSV.");

        } catch (IOException | InterruptedException e) {
            showAlert("Error", "Failed to scrape stock data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBestSellingStockAction(ActionEvent event) {
        try {
            String bestSellingStock = stockService.getBestSellingStock();
            if (!bestSellingStock.isEmpty()) {
                showAlert("Best Selling Stock", "The best selling stock is: " + bestSellingStock);
            } else {
                showAlert("Best Selling Stock", "No stocks found.");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to retrieve best selling stock: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private StockData scrapeStockData(String stockSymbol) throws IOException {
        String url = "https://finance.yahoo.com/quote/" + stockSymbol;

        // Use a user-agent to avoid being blocked by Yahoo Finance
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();

        // Debugging log to check if the document is retrieved
        System.out.println("Document fetched for " + stockSymbol + ": " + doc.title());

        // Selecting the elements from the page
        Element nameElement = doc.selectFirst("h1"); // Name of the stock
        Element priceElement = doc.selectFirst("fin-streamer[data-field='regularMarketPrice']");
        Element changeElement = doc.selectFirst("fin-streamer[data-field='regularMarketChange']");
        Element changePercentElement = doc.selectFirst("fin-streamer[data-field='regularMarketChangePercent']");

        // Parse the elements
        String name = nameElement != null ? nameElement.text() : "N/A";
        BigDecimal lastPrice = parseToBigDecimal(priceElement != null ? priceElement.text() : "0");
        BigDecimal change = parseToBigDecimal(changeElement != null ? changeElement.text() : "0");
        BigDecimal percentageChange = parseToBigDecimal(changePercentElement != null ? changePercentElement.text() : "0");

        return new StockData(stockSymbol, name, lastPrice, change, percentageChange);
    }

    private void convertToCSV(List<StockData> stockDataList, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write the CSV header
            writer.append("Symbol,Name,Last Price,Change,Percentage Change\n");

            // Write stock data
            for (StockData stockData : stockDataList) {
                writer.append(stockData.getSymbol())
                        .append(",")
                        .append(stockData.getName())
                        .append(",")
                        .append(stockData.getLastPrice().toString())
                        .append(",")
                        .append(stockData.getChange().toString())
                        .append(",")
                        .append(stockData.getPercentageChange().toString())
                        .append("\n");
            }

        } catch (IOException e) {
            showAlert("Error", "Failed to write CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private BigDecimal parseToBigDecimal(String value) {
        try {
            // Trim whitespace
            value = value.trim();

            // Log the value being parsed
            System.out.println("Parsing value: " + value); // Debugging log

            // Handle parentheses for negative numbers (e.g., (1000) -> -1000)
            if (value.contains("(") && value.contains(")")) {
                value = value.replace("(", "-").replace(")", "");
            }

            // Remove non-numeric characters like commas, percentage signs, and '+' signs
            value = value.replace(",", "").replace("%", "").replace("+", "");

            // Handle multiple negative signs (e.g., "--0.50" becomes "0.50")
            while (value.startsWith("-") && value.length() > 1) {
                value = value.substring(1);
            }

            // Handle empty strings after cleaning
            if (value.isEmpty()) {
                System.out.println("Parsed value is empty after cleaning. Returning 0."); // Debugging log
                return BigDecimal.ZERO; // Return 0 if the value is empty
            }

            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse value: " + value); // Log the problematic value
            e.printStackTrace();
            return BigDecimal.ZERO; // Return 0 in case of a parsing error
        }
    }




    @FXML
    private void handleStockManagement(ActionEvent event) {
        try {
            Path csvFilePath = Paths.get("stocks_data.csv");
            if (Files.exists(csvFilePath)) {
                String csvContent = Files.readString(csvFilePath);
                showAlert("Stock Management", "CSV Content:\n" + csvContent);
            } else {
                showAlert("Stock Management", "CSV file does not exist.");
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to load CSV file: " + e.getMessage());
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

    @FXML
    private void handleFinanceAction(ActionEvent event) {
        try {
            navigateToFinanceDashboard(event);
        } catch (IOException e) {
            showAlert("Error", "Failed to load the Finance Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
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
    @FXML
    private void handleViewBestStock(ActionEvent event) {
        try {
            String bestStockInfo = getBestStockFromCSV("stocks_data.csv");
            showBestStockWindow(bestStockInfo);
        } catch (IOException e) {
            showAlert("Error", "Failed to read CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getBestStockFromCSV(String csvFilePath) throws IOException {
        List<StockData> stockDataList = new ArrayList<>();

        // Read the CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            // Skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 5) {
                    String symbol = values[0];
                    String name = values[1];
                    BigDecimal lastPrice = new BigDecimal(values[2]);
                    BigDecimal change = new BigDecimal(values[3]);
                    BigDecimal percentageChange = new BigDecimal(values[4]);

                    stockDataList.add(new StockData(symbol, name, lastPrice, change, percentageChange));
                }
            }
        }

        // Determine the best stock based on the last price
        StockData bestStock = stockDataList.stream()
                .max(Comparator.comparing(StockData::getLastPrice))
                .orElse(null);

        return bestStock != null ? "Best Stock: " + bestStock.getName() + " (" + bestStock.getSymbol() + ") - Last Price: " + bestStock.getLastPrice() : "No stocks available.";
    }

    private void showBestStockWindow(String bestStockInfo) {
        Stage stage = new Stage();
        VBox vbox = new VBox(10);
        TextArea textArea = new TextArea(bestStockInfo);
        textArea.setEditable(false);
        vbox.getChildren().add(textArea);

        Scene scene = new Scene(vbox, 300, 200);
        stage.setTitle("Best Stock");
        stage.setScene(scene);
        stage.show();
    }
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
