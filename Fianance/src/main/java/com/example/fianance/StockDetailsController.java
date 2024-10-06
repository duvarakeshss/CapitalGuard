package com.example.fianance;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.util.List;

public class StockDetailsController {

    @FXML
    private TableView<StockData> stockTable;
    @FXML
    private TableColumn<StockData, String> symbolColumn;
    @FXML
    private TableColumn<StockData, String> nameColumn;
    @FXML
    private TableColumn<StockData, BigDecimal> priceColumn;
    @FXML
    private TableColumn<StockData, BigDecimal> changeColumn;
    @FXML
    private TableColumn<StockData, BigDecimal> percentageChangeColumn;

    public void initialize() {
        // Initialize the table columns to match StockData fields
        symbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("lastPrice"));
        changeColumn.setCellValueFactory(new PropertyValueFactory<>("change"));
        percentageChangeColumn.setCellValueFactory(new PropertyValueFactory<>("percentageChange"));
    }

    public void setStockDataList(List<StockData> stockDataList) {
        // Add the stock data to the table
        stockTable.getItems().setAll(stockDataList);
    }
}
