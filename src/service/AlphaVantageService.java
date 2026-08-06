package service;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
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
 *   - Prices are cached per ticker for 60 seconds. Multiple bots requesting
 *     the same ticker within that window share one API call. Only successful
 *     prices are cached; failures are not, so the next call retries the API.
 *   - Cache access is synchronized — safe for the 3 concurrent BotTrader threads.
 *   - After any API failure (HTTP error, rate-limit, network exception) a 30-second
 *     failure cooldown is activated. During the cooldown ALL tickers skip new HTTP
 *     requests and return PRICE_UNAVAILABLE immediately, so bots fall back to the
 *     random price simulator instead of queuing up blocked or rate-limited calls.
 *     A fresh cached price always takes priority over the cooldown.
 */
public class AlphaVantageService {

    // Alpha Vantage base URL
    private static final String BASE_URL =
            "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s";

    // Sentinel value returned when the price cannot be fetched
    private static final double PRICE_UNAVAILABLE = -1.0;

    // HTTP request timeout (seconds) — keeps the trading loop responsive
    private static final int TIMEOUT_SECONDS = 5;

    // --- Price cache ---
    // How long a cached price is considered fresh (milliseconds)
    private static final long CACHE_TTL_MS = 60_000L; // 60 seconds

    // ticker -> last successfully fetched price
    // Guarded by the lock on this AlphaVantageService instance
    private final Map<String, Double> priceCache     = new HashMap<>();

    // ticker -> System.currentTimeMillis() when the price was fetched
    private final Map<String, Long>   cacheFetchTime = new HashMap<>();
    // --- end cache ---

    // --- Failure cooldown ---
    // How long to pause ALL new API calls after any failure (milliseconds)
    private static final long FAILURE_COOLDOWN_MS = 30_000L; // 30 seconds

    // Timestamp of the most recent API failure; 0 means no failure has occurred yet.
    // A single global cooldown covers all tickers: if the API is down or rate-limited
    // it is down for every symbol, so there is no point trying others either.
    private long lastFailureTime = 0L;
    // --- end failure cooldown ---

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
    }

    public synchronized double getCurrentPrice(String symbol) {

        // --- Cache check ---
        Long lastFetch = cacheFetchTime.get(symbol);
        if (lastFetch != null && (System.currentTimeMillis() - lastFetch) < CACHE_TTL_MS) {
            // Cache hit: price is still fresh, skip the HTTP call
            return priceCache.get(symbol);
        }
        // Cache miss or expired — check the failure cooldown before hitting the API.

        // --- Failure cooldown check ---
        if (lastFailureTime != 0L) {
            long msSinceFailure = System.currentTimeMillis() - lastFailureTime;
            if (msSinceFailure < FAILURE_COOLDOWN_MS) {
                // Still within cooldown window — skip the HTTP call silently.
                return PRICE_UNAVAILABLE;
            }
            // Cooldown has expired — reset and allow a new attempt
            lastFailureTime = 0L;
        }
        // --- end cooldown check ---

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
                lastFailureTime = System.currentTimeMillis(); // activate cooldown
                return PRICE_UNAVAILABLE;
            }

            double price = parsePrice(response.body(), symbol);

            if (price == PRICE_UNAVAILABLE) {
                lastFailureTime = System.currentTimeMillis(); // activate cooldown
                return PRICE_UNAVAILABLE;
            }

            // --- Cache store (only on success) ---
            priceCache.put(symbol, price);
            cacheFetchTime.put(symbol, System.currentTimeMillis());
            return price;

        } catch (InterruptedException e) {
            // Restore interrupted status and treat as unavailable
            Thread.currentThread().interrupt();
            lastFailureTime = System.currentTimeMillis(); // activate cooldown
            return PRICE_UNAVAILABLE;
        } catch (Exception e) {
            lastFailureTime = System.currentTimeMillis(); // activate cooldown
            return PRICE_UNAVAILABLE;
        }
    }

    private double parsePrice(String json, String symbol) {
        // Detect rate-limit or error messages from Alpha Vantage
        if (json.contains("\"Note\"") || json.contains("\"Information\"")
                || json.contains("\"Error Message\"")) {
            return PRICE_UNAVAILABLE;
        }

        // Look for the "05. price" key in the JSON string
        final String PRICE_KEY = "\"05. price\"";
        int keyIndex = json.indexOf(PRICE_KEY);
        if (keyIndex == -1) {
            return PRICE_UNAVAILABLE;
        }

        // Extract the value after the key: "05. price": "213.54"
        int colonIndex  = json.indexOf(':', keyIndex);
        int quoteOpen   = json.indexOf('"', colonIndex + 1);
        int quoteClose  = json.indexOf('"', quoteOpen  + 1);

        if (colonIndex == -1 || quoteOpen == -1 || quoteClose == -1) {
            return PRICE_UNAVAILABLE;
        }

        String priceStr = json.substring(quoteOpen + 1, quoteClose).trim();
        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                return PRICE_UNAVAILABLE;
            }
            return price;
        } catch (NumberFormatException e) {
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
