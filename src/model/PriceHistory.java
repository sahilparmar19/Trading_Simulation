package model;

import java.sql.Timestamp;

public class PriceHistory {
    private String ticker;
    private double price;
    private Timestamp recordedAt;

    public PriceHistory(String ticker, double price, Timestamp recordedAt) {
        this.ticker = ticker;
        this.price = price;
        this.recordedAt = recordedAt;
    }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Timestamp getRecordedAt() { return recordedAt; }
    public void setRecordedAt(Timestamp recordedAt) { this.recordedAt = recordedAt; }
}
