package db;

import ds.CustomLinkedList;
import model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        Properties props = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("config.properties");
            props.load(fis);
            dbUrl = props.getProperty("db.url");
            dbUser = props.getProperty("db.user");
            dbPassword = props.getProperty("db.password");
            // Load driver explicitly just in case
            Class.forName("org.postgresql.Driver");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading config.properties or JDBC driver: " + e.getMessage());
        } finally {
            if (fis != null) {
                try { fis.close(); } catch (IOException e) { System.err.println("Error closing config stream: " + e.getMessage()); }
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    // Insert Order into DB and return the generated orderId
    public static int insertOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, ticker, is_buy, order_type, price, quantity, stop_price, status, timestamp) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING order_id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, order.getUserId());
            pstmt.setString(2, order.getTicker());
            pstmt.setBoolean(3, order.isBuy());
            pstmt.setString(4, order.getOrderType());
            pstmt.setDouble(5, order.getPrice());
            pstmt.setInt(6, order.getQuantity());
            pstmt.setDouble(7, order.getStopPrice());
            pstmt.setString(8, order.getStatus());
            pstmt.setTimestamp(9, order.getTimestamp());

            rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                order.setOrderId(id);
                return id;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting order: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return -1;
    }

    // Update order status and quantity
    public static void updateOrder(int orderId, String status, int quantity) {
        String sql = "UPDATE orders SET status = ?, quantity = ? WHERE order_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, quantity);
            pstmt.setInt(3, orderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
    }

    // Update user balance
    public static void updateBalance(int userId, double amount) {
        String sql = "UPDATE users SET balance = balance + ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating balance: " + e.getMessage());
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
    }

    // Get user balance
    public static double getUserBalance(int userId) {
        String sql = "SELECT balance FROM users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            System.err.println("Error getting user balance: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return 0.0;
    }

    // Update portfolio (upsert logic using INSERT ... ON CONFLICT)
    public static void updatePortfolio(int userId, String ticker, int quantityChange, double price) {
        // We need to calculate the new average buy price if we are buying (quantityChange > 0)
        // If we are selling, we just decrease the quantity. If quantity becomes 0, we can remove or keep at 0.
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Fetch current portfolio
            String selectSql = "SELECT quantity, avg_buy_price FROM portfolio WHERE user_id = ? AND ticker = ?";
            int currentQty = 0;
            double currentAvg = 0.0;
            boolean exists = false;

            PreparedStatement selectPstmt = null;
            ResultSet rs = null;
            try {
                selectPstmt = conn.prepareStatement(selectSql);
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
                // Buying: recalculate avg_buy_price
                newAvg = ((currentQty * currentAvg) + (quantityChange * price)) / newQty;
            }

            if (newQty <= 0) {
                // Remove from portfolio
                String deleteSql = "DELETE FROM portfolio WHERE user_id = ? AND ticker = ?";
                PreparedStatement deletePstmt = null;
                try {
                    deletePstmt = conn.prepareStatement(deleteSql);
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
                        updatePstmt = conn.prepareStatement(updateSql);
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
                        insertPstmt = conn.prepareStatement(insertSql);
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
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error updating portfolio: " + e.getMessage());
        } finally {
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
    }

    // Get user portfolio holdings
    public static CustomLinkedList getPortfolio(int userId) {
        CustomLinkedList holdings = new CustomLinkedList();
        String sql = "SELECT p.ticker, s.company_name, p.quantity, p.avg_buy_price, s.current_price " +
                     "FROM portfolio p JOIN stocks s ON p.ticker = s.ticker WHERE p.user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String ticker = rs.getString("ticker");
                String companyName = rs.getString("company_name");
                int quantity = rs.getInt("quantity");
                double avgBuyPrice = rs.getDouble("avg_buy_price");
                double currentPrice = rs.getDouble("current_price");
                holdings.addLast(new PortfolioHolding(ticker, companyName, quantity, avgBuyPrice, currentPrice));
            }
        } catch (SQLException e) {
            System.err.println("Error getting portfolio: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return holdings;
    }

    // Get stock by ticker
    public static Stock getStock(String ticker) {
        String sql = "SELECT * FROM stocks WHERE ticker = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ticker);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractStock(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting stock: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return null;
    }

    // Search stocks by name
    public static CustomLinkedList getStocksByName(String name) {
        CustomLinkedList list = new CustomLinkedList();
        String sql = "SELECT s.* FROM stocks s WHERE LOWER(s.company_name) LIKE ? OR LOWER(s.ticker) LIKE ? AND s.is_listed = TRUE";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + name.toLowerCase() + "%");
            pstmt.setString(2, "%" + name.toLowerCase() + "%");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.addLast(extractStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching stocks by name: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return list;
    }

    // Search stocks by sector name
    public static CustomLinkedList getStocksBySector(String sectorName) {
        CustomLinkedList list = new CustomLinkedList();
        String sql = "SELECT s.* FROM stocks s JOIN sectors sec ON s.sector_id = sec.sector_id " +
                     "WHERE LOWER(sec.sector_name) LIKE ? AND s.is_listed = TRUE";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + sectorName.toLowerCase() + "%");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.addLast(extractStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching stocks by sector: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return list;
    }

    // Get all sector names (sector_id -> sector_name)
    public static CustomLinkedList getAllSectors() {
        CustomLinkedList list = new CustomLinkedList();
        String sql = "SELECT sector_id, sector_name FROM sectors ORDER BY sector_id ASC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.addLast(new SectorInfo(rs.getInt("sector_id"), rs.getString("sector_name")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sectors: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return list;
    }

    // Get all listed stocks under a specific sector ID
    public static CustomLinkedList getStocksBySectorId(int sectorId) {
        CustomLinkedList list = new CustomLinkedList();
        String sql = "SELECT s.* FROM stocks s WHERE s.sector_id = ? AND s.is_listed = TRUE ORDER BY s.ticker ASC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sectorId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.addLast(extractStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching stocks by sector ID: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return list;
    }

    // Helper to extract stock
    private static Stock extractStock(ResultSet rs) throws SQLException {
        return new Stock(
            rs.getString("ticker"),
            rs.getString("company_name"),
            rs.getInt("sector_id"),
            rs.getDouble("current_price"),
            rs.getDouble("open_price"),
            rs.getDouble("prev_close"),
            rs.getDouble("market_cap"),
            rs.getDouble("pe_ratio"),
            rs.getDouble("pb_ratio"),
            rs.getDouble("roe"),
            rs.getDouble("roa"),
            rs.getLong("total_shares"),
            rs.getDouble("promoter_hold"),
            rs.getDouble("inst_hold"),
            rs.getDouble("retail_hold"),
            rs.getBoolean("is_listed"),
            rs.getString("exchange")
        );
    }

    // Get price history with period filter (7D / 1M / 6M / 52W)
    public static CustomLinkedList getPriceHistory(String ticker, String period) {
        CustomLinkedList history = new CustomLinkedList();

        // Compute cutoff time in Java — avoids JDBC INTERVAL casting issues
        long nowMs = System.currentTimeMillis();
        long cutoffMs;
        if (period.equalsIgnoreCase("1M")) {
            cutoffMs = nowMs - (long) 30 * 24 * 60 * 60 * 1000;
        } else if (period.equalsIgnoreCase("6M")) {
            cutoffMs = nowMs - (long) 183 * 24 * 60 * 60 * 1000;
        } else if (period.equalsIgnoreCase("52W")) {
            cutoffMs = nowMs - (long) 365 * 24 * 60 * 60 * 1000;
        } else {
            // Default: 7D
            cutoffMs = nowMs - (long) 7 * 24 * 60 * 60 * 1000;
        }
        Timestamp cutoff = new Timestamp(cutoffMs);

        String sql = "SELECT * FROM price_history WHERE ticker = ? AND recorded_at >= ? ORDER BY recorded_at ASC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, ticker);
            pstmt.setTimestamp(2, cutoff);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                history.addLast(new PriceHistory(
                    rs.getString("ticker"),
                    rs.getDouble("price"),
                    rs.getTimestamp("recorded_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting price history: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return history;
    }


    // Get top gainers
    public static CustomLinkedList getTopGainers(int n) {
        CustomLinkedList list = new CustomLinkedList();
        String sql = "SELECT *, ((current_price - prev_close) / prev_close * 100) AS pct_change " +
                     "FROM stocks WHERE is_listed = TRUE AND prev_close > 0 ORDER BY pct_change DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, n);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.addLast(extractStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting top gainers: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return list;
    }

    // Get top losers
    public static CustomLinkedList getTopLosers(int n) {
        CustomLinkedList list = new CustomLinkedList();
        String sql = "SELECT *, ((current_price - prev_close) / prev_close * 100) AS pct_change " +
                     "FROM stocks WHERE is_listed = TRUE AND prev_close > 0 ORDER BY pct_change ASC LIMIT ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, n);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.addLast(extractStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting top losers: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return list;
    }

    // Average P&L grouped by sector
    public static CustomLinkedList getSectorPnL() {
        CustomLinkedList list = new CustomLinkedList();
        String sql = "SELECT sec.sector_name, " +
                     "COALESCE(AVG((s.current_price - p.avg_buy_price) * p.quantity), 0) AS avg_pnl, " +
                     "COALESCE(AVG(((s.current_price - p.avg_buy_price) / p.avg_buy_price) * 100), 0) AS avg_pnl_pct " +
                     "FROM portfolio p " +
                     "JOIN stocks s ON p.ticker = s.ticker " +
                     "JOIN sectors sec ON s.sector_id = sec.sector_id " +
                     "GROUP BY sec.sector_name";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.addLast(new SectorPnL(
                    rs.getString("sector_name"),
                    rs.getDouble("avg_pnl"),
                    rs.getDouble("avg_pnl_pct")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting sector PnL: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return list;
    }

    // Watchlist methods
    public static void addToWatchlist(int userId, String ticker) {
        String sql = "INSERT INTO watchlist (user_id, ticker) VALUES (?, ?) ON CONFLICT DO NOTHING";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, ticker);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding to watchlist: " + e.getMessage());
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
    }

    public static void removeFromWatchlist(int userId, String ticker) {
        String sql = "DELETE FROM watchlist WHERE user_id = ? AND ticker = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, ticker);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing from watchlist: " + e.getMessage());
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
    }

    public static CustomLinkedList getWatchlist(int userId) {
        CustomLinkedList list = new CustomLinkedList();
        String sql = "SELECT s.* FROM watchlist w JOIN stocks s ON w.ticker = s.ticker WHERE w.user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.addLast(extractStock(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting watchlist: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return list;
    }

    // Helper DTO for Portfolio Holding
    public static class PortfolioHolding {
        public String ticker;
        public String companyName;
        public int quantity;
        public double avgBuyPrice;
        public double currentPrice;

        public PortfolioHolding(String ticker, String companyName, int quantity, double avgBuyPrice, double currentPrice) {
            this.ticker = ticker;
            this.companyName = companyName;
            this.quantity = quantity;
            this.avgBuyPrice = avgBuyPrice;
            this.currentPrice = currentPrice;
        }

        public double getPnL() {
            return (currentPrice - avgBuyPrice) * quantity;
        }

        public double getPnLPct() {
            if (avgBuyPrice == 0) return 0.0;
            return ((currentPrice - avgBuyPrice) / avgBuyPrice) * 100.0;
        }
    }

    // Helper DTO for Sector P&L
    public static class SectorPnL {
        public String sectorName;
        public double avgPnL;
        public double avgPnLPct;

        public SectorPnL(String sectorName, double avgPnL, double avgPnLPct) {
            this.sectorName = sectorName;
            this.avgPnL = avgPnL;
            this.avgPnLPct = avgPnLPct;
        }
    }

    // Helper DTO for Sector Info
    public static class SectorInfo {
        public int sectorId;
        public String sectorName;

        public SectorInfo(int sectorId, String sectorName) {
            this.sectorId = sectorId;
            this.sectorName = sectorName;
        }
    }

    // Get pending limit orders for a user
    public static CustomLinkedList getPendingLimitOrders(int userId) {
        CustomLinkedList list = new CustomLinkedList();
        String sql = "SELECT * FROM orders WHERE user_id = ? AND order_type = 'LIMIT' AND status = 'PENDING' ORDER BY timestamp DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.addLast(new Order(
                    rs.getInt("order_id"),
                    rs.getInt("user_id"),
                    rs.getString("ticker"),
                    rs.getBoolean("is_buy"),
                    rs.getString("order_type"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getDouble("stop_price"),
                    rs.getTimestamp("timestamp"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending limit orders: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return list;
    }

    // Update stock price and history record
    public static void updateStockPriceAndHistory(String ticker, double price) {
        String updateStockSql = "UPDATE stocks SET current_price = ? WHERE ticker = ?";
        String insertHistorySql = "INSERT INTO price_history (ticker, price, recorded_at) VALUES (?, ?, NOW())";
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps1 = null;
            PreparedStatement ps2 = null;
            try {
                ps1 = conn.prepareStatement(updateStockSql);
                ps2 = conn.prepareStatement(insertHistorySql);
                ps1.setDouble(1, price);
                ps1.setString(2, ticker);
                ps1.executeUpdate();

                ps2.setString(1, ticker);
                ps2.setDouble(2, price);
                ps2.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                if (ps1 != null) { try { ps1.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
                if (ps2 != null) { try { ps2.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }
        } catch (SQLException e) {
            System.err.println("Error updating stock price and history: " + e.getMessage());
        } finally {
            if (conn != null) { try { conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
    }
}
