package io;

import db.DatabaseManager;
import model.IPO;
import model.Stock;
import ds.CustomLinkedList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportGenerator {

    static {
        File reportsDir = new File("reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
    }

    public static String exportTransactionHistory(int userId) {
        String filePath = "reports/user_" + userId + "_history.csv";
        String sql = "SELECT t.executed_at, t.ticker, " +
                     "(CASE WHEN o_buy.user_id = ? THEN 'BUY' ELSE 'SELL' END) AS trade_type, " +
                     "t.quantity, t.executed_price, (t.quantity * t.executed_price) AS total_val " +
                     "FROM trades t " +
                     "JOIN orders o_buy ON t.buy_order_id = o_buy.order_id " +
                     "JOIN orders o_sell ON t.sell_order_id = o_sell.order_id " +
                     "WHERE o_buy.user_id = ? OR o_sell.user_id = ? " +
                     "ORDER BY t.executed_at DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            fw = new FileWriter(filePath);
            pw = new PrintWriter(fw);

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);

            pw.println("Date,Ticker,Type,Qty,Price,Total Value");

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("executed_at");
                String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(ts.getTime()));
                String ticker = rs.getString("ticker");
                String type = rs.getString("trade_type");
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("executed_price");
                double totalVal = rs.getDouble("total_val");

                pw.printf("%s,%s,%s,%d,%.2f,%.2f%n", dateStr, ticker, type, qty, price, totalVal);
            }
            return filePath;
        } catch (SQLException | IOException e) {
            System.err.println("Error exporting transaction history: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
            if (pw != null) { pw.close(); }
            if (fw != null) { try { fw.close(); } catch (IOException e) { System.err.println("Error closing FileWriter: " + e.getMessage()); } }
        }
        return null;
    }

    public static String exportPortfolio(int userId) {
        String filePath = "reports/user_" + userId + "_portfolio.csv";
        CustomLinkedList<DatabaseManager.PortfolioHolding> holdings = DatabaseManager.getPortfolio(userId);

        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(filePath);
            pw = new PrintWriter(fw);

            pw.println("Company,Ticker,Qty,Avg Buy Price,Current Price,P&L (INR),P&L (%)");

            for (DatabaseManager.PortfolioHolding holding : holdings) {
                pw.printf("\"%s\",%s,%d,%.2f,%.2f,%.2f,%.2f%%%n",
                        holding.companyName, holding.ticker, holding.quantity,
                        holding.avgBuyPrice, holding.currentPrice,
                        holding.getPnL(), holding.getPnLPct());
            }
            return filePath;
        } catch (IOException e) {
            System.err.println("Error exporting portfolio: " + e.getMessage());
        } finally {
            if (pw != null) { pw.close(); }
            if (fw != null) { try { fw.close(); } catch (IOException e) { System.err.println("Error closing FileWriter: " + e.getMessage()); } }
        }
        return null;
    }

    public static String exportIPOAllotment(int ipoId) {
        IPO ipo = db.IPOManager.getIPO(ipoId);
        if (ipo == null) return null;

        String ticker = ipo.getTicker();
        String filePath = "reports/ipo_" + ticker + "_allotment.txt";

        String sql = "SELECT u.username, app.applied_qty, app.allotted_qty, " +
                     "((app.applied_qty - app.allotted_qty) * ?) AS refund_amt " +
                     "FROM ipo_applications app " +
                     "JOIN users u ON app.user_id = u.user_id " +
                     "WHERE app.ipo_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement(sql);
            fw = new FileWriter(filePath);
            pw = new PrintWriter(fw);

            pstmt.setDouble(1, ipo.getIpoPrice());
            pstmt.setInt(2, ipoId);

            pw.println("==================================================");
            pw.println("           IPO ALLOTMENT REPORT");
            pw.printf("Company: %s (%s)%n", ipo.getCompanyName(), ticker);
            pw.printf("IPO Price: INR %.2f | Total Shares: %d%n", ipo.getIpoPrice(), ipo.getTotalShares());
            pw.println("==================================================");
            pw.printf("%-15s %-12s %-12s %-12s%n", "Username", "Applied Qty", "Allotted Qty", "Refund Amt");
            pw.println("--------------------------------------------------");

            rs = pstmt.executeQuery();
            while (rs.next()) {
                pw.printf("%-15s %-12d %-12d INR %-10.2f%n",
                        rs.getString("username"),
                        rs.getInt("applied_qty"),
                        rs.getInt("allotted_qty"),
                        rs.getDouble("refund_amt"));
            }
            pw.println("==================================================");
            return filePath;
        } catch (SQLException | IOException e) {
            System.err.println("Error exporting IPO allotment report: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
            if (pw != null) { pw.close(); }
            if (fw != null) { try { fw.close(); } catch (IOException e) { System.err.println("Error closing FileWriter: " + e.getMessage()); } }
        }
        return null;
    }

    public static String exportDividendReport(int dividendId) {
        String ticker = "";
        double amountPerShare = 0.0;
        Timestamp declaredAt = null;

        String divSql = "SELECT ticker, amount_per_share, declared_at FROM dividends WHERE dividend_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement(divSql);
            pstmt.setInt(1, dividendId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ticker = rs.getString("ticker");
                amountPerShare = rs.getDouble("amount_per_share");
                declaredAt = rs.getTimestamp("declared_at");
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching dividend: " + e.getMessage());
            return null;
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }

        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date(declaredAt.getTime()));
        String filePath = "reports/dividend_" + ticker + "_" + dateStr + ".txt";

        String paymentsSql = "SELECT u.username, pay.shares_held, pay.total_paid, pay.paid_at " +
                             "FROM dividend_payments pay " +
                             "JOIN users u ON pay.user_id = u.user_id " +
                             "WHERE pay.dividend_id = ?";

        Connection conn2 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs2 = null;
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            conn2 = DatabaseManager.getConnection();
            pstmt2 = conn2.prepareStatement(paymentsSql);
            fw = new FileWriter(filePath);
            pw = new PrintWriter(fw);

            pstmt2.setInt(1, dividendId);

            pw.println("==================================================");
            pw.println("             DIVIDEND PAYOUT REPORT");
            pw.printf("Stock Ticker: %s%n", ticker);
            pw.printf("Dividend declared: INR %.2f per share%n", amountPerShare);
            pw.printf("Declared Date: %s%n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(declaredAt.getTime())));
            pw.println("==================================================");
            pw.printf("%-15s %-15s %-15s%n", "Username", "Shares Held", "Total Paid");
            pw.println("--------------------------------------------------");

            rs2 = pstmt2.executeQuery();
            while (rs2.next()) {
                pw.printf("%-15s %-15d INR %-12.2f%n",
                        rs2.getString("username"),
                        rs2.getInt("shares_held"),
                        rs2.getDouble("total_paid"));
            }
            pw.println("==================================================");
            return filePath;
        } catch (SQLException | IOException e) {
            System.err.println("Error exporting dividend report: " + e.getMessage());
        } finally {
            if (rs2 != null) { try { rs2.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt2 != null) { try { pstmt2.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn2 != null) { try { conn2.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
            if (pw != null) { pw.close(); }
            if (fw != null) { try { fw.close(); } catch (IOException e) { System.err.println("Error closing FileWriter: " + e.getMessage()); } }
        }
        return null;
    }
}
