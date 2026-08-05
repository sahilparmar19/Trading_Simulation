package engine;

import db.DatabaseManager;
import io.IOManager;
import model.Order;
import model.OrderStatus;
import model.OrderType;
import model.StopLossOrder;
import model.StopLossStatus;
import model.Stock;

import java.sql.*;

public class StopLossMonitor extends Thread {
    private boolean running = true;

    public StopLossMonitor() {
        this.setDaemon(true);
    }

    public void shutdown() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(2000); // Check every 2 seconds
                checkStopLossOrders();
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                System.err.println("Error in StopLossMonitor: " + e.getMessage());
            }
        }
    }

    private void checkStopLossOrders() {
        String sql = "SELECT * FROM stop_loss_orders WHERE status = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DatabaseManager.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, StopLossStatus.ACTIVE.name());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int slId = rs.getInt("sl_id");
                int userId = rs.getInt("user_id");
                String ticker = rs.getString("ticker");
                int quantity = rs.getInt("quantity");
                double stopPrice = rs.getDouble("stop_price");
                StopLossStatus status = StopLossStatus.valueOf(rs.getString("status"));

                Stock stock = DatabaseManager.getStock(ticker);
                if (status == StopLossStatus.ACTIVE && stock != null && stock.getCurrentPrice() <= stopPrice) {
                    triggerStopLoss(slId, userId, ticker, quantity, stopPrice);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking stop loss orders: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (con != null) { try { con.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
    }

    private void triggerStopLoss(int slId, int userId, String ticker, int quantity, double stopPrice) {
        Connection con = null;
        try {
            con = DatabaseManager.getConnection();
            con.setAutoCommit(false);

            // 1. Update stop-loss order status to TRIGGERED
            String updateSlSql = "UPDATE stop_loss_orders SET status = ? WHERE sl_id = ?";
            PreparedStatement ps1 = null;
            try {
                ps1 = con.prepareStatement(updateSlSql);
                ps1.setString(1, StopLossStatus.TRIGGERED.name());
                ps1.setInt(2, slId);
                ps1.executeUpdate();
            } finally {
                if (ps1 != null) { try { ps1.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            // 2. Create MARKET SELL order
            String insertOrderSql = "INSERT INTO orders (user_id, ticker, is_buy, order_type, price, quantity, stop_price, status, timestamp) " +
                                    "VALUES (?, ?, FALSE, ?, 0.0, ?, 0.0, ?, NOW()) RETURNING order_id";
            int orderId = -1;
            PreparedStatement ps2 = null;
            ResultSet rs = null;
            try {
                ps2 = con.prepareStatement(insertOrderSql);
                ps2.setInt(1, userId);
                ps2.setString(2, ticker);
                ps2.setString(3, OrderType.MARKET.name());
                ps2.setInt(4, quantity);
                ps2.setString(5, OrderStatus.PENDING.name());
                rs = ps2.executeQuery();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                }
            } finally {
                if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
                if (ps2 != null) { try { ps2.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            con.commit();

            if (orderId != -1) {
                // Log trigger
                StopLossOrder slo = new StopLossOrder(slId, userId, ticker, quantity, stopPrice, new Timestamp(System.currentTimeMillis()), StopLossStatus.TRIGGERED);
                IOManager.logStopLossTrigger(slo);

                // Insert into OrderBook BST
                Order marketOrder = new Order(orderId, userId, ticker, false, OrderType.MARKET, 0.0, quantity, 0.0, new Timestamp(System.currentTimeMillis()), OrderStatus.PENDING);
                OrderBook book = OrderBook.get(ticker);
                synchronized (book) {
                    book.getSellSide().insert(marketOrder);
                }
                System.out.println("\n[SYSTEM] Stop-loss triggered for " + ticker + " (Stop Price: ₹" + stopPrice + "). Placed Market Sell Order ID " + orderId);
            }
        } catch (SQLException e) {
            System.err.println("Error triggering stop loss: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { System.err.println("Rollback failed: " + ex.getMessage()); }
            }
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
            }
        }
    }
}
