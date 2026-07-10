package model;

import java.sql.Timestamp;

public class StopLossOrder {
    private int slId;
    private int userId;
    private String ticker;
    private int quantity;
    private double stopPrice;
    private Timestamp createdAt;
    private String status; // ACTIVE, TRIGGERED, CANCELLED

    public StopLossOrder(int slId, int userId, String ticker, int quantity, double stopPrice, Timestamp createdAt, String status) {
        this.slId = slId;
        this.userId = userId;
        this.ticker = ticker;
        this.quantity = quantity;
        this.stopPrice = stopPrice;
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
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getStopPrice() { return stopPrice; }
    public void setStopPrice(double stopPrice) { this.stopPrice = stopPrice; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
