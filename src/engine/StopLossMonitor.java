package engine;

import db.DatabaseManager;
import io.IOManager;
import model.Order;
import model.StopLossOrder;
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
        String sql = "SELECT * FROM stop_loss_orders WHERE status = 'ACTIVE'";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int slId = rs.getInt("sl_id");
                int userId = rs.getInt("user_id");
                String ticker = rs.getString("ticker");
                int quantity = rs.getInt("quantity");
                double stopPrice = rs.getDouble("stop_price");

                Stock stock = DatabaseManager.getStock(ticker);
                if (stock != null && stock.getCurrentPrice() <= stopPrice) {
                    triggerStopLoss(slId, userId, ticker, quantity, stopPrice);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking stop loss orders: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
    }

    private void triggerStopLoss(int slId, int userId, String ticker, int quantity, double stopPrice) {
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            // 1. Update stop-loss order status to TRIGGERED
            String updateSlSql = "UPDATE stop_loss_orders SET status = 'TRIGGERED' WHERE sl_id = ?";
            PreparedStatement ps1 = null;
            try {
                ps1 = conn.prepareStatement(updateSlSql);
                ps1.setInt(1, slId);
                ps1.executeUpdate();
            } finally {
                if (ps1 != null) { try { ps1.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            // 2. Create MARKET SELL order
            String insertOrderSql = "INSERT INTO orders (user_id, ticker, is_buy, order_type, price, quantity, stop_price, status, timestamp) " +
                                    "VALUES (?, ?, FALSE, 'MARKET', 0.0, ?, 0.0, 'PENDING', NOW()) RETURNING order_id";
            int orderId = -1;
            PreparedStatement ps2 = null;
            ResultSet rs = null;
            try {
                ps2 = conn.prepareStatement(insertOrderSql);
                ps2.setInt(1, userId);
                ps2.setString(2, ticker);
                ps2.setInt(3, quantity);
                rs = ps2.executeQuery();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                }
            } finally {
                if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
                if (ps2 != null) { try { ps2.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            conn.commit();

            if (orderId != -1) {
                // Log trigger
                StopLossOrder slo = new StopLossOrder(slId, userId, ticker, quantity, stopPrice, new Timestamp(System.currentTimeMillis()), "TRIGGERED");
                IOManager.logStopLossTrigger(slo);

                // Insert into OrderBook BST
                Order marketOrder = new Order(orderId, userId, ticker, false, "MARKET", 0.0, quantity, 0.0, new Timestamp(System.currentTimeMillis()), "PENDING");
                OrderBook book = OrderBook.get(ticker);
                synchronized (book) {
                    book.getSellSide().insert(marketOrder);
                }
                System.out.println("\n[SYSTEM] Stop-loss triggered for " + ticker + " (Stop Price: ₹" + stopPrice + "). Placed Market Sell Order ID " + orderId);
            }
        } catch (SQLException e) {
            System.err.println("Error triggering stop loss: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { System.err.println("Rollback failed: " + ex.getMessage()); }
            }
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
            }
        }
    }
}
