package com.example.fianance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StockPriceDAO {

    public void insertStockPrice(String symbol, String openPrice, String highPrice, String lowPrice,
                                 String closePrice, String ltp, String volume, String changePercentage) {
        String query = "INSERT INTO stock_prices (symbol, open_price, high_price, low_price, close_price, ltp, volume, change_percentage) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, symbol);
            stmt.setBigDecimal(2, parseBigDecimal(openPrice));
            stmt.setBigDecimal(3, parseBigDecimal(highPrice));
            stmt.setBigDecimal(4, parseBigDecimal(lowPrice));
            stmt.setBigDecimal(5, parseBigDecimal(closePrice));
            stmt.setBigDecimal(6, parseBigDecimal(ltp));
            stmt.setLong(7, parseLong(volume));
            stmt.setBigDecimal(8, parseBigDecimal(changePercentage));

            stmt.executeUpdate();
            System.out.println("Inserted stock price for: " + symbol);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private java.math.BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return java.math.BigDecimal.ZERO;
        }
        return new java.math.BigDecimal(value.replaceAll(",", ""));
    }

    private long parseLong(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        return Long.parseLong(value.replaceAll(",", ""));
    }
}
