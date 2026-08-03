package model;

import java.sql.Timestamp;

public class Order implements Comparable<Order> {
    private int orderId;
    private int userId;
    private String ticker;
    private boolean isBuy;
    private OrderType orderType;
    private double price;
    private int quantity;
    private double stopPrice;
    private Timestamp timestamp;
    private OrderStatus status;

    public Order(int orderId, int userId, String ticker, boolean isBuy, OrderType orderType,
                 double price, int quantity, double stopPrice, Timestamp timestamp, OrderStatus status) {
        this.orderId = orderId;
        this.userId = userId;
        this.ticker = ticker;
        this.isBuy = isBuy;
        this.orderType = orderType;
        setPrice(price);
        setQuantity(quantity);
        setStopPrice(stopPrice);
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

    public OrderType getOrderType() { return orderType; }
    public void setOrderType(OrderType orderType) {
        validatePrice(orderType, price);
        this.orderType = orderType;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        validatePrice(orderType, price);
        this.price = price;
    }

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

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    private static void validatePrice(OrderType orderType, double price) {
        if (orderType == OrderType.LIMIT && price <= 0) {
            throw new IllegalArgumentException("Limit order price must be greater than zero.");
        }
    }

    @Override
    public int compareTo(Order other) {
        // Natural order: ascending by price, then ascending by orderId
        if (this.price != other.price) {
            return Double.compare(this.price, other.price);
        }
        return Integer.compare(this.orderId, other.orderId);
    }
}
