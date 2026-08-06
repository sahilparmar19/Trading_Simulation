package service;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

/**
 * AlphaVantageService
 *
 * Fetches live stock prices from the Alpha Vantage GLOBAL_QUOTE endpoint.
 *
 * API endpoint used:
 *   https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol={SYMBOL}&apikey={KEY}
 *
 * Example response (relevant excerpt):
 *   { "Global Quote": { "01. symbol": "AAPL", "05. price": "213.54" } }
 *
 * Design decisions:
 *   - Uses java.net.http.HttpClient (built-in since Java 11, no new JAR needed).
 *   - Parses price with simple String operations; avoids adding a JSON library.
 *   - Returns -1.0 on ANY failure so the caller can fall back to random prices.
 *   - The API key is loaded once from config.properties at construction time.
 *   - A 5-second request timeout prevents the trading loop from hanging.
 */
public class AlphaVantageService {

    // Alpha Vantage base URL
    private static final String BASE_URL =
            "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s";

    // Sentinel value returned when the price cannot be fetched
    private static final double PRICE_UNAVAILABLE = -1.0;

    // HTTP request timeout (seconds) — keeps the trading loop responsive
    private static final int TIMEOUT_SECONDS = 5;

    private final String apiKey;

    // Reuse a single HttpClient instance (thread-safe, recommended pattern)
    private final HttpClient httpClient;

    /**
     * Constructs the service and reads the API key from config.properties.
     * If the key cannot be loaded, "demo" is used as a safe fallback
     * (only works for AAPL on Alpha Vantage's demo endpoint).
     */
    public AlphaVantageService() {
        this.apiKey   = loadApiKey();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
        System.out.println("[AlphaVantageService] Initialized. API key loaded: "
                + (apiKey.equals("demo") ? "DEMO (limited to AAPL)" : "custom key"));
    }

    /**
     * Returns the latest price for the given stock symbol from Alpha Vantage.
     *
     * @param symbol  The ticker symbol (e.g. "AAPL", "RELIANCE.BSE")
     * @return        The current price as a double, or PRICE_UNAVAILABLE (-1.0)
     *                if the API call fails, times out, or returns invalid data.
     */
    public double getCurrentPrice(String symbol) {
        try {
            // Build the request URL
            String url = String.format(BASE_URL, symbol, apiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .GET()
                    .build();

            // Send the request synchronously (called from a background BotTrader thread)
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("[AlphaVantageService] HTTP " + response.statusCode()
                        + " for symbol: " + symbol);
                return PRICE_UNAVAILABLE;
            }

            return parsePrice(response.body(), symbol);

        } catch (InterruptedException e) {
            // Restore interrupted status and treat as unavailable
            Thread.currentThread().interrupt();
            System.err.println("[AlphaVantageService] Request interrupted for: " + symbol);
            return PRICE_UNAVAILABLE;
        } catch (Exception e) {
            // Catches IOException, URISyntaxException, etc.
            System.err.println("[AlphaVantageService] Error fetching price for "
                    + symbol + ": " + e.getMessage());
            return PRICE_UNAVAILABLE;
        }
    }

    /**
     * Parses the "05. price" field from the Alpha Vantage JSON response.
     *
     * We deliberately avoid adding a JSON library and instead rely on the
     * well-known, stable structure of the Alpha Vantage GLOBAL_QUOTE response.
     *
     * Example JSON:
     *   {"Global Quote": {"01. symbol": "AAPL", "05. price": "213.54", ...}}
     *
     * Rate-limit / empty response example:
     *   {"Note": "Thank you for using Alpha Vantage! ..."}
     *   {"Information": "..."}
     *
     * @return parsed price, or PRICE_UNAVAILABLE on any parsing failure.
     */
    private double parsePrice(String json, String symbol) {
        // Detect rate-limit or error messages from Alpha Vantage
        if (json.contains("\"Note\"") || json.contains("\"Information\"")
                || json.contains("\"Error Message\"")) {
            System.err.println("[AlphaVantageService] API limit or error response for "
                    + symbol + ". Falling back to random price.");
            return PRICE_UNAVAILABLE;
        }

        // Look for the "05. price" key in the JSON string
        final String PRICE_KEY = "\"05. price\"";
        int keyIndex = json.indexOf(PRICE_KEY);
        if (keyIndex == -1) {
            System.err.println("[AlphaVantageService] Price key not found in response for: "
                    + symbol);
            return PRICE_UNAVAILABLE;
        }

        // Extract the value after the key: "05. price": "213.54"
        //                                               ^-start  ^-end
        int colonIndex  = json.indexOf(':', keyIndex);
        int quoteOpen   = json.indexOf('"', colonIndex + 1);
        int quoteClose  = json.indexOf('"', quoteOpen  + 1);

        if (colonIndex == -1 || quoteOpen == -1 || quoteClose == -1) {
            System.err.println("[AlphaVantageService] Malformed price value in response for: "
                    + symbol);
            return PRICE_UNAVAILABLE;
        }

        String priceStr = json.substring(quoteOpen + 1, quoteClose).trim();
        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                System.err.println("[AlphaVantageService] Non-positive price received for: "
                        + symbol);
                return PRICE_UNAVAILABLE;
            }
            return price;
        } catch (NumberFormatException e) {
            System.err.println("[AlphaVantageService] Cannot parse price '" + priceStr
                    + "' for: " + symbol);
            return PRICE_UNAVAILABLE;
        }
    }

    /**
     * Loads the Alpha Vantage API key from config.properties.
     * Falls back to "demo" if the file cannot be read or the key is missing.
     */
    private String loadApiKey() {
        Properties props = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("config.properties");
            props.load(fis);
            String key = props.getProperty("alphavantage.apikey", "demo").trim();
            return key.isEmpty() ? "demo" : key;
        } catch (IOException e) {
            System.err.println("[AlphaVantageService] Could not read config.properties: "
                    + e.getMessage() + ". Using demo key.");
            return "demo";
        } finally {
            if (fis != null) {
                try { fis.close(); } catch (IOException ignored) {}
            }
        }
    }
}
