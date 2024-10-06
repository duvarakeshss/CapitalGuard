package com.example.fianance;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;

public class StockManagementController {

//    @FXML
//    private TableView<StockData> stockTable;
//
//    @FXML
//    private TableColumn<StockData, String> symbolColumn;
//
//    @FXML
//    private TableColumn<StockData, BigDecimal> priceColumn;
//
//    @FXML
//    private TableColumn<StockData, BigDecimal> changeColumn;
//
//    @FXML
//    private TableColumn<StockData, BigDecimal> changePercentColumn;
//
//    @FXML
//    private TableColumn<StockData, Long> volumeColumn;
//
//    @FXML
//    public void initialize() {
//        // Link columns to StockData fields
//        symbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
//        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
//        changeColumn.setCellValueFactory(new PropertyValueFactory<>("change"));
//        changePercentColumn.setCellValueFactory(new PropertyValueFactory<>("changePercent"));
//        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));
//
//        // Load the initial data
//        loadData();
//    }
//
//    private void loadData() {
//        // Example: Load stock data into the table
//        stockTable.getItems().add(new StockData("AAPL", BigDecimal.valueOf(150.00), BigDecimal.valueOf(2.00), BigDecimal.valueOf(1.50), 100000));
//        stockTable.getItems().add(new StockData("GOOGL", BigDecimal.valueOf(2800.00), BigDecimal.valueOf(10.00), BigDecimal.valueOf(0.36), 120000));
//        stockTable.getItems().add(new StockData("AMZN", BigDecimal.valueOf(3400.00), BigDecimal.valueOf(5.00), BigDecimal.valueOf(0.15), 90000));
//        // Add more data as needed
//    }
}
