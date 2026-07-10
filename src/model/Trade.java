package model;

import java.sql.Timestamp;

public class Trade {
    private int tradeId;
    private int buyOrderId;
    private int sellOrderId;
    private String ticker;
    private double executedPrice;
    private int quantity;
    private Timestamp executedAt;

    public Trade(int tradeId, int buyOrderId, int sellOrderId, String ticker, double executedPrice, int quantity, Timestamp executedAt) {
        this.tradeId = tradeId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.ticker = ticker;
        this.executedPrice = executedPrice;
        this.quantity = quantity;
        this.executedAt = executedAt;
    }

    public int getTradeId() { return tradeId; }
    public void setTradeId(int tradeId) { this.tradeId = tradeId; }

    public int getBuyOrderId() { return buyOrderId; }
    public void setBuyOrderId(int buyOrderId) { this.buyOrderId = buyOrderId; }

    public int getSellOrderId() { return sellOrderId; }
    public void setSellOrderId(int sellOrderId) { this.sellOrderId = sellOrderId; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public double getExecutedPrice() { return executedPrice; }
    public void setExecutedPrice(double executedPrice) { this.executedPrice = executedPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Timestamp getExecutedAt() { return executedAt; }
    public void setExecutedAt(Timestamp executedAt) { this.executedAt = executedAt; }
}
