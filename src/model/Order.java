package model;

import java.sql.Timestamp;

public class Order implements Comparable<Order> {
    private int orderId;
    private int userId;
    private String ticker;
    private boolean isBuy;
    private String orderType; // LIMIT, MARKET, STOP_LOSS
    private double price;
    private int quantity;
    private double stopPrice;
    private Timestamp timestamp;
    private String status; // PENDING, MATCHED, CANCELLED, TRIGGERED

    public Order(int orderId, int userId, String ticker, boolean isBuy, String orderType,
                 double price, int quantity, double stopPrice, Timestamp timestamp, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.ticker = ticker;
        this.isBuy = isBuy;
        this.orderType = orderType;
        this.price = price;
        this.quantity = quantity;
        this.stopPrice = stopPrice;
        this.timestamp = timestamp;
        this.status = status;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public boolean isBuy() { return isBuy; }
    public void setBuy(boolean buy) { isBuy = buy; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getStopPrice() { return stopPrice; }
    public void setStopPrice(double stopPrice) { this.stopPrice = stopPrice; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public int compareTo(Order other) {
        // Natural order: ascending by price, then ascending by orderId
        if (this.price != other.price) {
            return Double.compare(this.price, other.price);
        }
        return Integer.compare(this.orderId, other.orderId);
    }
}
