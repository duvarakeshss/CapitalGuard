package com.example.fianance;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    public static String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public static String getDbUsername() {
        return properties.getProperty("db.username");
    }

    public static String getDbPassword() {
        return properties.getProperty("db.password");
    }

    static {
        try {
            InputStream var0 = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties");

            try {
                if (var0 == null) {
                    System.out.println("Sorry, unable to find db.properties");
                    System.exit(1);
                }

                properties.load(var0);
            } catch (Throwable var4) {
                if (var0 != null) {
                    try {
                        var0.close();
                    } catch (Throwable var3) {
                        var4.addSuppressed(var3);
                    }
                }

                throw var4;
            }

            if (var0 != null) {
                var0.close();
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }
}
