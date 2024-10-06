package com.example.fianance;

import java.math.BigDecimal;

public class StockData {
    private String symbol;
    private String name;
    private BigDecimal lastPrice;
    private BigDecimal change;
    private BigDecimal percentageChange;

    public StockData(String symbol, String name, BigDecimal lastPrice, BigDecimal change, BigDecimal percentageChange) {
        this.symbol = symbol;
        this.name = name;
        this.lastPrice = lastPrice;
        this.change = change;
        this.percentageChange = percentageChange;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public BigDecimal getChange() {
        return change;
    }

    public BigDecimal getPercentageChange() {
        return percentageChange;
    }
}
