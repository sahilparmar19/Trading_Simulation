package db;

import java.util.OptionalDouble;

public class MarketInitializationService {

    private static class StockMapping {
        final String dbTicker;
        final String apiTicker;

        StockMapping(String dbTicker, String apiTicker) {
            this.dbTicker = dbTicker;
            this.apiTicker = apiTicker;
        }
    }


    private static final StockMapping[] SEEDED_TICKERS = {
            new StockMapping("RELIANCE", "RELIANCE.NS"),
            new StockMapping("TCS", "TCS.NS"),
            new StockMapping("INFY", "INFY.NS"),
            new StockMapping("HDFCBANK", "HDFCBANK.NS"),
            new StockMapping("ICICIBANK", "ICICIBANK.NS"),
            new StockMapping("SBIN", "SBIN.NS"),
            new StockMapping("WIPRO", "WIPRO.NS"),
            new StockMapping("TECHM", "TECHM.NS"),
            new StockMapping("NTPC", "NTPC.NS"),
            new StockMapping("ONGC", "ONGC.NS")
    };

    public static void initialize() {

        System.out.println("[MarketInitialization] Starting Yahoo Finance market price initialization...");

        System.out.println("[MarketInitialization] Initializing prices for "
                + SEEDED_TICKERS.length + " stock(s).");

        int successCount = 0;
        int failureCount = 0;

        for (StockMapping stock : SEEDED_TICKERS) {

            OptionalDouble priceOpt =
                    YahooFinanceService.getCurrentPrice(stock.apiTicker);

            if (priceOpt.isPresent()) {

                double price = priceOpt.getAsDouble();

                boolean updated =
                        DatabaseManager.initializeStockPrice(stock.dbTicker, price);

                if (updated) {

                    System.out.printf(
                            "[MarketInitialization] Initialized %-12s (API: %s) -> %.2f%n",
                            stock.dbTicker,
                            stock.apiTicker,
                            price);

                    successCount++;

                } else {

                    System.err.println(
                            "[MarketInitialization] DB write failed for "
                                    + stock.dbTicker
                                    + ". Using existing database value.");

                    failureCount++;
                }

            } else {

                System.err.println(
                        "[MarketInitialization] Failed to initialize "
                                + stock.dbTicker
                                + ". Using existing database value.");

            }
        }

        System.out.println(
                "[MarketInitialization] Initialization complete. Success: "
                        + successCount
                        + ", Failed: "
                        + failureCount
                        + ".");
    }
}