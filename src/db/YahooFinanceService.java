package db;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.OptionalDouble;

/**
 * YahooFinanceService
 *
 * Fetches real‑time stock prices from Yahoo Finance public endpoint.
 *
 * CONTRACT:
 *   - Called ONLY during application startup via MarketInitializationService.
 *   - Returns OptionalDouble.empty() on any failure.
 *   - No API key required.
 */
public class YahooFinanceService {

    // Yahoo Finance v8 chart endpoint – no API key required.
    private static final String BASE_URL = "https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=1d&range=1d";

    // Request timeout (10 seconds).
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    // Shared HttpClient – thread‑safe, reused for all startup calls.
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();

    /**
     * Retrieves the latest market price for the given Yahoo Finance symbol.
     *
     * @param yahooSymbol e.g. "RELIANCE.NS"
     * @return OptionalDouble containing the price, or empty if any error occurs.
     */
    public static OptionalDouble getCurrentPrice(String yahooSymbol) {
        String url = String.format(BASE_URL, yahooSymbol);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .build();
        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.err.println("[YahooFinance] HTTP " + response.statusCode() + " for symbol: " + yahooSymbol);
                return OptionalDouble.empty();
            }
            return parsePrice(yahooSymbol, response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[YahooFinance] Request interrupted for " + yahooSymbol);
            return OptionalDouble.empty();
        } catch (Exception e) {
            System.err.println("[YahooFinance] Request failed for " + yahooSymbol + ": " + e.getMessage());
            return OptionalDouble.empty();
        }
    }

    /** Parses the JSON payload returned by Yahoo Finance v8 chart endpoint and extracts regularMarketPrice. */
    private static OptionalDouble parsePrice(String yahooSymbol, String body) {
        try {
            JSONObject root = new JSONObject(body);
            if (!root.has("chart")) {
                System.err.println("[YahooFinance] Missing 'chart' key in response for " + yahooSymbol);
                return OptionalDouble.empty();
            }
            JSONObject chart = root.getJSONObject("chart");
            if (!chart.has("result") || chart.isNull("result")) {
                System.err.println("[YahooFinance] Missing 'result' array for " + yahooSymbol);
                return OptionalDouble.empty();
            }
            JSONArray result = chart.getJSONArray("result");
            if (result.isEmpty()) {
                System.err.println("[YahooFinance] Empty 'result' array for " + yahooSymbol);
                return OptionalDouble.empty();
            }
            JSONObject firstResult = result.getJSONObject(0);
            if (!firstResult.has("meta")) {
                System.err.println("[YahooFinance] Missing 'meta' object for " + yahooSymbol);
                return OptionalDouble.empty();
            }
            JSONObject meta = firstResult.getJSONObject("meta");

            double price = -1.0;
            if (meta.has("regularMarketPrice") && !meta.isNull("regularMarketPrice")) {
                price = meta.getDouble("regularMarketPrice");
            } else if (meta.has("chartPreviousClose") && !meta.isNull("chartPreviousClose")) {
                price = meta.getDouble("chartPreviousClose");
            }

            if (price <= 0.0) {
                System.err.println("[YahooFinance] Invalid price (" + price + ") for " + yahooSymbol);
                return OptionalDouble.empty();
            }
            return OptionalDouble.of(price);
        } catch (Exception e) {
            System.err.println("[YahooFinance] JSON parse error for " + yahooSymbol + ": " + e.getMessage());
            return OptionalDouble.empty();
        }
    }
}
