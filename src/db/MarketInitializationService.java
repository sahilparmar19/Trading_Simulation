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

    // 1.5 second delay between API requests (for testing)
    private static final long REQUEST_DELAY_MS = 1500;

    private static final StockMapping[] SEEDED_TICKERS = {
            new StockMapping("RELIANCE", "RELIANCE.BSE"),
            new StockMapping("TCS", "TCS.BSE"),
            new StockMapping("INFY", "INFY.BSE"),
            new StockMapping("HDFCBANK", "HDFCBANK.BSE"),
            new StockMapping("ICICIBANK", "ICICIBANK.BSE"),
            new StockMapping("SBIN", "SBIN.BSE"),
            new StockMapping("LT", "LT.BSE"),
            new StockMapping("ITC", "ITC.BSE"),
            new StockMapping("BHARTIARTL", "BHARTIARTL.BSE"),
            new StockMapping("ONGC", "ONGC.BSE")
    };

    public static void initialize() {

        System.out.println("[MarketInitialization] Starting Alpha Vantage market price initialization...");

        System.out.println("[MarketInitialization] Initializing prices for "
                + SEEDED_TICKERS.length + " stock(s).");

        int successCount = 0;
        int failureCount = 0;

        for (StockMapping stock : SEEDED_TICKERS) {

            OptionalDouble priceOpt =
                    AlphaVantageService.getCurrentPrice(stock.apiTicker);

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

                failureCount++;
            }

            // Wait 1.5 seconds before the next API request
            try {
                Thread.sleep(REQUEST_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[MarketInitialization] Initialization interrupted.");
                break;
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