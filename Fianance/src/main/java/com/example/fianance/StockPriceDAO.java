package com.example.fianance;
import com.example.fianance.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class  StockPriceDAO {
    public void insertStockPrice(String symbol, String price) {
        String sql = "INSERT INTO stock_prices(symbol, price) VALUES(?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.out.println("Connection is null, cannot proceed with insertion.");
                return;
            }
            System.out.println("Inserting data: Symbol = " + symbol + ", Price = " + price);
            pstmt.setString(1, symbol);
            pstmt.setString(2, price);
            conn.setAutoCommit(false);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Data inserted successfully for " + symbol);
                conn.commit(); // Commit the transaction
            } else {
                System.out.println("No rows affected.");
                conn.rollback(); // Rollback in case of failure
            }

//            pstmt.executeUpdate();
//            conn.commit();
//            System.out.println("Data inserted successfully for " + symbol);

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();        }
    }
}
