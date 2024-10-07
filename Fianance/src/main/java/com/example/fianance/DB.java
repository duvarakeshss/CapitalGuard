package com.example.fianance;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DB {
    public static Connection connect() throws SQLException {
        try (InputStream input = DB.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.out.println("db.properties file not found in classpath!");
                throw new RuntimeException("Sorry, unable to find db.properties");
            } else {
                System.out.println("db.properties file loaded successfully.");
            }

            // Load properties file
            Properties prop = new Properties();
            prop.load(input);

            // Get connection properties
            String url = prop.getProperty("db.url");
            String username = prop.getProperty("db.username");
            String password = prop.getProperty("db.password");

            System.out.println("URL: " + url);
            System.out.println("Username: " + username);
        

            // Return a connection object
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace(); 
            throw new SQLException("Failed to load database connection details", e);
        }
    }
}
