package com.example.fianance;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class StockService {

    public String getBestSellingStock() {
        List<String> stockSymbols = Arrays.asList("AAPL", "GOOGL", "AMZN", "MSFT", "TSLA");
        double highestVolume = 0.0;
        String bestStock = "";

        for (String symbol : stockSymbols) {
            try {
                // Scrape stock data from Yahoo Finance
                String url = "https://finance.yahoo.com/quote/" + symbol + "/history?p=" + symbol;
                Document document = Jsoup.connect(url).get();
                Elements rows = document.select("table[data-test='historical-prices'] tbody tr");

                for (Element row : rows) {
                    Elements cols = row.select("td");
                    if (cols.size() >= 7) {
                        // Extract volume data
                        String volumeString = cols.get(6).text().replace(",", "");
                        double volume = Double.parseDouble(volumeString);

                        // Find the stock with the highest volume
                        if (volume > highestVolume) {
                            highestVolume = volume;
                            bestStock = symbol;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bestStock;
    }
}
