package model;

import java.sql.Timestamp;

public class PriceHistory {
    private String ticker;
    private double price;
    private Timestamp recordedAt;

    public PriceHistory(String ticker, double price, Timestamp recordedAt) {
        this.ticker = ticker;
        setPrice(price);
        this.recordedAt = recordedAt;
    }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        this.price = price;
    }

    public Timestamp getRecordedAt() { return recordedAt; }

    @Override
    public String toString() {
        return "PriceHistory{" +
                "ticker='" + ticker + '\'' +
                ", price=" + price +
                ", recordedAt=" + recordedAt +
                '}';
    }
}
