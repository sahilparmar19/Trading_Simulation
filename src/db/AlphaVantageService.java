package db;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.OptionalDouble;
import java.util.Properties;

/**
 * AlphaVantageService
 *
 * Fetches real-time stock prices from the Alpha Vantage GLOBAL_QUOTE endpoint.
 *
 * USAGE CONTRACT (enforced by architecture):
 *   - Called ONLY during application startup by MarketInitializationService.
 *   - NEVER called after the simulation has started.
 *   - NEVER called by BotTrader, MatchingEngine, GUI, or any other component.
 *
 * API key is read once from config.properties (key: alphavantage.api.key).
 * The HttpClient is shared and reused across all requests within a startup cycle.
 */
public class AlphaVantageService {

    // Alpha Vantage GLOBAL_QUOTE endpoint template
    private static final String BASE_URL =
            "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s";

    // HTTP request timeout — fail fast rather than block startup indefinitely
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    // The JSON key path: response["Global Quote"]["05. price"]
    private static final String KEY_GLOBAL_QUOTE = "Global Quote";
    private static final String KEY_PRICE        = "05. price";

    // Alpha Vantage returns these top-level keys when the request is rate-limited or invalid
    private static final String KEY_NOTE          = "Note";
    private static final String KEY_ERROR_MESSAGE = "Error Message";
    private static final String KEY_INFORMATION   = "Information";

    // API key loaded once from config.properties at class initialization
    private static final String API_KEY;

    // Shared HttpClient — thread-safe, reused for all startup requests
    private static final HttpClient HTTP_CLIENT;

    static {
        // Load the API key from config.properties (same file DatabaseManager uses)
        String key = "";
        Properties props = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("config.properties");
            props.load(fis);
            key = props.getProperty("alphavantage.api.key", "").trim();
            if (key.isEmpty()) {
                System.err.println("[AlphaVantage] Warning: 'alphavantage.api.key' is missing or empty in config.properties.");
            }
        } catch (IOException e) {
            System.err.println("[AlphaVantage] Could not load config.properties: " + e.getMessage());
        } finally {
            if (fis != null) {
                try { fis.close(); } catch (IOException e) {
                    System.err.println("[AlphaVantage] Error closing config stream: " + e.getMessage());
                }
            }
        }
        API_KEY = key;

        // Build a shared HttpClient with a connect timeout
        HTTP_CLIENT = HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .build();
    }

    /**
     * Fetches the current (real-time) price for the given stock symbol from Alpha Vantage.
     *
     * Returns OptionalDouble.empty() if any of the following occur:
     *   - API key is missing
     *   - Network error or timeout
     *   - HTTP error (non-200 status)
     *   - Rate-limit response  ("Note"  key present in JSON)
     *   - Error response       ("Error Message" or "Information" key present in JSON)
     *   - Missing "Global Quote" object in response
     *   - Empty "Global Quote" object (e.g., unrecognised symbol)
     *   - "05. price" field is absent or non-numeric
     *   - Returned price is <= 0 (invalid / pre-market data edge case)
     *
     * @param symbol The stock ticker symbol (e.g. "AAPL", "TCS.BSE").
     * @return OptionalDouble containing the price on success, or empty on any failure.
     */
    public static OptionalDouble getCurrentPrice(String symbol) {
        // Guard: refuse to make a request if no API key is configured
        if (API_KEY == null || API_KEY.isEmpty()) {
            System.err.println("[AlphaVantage] Skipping " + symbol + ": API key not configured.");
            return OptionalDouble.empty();
        }

        String url = String.format(BASE_URL, symbol, API_KEY);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(REQUEST_TIMEOUT)
                    .GET()
                    .build();

            HttpResponse<String> response =
                    HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            // Non-200 status: treat as network failure
            if (response.statusCode() != 200) {
                System.err.println("[AlphaVantage] HTTP " + response.statusCode()
                        + " for symbol: " + symbol);
                return OptionalDouble.empty();
            }

            return parsePrice(symbol, response.body());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupt flag
            System.err.println("[AlphaVantage] Request interrupted for " + symbol);
            return OptionalDouble.empty();
        } catch (Exception e) {
            // Covers IOException, URISyntaxException, and any other runtime issue
            System.err.println("[AlphaVantage] Request failed for " + symbol + ": " + e.getMessage());
            return OptionalDouble.empty();
        }
    }

    /**
     * Parses the Alpha Vantage JSON response and extracts the "05. price" value.
     *
     * Explicitly detects and rejects:
     *   - "Note"          key -> rate-limit or premium endpoint restriction
     *   - "Error Message" key -> invalid symbol or malformed request
     *   - "Information"   key -> API key invalid / usage exceeded
     *   - Missing or empty "Global Quote" -> unrecognised or unlisted symbol
     *
     * @param symbol The symbol (used for logging only).
     * @param body   The raw JSON response body string.
     * @return OptionalDouble with the price, or empty on any parse failure.
     */
    private static OptionalDouble parsePrice(String symbol, String body) {
        try {
            JSONObject root = new JSONObject(body);

            // --- Detect Alpha Vantage error/rate-limit responses ---

            if (root.has(KEY_NOTE)) {
                // Rate limit or premium-only endpoint restriction
                System.err.println("[AlphaVantage] Rate limit hit for " + symbol
                        + ": " + root.getString(KEY_NOTE).trim());
                return OptionalDouble.empty();
            }

            if (root.has(KEY_ERROR_MESSAGE)) {
                // Invalid symbol or bad request parameters
                System.err.println("[AlphaVantage] API error for " + symbol
                        + ": " + root.getString(KEY_ERROR_MESSAGE).trim());
                return OptionalDouble.empty();
            }

            if (root.has(KEY_INFORMATION)) {
                // API key exceeded daily limit or invalid key
                System.err.println("[AlphaVantage] API information message for " + symbol
                        + ": " + root.getString(KEY_INFORMATION).trim());
                return OptionalDouble.empty();
            }

            // --- Validate "Global Quote" object ---

            if (!root.has(KEY_GLOBAL_QUOTE)) {
                System.err.println("[AlphaVantage] Missing 'Global Quote' in response for " + symbol);
                return OptionalDouble.empty();
            }

            JSONObject quote = root.getJSONObject(KEY_GLOBAL_QUOTE);

            // An empty Global Quote object means the symbol was not found
            if (quote.isEmpty()) {
                System.err.println("[AlphaVantage] Empty 'Global Quote' for " + symbol
                        + " -- symbol may not be supported by Alpha Vantage.");
                return OptionalDouble.empty();
            }

            // --- Extract price ---

            if (!quote.has(KEY_PRICE)) {
                System.err.println("[AlphaVantage] Missing '" + KEY_PRICE + "' field for " + symbol);
                return OptionalDouble.empty();
            }

            double price = Double.parseDouble(quote.getString(KEY_PRICE));

            // Sanity check: price must be positive (Stock.validatePrice() enforces this too)
            if (price <= 0.0) {
                System.err.println("[AlphaVantage] Invalid price (" + price + ") received for " + symbol);
                return OptionalDouble.empty();
            }

            return OptionalDouble.of(price);

        } catch (Exception e) {
            // Covers JSONException, NumberFormatException, etc.
            System.err.println("[AlphaVantage] JSON parse error for " + symbol + ": " + e.getMessage());
            return OptionalDouble.empty();
        }
    }
}
