package db;

import model.Order;
import model.OrderStatus;
import java.sql.*;

public class TradeTransaction {
    public static boolean execute(Order buyOrder, Order sellOrder, double executedPrice, int quantity) {
        Connection con = null;
        try {
            con = DatabaseManager.getConnection();
            con.setAutoCommit(false);

            // 1. Check buyer's balance
            String checkBalanceSql = "SELECT balance FROM users WHERE user_id = ? FOR UPDATE";
            double buyerBalance = 0;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = con.prepareStatement(checkBalanceSql);
                ps.setInt(1, buyOrder.getUserId());
                rs = ps.executeQuery();
                if (rs.next()) {
                    buyerBalance = rs.getDouble("balance");
                } else {
                    throw new SQLException("Buyer not found");
                }
            } finally {
                if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
                if (ps != null) { try { ps.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            double totalCost = executedPrice * quantity;
            if (buyerBalance < totalCost) {
                // Insufficient funds: we fail this transaction and rollback
                throw new SQLException("Buyer has insufficient funds. Balance: " + buyerBalance + ", Cost: " + totalCost);
            }

            // 2. Lock seller's user record for update to avoid race conditions (unless it is the same user)
            if (buyOrder.getUserId() != sellOrder.getUserId()) {
                String lockSellerSql = "SELECT balance FROM users WHERE user_id = ? FOR UPDATE";
                PreparedStatement ps2 = null;
                ResultSet rs2 = null;
                try {
                    ps2 = con.prepareStatement(lockSellerSql);
                    ps2.setInt(1, sellOrder.getUserId());
                    rs2 = ps2.executeQuery();
                    if (!rs2.next()) {
                        throw new SQLException("Seller not found");
                    }
                } finally {
                    if (rs2 != null) { try { rs2.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
                    if (ps2 != null) { try { ps2.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
                }
            }

            // 3. Deduct buyer balance
            String deductSql = "UPDATE users SET balance = balance - ? WHERE user_id = ?";
            PreparedStatement ps3 = null;
            try {
                ps3 = con.prepareStatement(deductSql);
                ps3.setDouble(1, totalCost);
                ps3.setInt(2, buyOrder.getUserId());
                ps3.executeUpdate();
            } finally {
                if (ps3 != null) { try { ps3.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            // 4. Credit seller balance
            String creditSql = "UPDATE users SET balance = balance + ? WHERE user_id = ?";
            PreparedStatement ps4 = null;
            try {
                ps4 = con.prepareStatement(creditSql);
                ps4.setDouble(1, totalCost);
                ps4.setInt(2, sellOrder.getUserId());
                ps4.executeUpdate();
            } finally {
                if (ps4 != null) { try { ps4.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            // 5. Update portfolios
            // Buyer portfolio: buy quantity
            updatePortfolioInternal(con, buyOrder.getUserId(), buyOrder.getTicker(), quantity, executedPrice);
            // Seller portfolio: sell quantity
            updatePortfolioInternal(con, sellOrder.getUserId(), sellOrder.getTicker(), -quantity, executedPrice);

            // 6. Insert trade record
            String insertTradeSql = "INSERT INTO trades (buy_order_id, sell_order_id, ticker, executed_price, quantity, executed_at) " +
                                     "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps5 = null;
            try {
                ps5 = con.prepareStatement(insertTradeSql);
                ps5.setInt(1, buyOrder.getOrderId());
                ps5.setInt(2, sellOrder.getOrderId());
                ps5.setString(3, buyOrder.getTicker());
                ps5.setDouble(4, executedPrice);
                ps5.setInt(5, quantity);
                ps5.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                ps5.executeUpdate();
            } finally {
                if (ps5 != null) { try { ps5.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            // 7. Update orders in DB
            int newBuyQty = buyOrder.getQuantity() - quantity;
            OrderStatus newBuyStatus = newBuyQty == 0 ? OrderStatus.MATCHED : OrderStatus.PENDING;
            updateOrderInternal(con, buyOrder.getOrderId(), newBuyStatus, newBuyQty);

            int newSellQty = sellOrder.getQuantity() - quantity;
            OrderStatus newSellStatus = newSellQty == 0 ? OrderStatus.MATCHED : OrderStatus.PENDING;
            updateOrderInternal(con, sellOrder.getOrderId(), newSellStatus, newSellQty);

            con.commit();
            return true;
        } catch (SQLException e) {
            // System.err.println("Trade matching failed, rolling back. Reason: " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    private static void updatePortfolioInternal(Connection con, int userId, String ticker, int quantityChange, double price) throws SQLException {
        // Fetch current portfolio state
        String selectSql = "SELECT quantity, avg_buy_price FROM portfolio WHERE user_id = ? AND ticker = ? FOR UPDATE";
        int currentQty = 0;
        double currentAvg = 0.0;
        boolean exists = false;

        PreparedStatement selectPstmt = null;
        ResultSet rs = null;
        try {
            selectPstmt = con.prepareStatement(selectSql);
            selectPstmt.setInt(1, userId);
            selectPstmt.setString(2, ticker);
            rs = selectPstmt.executeQuery();
            if (rs.next()) {
                currentQty = rs.getInt("quantity");
                currentAvg = rs.getDouble("avg_buy_price");
                exists = true;
            }
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (selectPstmt != null) { try { selectPstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
        }

        int newQty = currentQty + quantityChange;
        double newAvg = currentAvg;

        if (quantityChange > 0) {
            // Recalculate average cost when buying
            newAvg = ((currentQty * currentAvg) + (quantityChange * price)) / newQty;
        }

        if (newQty <= 0) {
            String deleteSql = "DELETE FROM portfolio WHERE user_id = ? AND ticker = ?";
            PreparedStatement deletePstmt = null;
            try {
                deletePstmt = con.prepareStatement(deleteSql);
                deletePstmt.setInt(1, userId);
                deletePstmt.setString(2, ticker);
                deletePstmt.executeUpdate();
            } finally {
                if (deletePstmt != null) { try { deletePstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }
        } else {
            if (exists) {
                String updateSql = "UPDATE portfolio SET quantity = ?, avg_buy_price = ? WHERE user_id = ? AND ticker = ?";
                PreparedStatement updatePstmt = null;
                try {
                    updatePstmt = con.prepareStatement(updateSql);
                    updatePstmt.setInt(1, newQty);
                    updatePstmt.setDouble(2, newAvg);
                    updatePstmt.setInt(3, userId);
                    updatePstmt.setString(4, ticker);
                    updatePstmt.executeUpdate();
                } finally {
                    if (updatePstmt != null) { try { updatePstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
                }
            } else {
                String insertSql = "INSERT INTO portfolio (user_id, ticker, quantity, avg_buy_price) VALUES (?, ?, ?, ?)";
                PreparedStatement insertPstmt = null;
                try {
                    insertPstmt = con.prepareStatement(insertSql);
                    insertPstmt.setInt(1, userId);
                    insertPstmt.setString(2, ticker);
                    insertPstmt.setInt(3, newQty);
                    insertPstmt.setDouble(4, newAvg);
                    insertPstmt.executeUpdate();
                } finally {
                    if (insertPstmt != null) { try { insertPstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
                }
            }
        }
    }

    private static void updateOrderInternal(Connection con, int orderId, OrderStatus status, int quantity) throws SQLException {
        String sql = "UPDATE orders SET status = ?, quantity = ? WHERE order_id = ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, status.name());
            pstmt.setInt(2, quantity);
            pstmt.setInt(3, orderId);
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
        }
    }
}
