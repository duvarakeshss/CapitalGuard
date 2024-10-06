package com.example.fianance;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NseScraper {
    public static void main(String[] args) {
        try {
            String url = "https://www.nseindia.com/market-data/live-equity-market";

            Document document = Jsoup.connect(url).get();

            StockPriceDAO dao = new StockPriceDAO();

            for (Element row : document.select("table tbody tr")) {
                String symbol = row.select("td.symbol").text();
                String price = row.select("td.price").text();

                // For now, print scraped data
                System.out.println("Symbol: " + symbol + " | Price: " + price);

                // Insert into the database
                dao.insertStockPrice(symbol, price);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
