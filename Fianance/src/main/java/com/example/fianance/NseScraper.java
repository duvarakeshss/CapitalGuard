package com.example.fianance;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;

public class NseScraper {
    public static void main(String[] args) {
        try {
            String url = "https://www.nseindia.com/market-data/live-equity-market";
            Document document = Jsoup.connect(url).get();

            StockPriceDAO dao = new StockPriceDAO();

            for (Element row : document.select("table tbody tr")) {
                String symbol = row.select("td.symbol").text();
                String openPrice = row.select("td.open").text();
                String highPrice = row.select("td.high").text();
                String lowPrice = row.select("td.low").text();
                String closePrice = row.select("td.prevClose").text();
                String ltp = row.select("td.ltp").text();
                String volume = row.select("td.volume").text();
                String changePercentage = row.select("td.changePercentage").text();

                System.out.println("Symbol: " + symbol + " | Price: " + ltp);

                // Call the method with all the required parameters
                dao.insertStockPrice(symbol, openPrice, highPrice, lowPrice, closePrice, ltp, volume, changePercentage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
