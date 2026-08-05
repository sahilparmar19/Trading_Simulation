package db;

import java.sql.*;

public class DividendManager {
    public static int declareDividend(String ticker, double amountPerShare) {
        String sql = "INSERT INTO dividends (ticker, amount_per_share, declared_at) VALUES (?, ?, NOW()) RETURNING dividend_id";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DatabaseManager.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, ticker);
            pstmt.setDouble(2, amountPerShare);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error declaring dividend: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (con != null) { try { con.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return -1;
    }

    public static boolean payAllShareholders(int dividendId) {
        Connection con = null;
        try {
            con = DatabaseManager.getConnection();
            con.setAutoCommit(false);

            // 1. Fetch dividend details
            String divSql = "SELECT ticker, amount_per_share FROM dividends WHERE dividend_id = ? AND paid_at IS NULL";
            String ticker = "";
            double amountPerShare = 0.0;
            PreparedStatement ps1 = null;
            ResultSet rs1 = null;
            try {
                ps1 = con.prepareStatement(divSql);
                ps1.setInt(1, dividendId);
                rs1 = ps1.executeQuery();
                if (rs1.next()) {
                    ticker = rs1.getString("ticker");
                    amountPerShare = rs1.getDouble("amount_per_share");
                } else {
                    throw new SQLException("Dividend not found with ID " + dividendId);
                }
            } finally {
                if (rs1 != null) { try { rs1.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
                if (ps1 != null) { try { ps1.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            // 2. Find all shareholders
            String holdersSql = "SELECT user_id, quantity FROM portfolio WHERE ticker = ? AND quantity > 0";
            String updateBalSql = "UPDATE users SET balance = balance + ? WHERE user_id = ?";
            String payRecordSql = "INSERT INTO dividend_payments (dividend_id, user_id, shares_held, total_paid, paid_at) VALUES (?, ?, ?, ?, NOW())";

            PreparedStatement selectHolders = null;
            PreparedStatement updateBalance = null;
            PreparedStatement insertPayment = null;
            ResultSet rs2 = null;
            try {
                selectHolders = con.prepareStatement(holdersSql);
                updateBalance = con.prepareStatement(updateBalSql);
                insertPayment = con.prepareStatement(payRecordSql);

                selectHolders.setString(1, ticker);
                rs2 = selectHolders.executeQuery();
                while (rs2.next()) {
                    int userId = rs2.getInt("user_id");
                    int quantity = rs2.getInt("quantity");
                    double payout = amountPerShare * quantity;

                    // Credit shareholder
                    updateBalance.setDouble(1, payout);
                    updateBalance.setInt(2, userId);
                    updateBalance.executeUpdate();

                    // Log payment
                    insertPayment.setInt(1, dividendId);
                    insertPayment.setInt(2, userId);
                    insertPayment.setInt(3, quantity);
                    insertPayment.setDouble(4, payout);
                    insertPayment.executeUpdate();
                }
            } finally {
                if (rs2 != null) { try { rs2.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
                if (selectHolders != null) { try { selectHolders.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
                if (updateBalance != null) { try { updateBalance.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
                if (insertPayment != null) { try { insertPayment.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            // 3. Mark paid_at in dividends table
            String updateDivSql = "UPDATE dividends SET paid_at = NOW() WHERE dividend_id = ?";
            PreparedStatement ps3 = null;
            try {
                ps3 = con.prepareStatement(updateDivSql);
                ps3.setInt(1, dividendId);
                ps3.executeUpdate();
            } finally {
                if (ps3 != null) { try { ps3.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error paying shareholders, rolling back. Reason: " + e.getMessage());
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
}
