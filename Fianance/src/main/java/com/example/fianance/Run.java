package com.example.fianance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Run {
    public static void main(String[] args) {

        try (Connection conn = DB.connect();   // Assuming DB.connect() returns a valid Connection object
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {

            // Process the result set
            while (rs.next()) {
                // Assuming the products table has columns 'id', 'name', 'price'
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");

                // Print each row's data
                System.out.printf("Product ID: %d, Name: %s, Price: %.2f%n", id, name, price);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
