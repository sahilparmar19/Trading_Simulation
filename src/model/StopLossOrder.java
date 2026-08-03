package model;

import java.sql.Timestamp;

public class StopLossOrder {
    private int slId;
    private int userId;
    private String ticker;
    private int quantity;
    private double stopPrice;
    private Timestamp createdAt;
    private StopLossStatus status;

    public StopLossOrder(int slId, int userId, String ticker, int quantity, double stopPrice, Timestamp createdAt, StopLossStatus status) {
        this.slId = slId;
        this.userId = userId;
        this.ticker = ticker;
        setQuantity(quantity);
        setStopPrice(stopPrice);
        this.createdAt = createdAt;
        this.status = status;
    }

    public int getSlId() { return slId; }
    public void setSlId(int slId) { this.slId = slId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        this.quantity = quantity;
    }

    public double getStopPrice() { return stopPrice; }
    public void setStopPrice(double stopPrice) {
        if (stopPrice < 0) {
            throw new IllegalArgumentException("Stop price must be greater than or equal to zero.");
        }
        this.stopPrice = stopPrice;
    }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public StopLossStatus getStatus() { return status; }
    public void setStatus(StopLossStatus status) { this.status = status; }
}
