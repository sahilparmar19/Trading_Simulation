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
        setExecutedPrice(executedPrice);
        setQuantity(quantity);
        this.executedAt = executedAt;
    }

    public int getTradeId() { return tradeId; }

    public int getBuyOrderId() { return buyOrderId; }

    public int getSellOrderId() { return sellOrderId; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public double getExecutedPrice() { return executedPrice; }
    public void setExecutedPrice(double executedPrice) {
        if (executedPrice <= 0) {
            throw new IllegalArgumentException("Executed price must be greater than zero.");
        }
        this.executedPrice = executedPrice;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        this.quantity = quantity;
    }

    public Timestamp getExecutedAt() { return executedAt; }

    @Override
    public String toString() {
        return "Trade{" +
                "tradeId=" + tradeId +
                ", buyOrderId=" + buyOrderId +
                ", sellOrderId=" + sellOrderId +
                ", ticker='" + ticker + '\'' +
                ", executedPrice=" + executedPrice +
                ", quantity=" + quantity +
                ", executedAt=" + executedAt +
                '}';
    }
}
