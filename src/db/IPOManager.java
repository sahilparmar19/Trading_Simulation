package db;

import model.IPO;
import model.IPOStatus;
import java.sql.*;

public class IPOManager {

    public static int createIPO(String companyName, String ticker, int sectorId, double ipoPrice, long totalShares, Timestamp openTime, Timestamp closeTime) {
        Connection con = null;
        try {
            con = DatabaseManager.getConnection();
            con.setAutoCommit(false);

            // 1. Insert into stocks (is_listed = FALSE)
            String insertStockSql = "INSERT INTO stocks (ticker, company_name, sector_id, current_price, open_price, prev_close, market_cap, " +
                                    "pe_ratio, pb_ratio, roe, roa, total_shares, promoter_hold, inst_hold, retail_hold, is_listed, exchange) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, FALSE, 'NSE')";
            PreparedStatement ps1 = null;
            try {
                ps1 = con.prepareStatement(insertStockSql);
                ps1.setString(1, ticker);
                ps1.setString(2, companyName);
                ps1.setInt(3, sectorId);
                ps1.setDouble(4, ipoPrice);
                ps1.setDouble(5, ipoPrice);
                ps1.setDouble(6, ipoPrice);
                ps1.setDouble(7, ipoPrice * totalShares);
                ps1.setDouble(8, 15.0); // PE
                ps1.setDouble(9, 1.5);  // PB
                ps1.setDouble(10, 0.12); // ROE
                ps1.setDouble(11, 0.06); // ROA
                ps1.setLong(12, totalShares);
                ps1.setDouble(13, 50.0); // promoter hold
                ps1.setDouble(14, 25.0); // inst hold
                ps1.setDouble(15, 25.0); // retail hold
                ps1.executeUpdate();
            } finally {
                if (ps1 != null) { try { ps1.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            // 2. Insert into ipos
            String insertIpoSql = "INSERT INTO ipos (ticker, company_name, sector_id, ipo_price, total_shares, shares_remaining, open_time, close_time, status) " +
                                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING ipo_id";
            int ipoId = -1;
            PreparedStatement ps2 = null;
            ResultSet rs2 = null;
            try {
                ps2 = con.prepareStatement(insertIpoSql);
                ps2.setString(1, ticker);
                ps2.setString(2, companyName);
                ps2.setInt(3, sectorId);
                ps2.setDouble(4, ipoPrice);
                ps2.setLong(5, totalShares);
                ps2.setLong(6, totalShares);
                ps2.setTimestamp(7, openTime);
                ps2.setTimestamp(8, closeTime);
                ps2.setString(9, IPOStatus.UPCOMING.name());
                rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    ipoId = rs2.getInt(1);
                }
            } finally {
                if (rs2 != null) { try { rs2.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
                if (ps2 != null) { try { ps2.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            con.commit();
            return ipoId;
        } catch (SQLException e) {
            System.err.println("Error creating IPO, rolling back. Reason: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { System.err.println("Rollback failed: " + ex.getMessage()); }
            }
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
            }
        }
        return -1;
    }

    public static void openIPO(int ipoId) {
        String sql = "UPDATE ipos SET status = ? WHERE ipo_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DatabaseManager.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, IPOStatus.OPEN.name());
            pstmt.setInt(2, ipoId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error opening IPO: " + e.getMessage());
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (con != null) { try { con.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
    }

    public static boolean applyForIPO(int userId, int ipoId, int qty) {
        Connection con = null;
        try {
            con = DatabaseManager.getConnection();
            con.setAutoCommit(false);

            // 1. Fetch IPO details
            String ipoSql = "SELECT ipo_price, status FROM ipos WHERE ipo_id = ? FOR UPDATE";
            double ipoPrice = 0;
            IPOStatus status;
            PreparedStatement ps1 = null;
            ResultSet rs1 = null;
            try {
                ps1 = con.prepareStatement(ipoSql);
                ps1.setInt(1, ipoId);
                rs1 = ps1.executeQuery();
                if (rs1.next()) {
                    ipoPrice = rs1.getDouble("ipo_price");
                    status = IPOStatus.valueOf(rs1.getString("status"));
                } else {
                    throw new SQLException("IPO not found");
                }
            } finally {
                if (rs1 != null) { try { rs1.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
                if (ps1 != null) { try { ps1.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            if (status != IPOStatus.OPEN) {
                throw new SQLException("IPO is not open for applications. Status: " + status);
            }

            // 1b. Check if user already applied
            String checkAppSql = "SELECT 1 FROM ipo_applications WHERE ipo_id = ? AND user_id = ?";
            PreparedStatement psCheck = null;
            ResultSet rsCheck = null;
            try {
                psCheck = con.prepareStatement(checkAppSql);
                psCheck.setInt(1, ipoId);
                psCheck.setInt(2, userId);
                rsCheck = psCheck.executeQuery();
                if (rsCheck.next()) {
                    throw new SQLException("You have already applied for this IPO.");
                }
            } finally {
                if (rsCheck != null) { try { rsCheck.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
                if (psCheck != null) { try { psCheck.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            // 2. Check user balance
            String balSql = "SELECT balance FROM users WHERE user_id = ? FOR UPDATE";
            double userBalance = 0;
            PreparedStatement ps2 = null;
            ResultSet rs2 = null;
            try {
                ps2 = con.prepareStatement(balSql);
                ps2.setInt(1, userId);
                rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    userBalance = rs2.getDouble("balance");
                } else {
                    throw new SQLException("User not found");
                }
            } finally {
                if (rs2 != null) { try { rs2.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
                if (ps2 != null) { try { ps2.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            double totalCost = ipoPrice * qty;
            if (userBalance < totalCost) {
                throw new SQLException("Insufficient balance to apply for IPO. Required: " + totalCost + ", Available: " + userBalance);
            }

            // 3. Deduct balance (Locking)
            String deductSql = "UPDATE users SET balance = balance - ? WHERE user_id = ?";
            PreparedStatement ps3 = null;
            try {
                ps3 = con.prepareStatement(deductSql);
                ps3.setDouble(1, totalCost);
                ps3.setInt(2, userId);
                ps3.executeUpdate();
            } finally {
                if (ps3 != null) { try { ps3.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            // 4. Insert application record
            String appSql = "INSERT INTO ipo_applications (ipo_id, user_id, applied_qty, allotted_qty) VALUES (?, ?, ?, 0)";
            PreparedStatement ps4 = null;
            try {
                ps4 = con.prepareStatement(appSql);
                ps4.setInt(1, ipoId);
                ps4.setInt(2, userId);
                ps4.setInt(3, qty);
                ps4.executeUpdate();
            } finally {
                if (ps4 != null) { try { ps4.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error applying for IPO: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { System.err.println("Rollback failed: " + ex.getMessage()); }
            }
            return false;
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
            }
        }
    }

    public static boolean processAllotment(int ipoId) {
        Connection con = null;
        try {
            con = DatabaseManager.getConnection();
            con.setAutoCommit(false);

            // 1. Fetch IPO details
            String ipoSql = "SELECT ticker, ipo_price, total_shares, status FROM ipos WHERE ipo_id = ? FOR UPDATE";
            String ticker = "";
            double ipoPrice = 0;
            long totalShares = 0;
            IPOStatus status;
            PreparedStatement ps1 = null;
            ResultSet rs1 = null;
            try {
                ps1 = con.prepareStatement(ipoSql);
                ps1.setInt(1, ipoId);
                rs1 = ps1.executeQuery();
                if (rs1.next()) {
                    ticker = rs1.getString("ticker");
                    ipoPrice = rs1.getDouble("ipo_price");
                    totalShares = rs1.getLong("total_shares");
                    status = IPOStatus.valueOf(rs1.getString("status"));
                } else {
                    throw new SQLException("IPO not found");
                }
            } finally {
                if (rs1 != null) { try { rs1.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
                if (ps1 != null) { try { ps1.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            if (status != IPOStatus.OPEN) {
                throw new SQLException("IPO is not open/cannot be allotted. Status: " + status);
            }

            // 2. Sum up total applied shares
            String sumSql = "SELECT SUM(applied_qty) FROM ipo_applications WHERE ipo_id = ?";
            long totalApplied = 0;
            PreparedStatement ps2 = null;
            ResultSet rs2 = null;
            try {
                ps2 = con.prepareStatement(sumSql);
                ps2.setInt(1, ipoId);
                rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    totalApplied = rs2.getLong(1);
                }
            } finally {
                if (rs2 != null) { try { rs2.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
                if (ps2 != null) { try { ps2.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            if (totalApplied == 0) {
                // No applicants: close IPO
                String updateIpoSql = "UPDATE ipos SET status = ?, shares_remaining = ? WHERE ipo_id = ?";
                PreparedStatement ps3 = null;
                try {
                    ps3 = con.prepareStatement(updateIpoSql);
                    ps3.setString(1, IPOStatus.CLOSED.name());
                    ps3.setLong(2, totalShares);
                    ps3.setInt(3, ipoId);
                    ps3.executeUpdate();
                } finally {
                    if (ps3 != null) { try { ps3.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
                }
                con.commit();
                return true;
            }

            // 3. Process applications
            String appsSql = "SELECT app_id, user_id, applied_qty FROM ipo_applications WHERE ipo_id = ? FOR UPDATE";
            String updateAppSql = "UPDATE ipo_applications SET allotted_qty = ? WHERE app_id = ?";
            String refundSql = "UPDATE users SET balance = balance + ? WHERE user_id = ?";
            String insertPortfolioSql = "INSERT INTO portfolio (user_id, ticker, quantity, avg_buy_price) VALUES (?, ?, ?, ?) " +
                                        "ON CONFLICT (user_id, ticker) DO UPDATE SET quantity = portfolio.quantity + EXCLUDED.quantity, " +
                                        "avg_buy_price = ((portfolio.quantity * portfolio.avg_buy_price) + (EXCLUDED.quantity * EXCLUDED.avg_buy_price)) / (portfolio.quantity + EXCLUDED.quantity)";

            long sharesAllottedTotal = 0;

            PreparedStatement getApps = null;
            PreparedStatement updateApp = null;
            PreparedStatement refundUser = null;
            PreparedStatement updatePortfolio = null;
            ResultSet rs3 = null;
            try {
                getApps = con.prepareStatement(appsSql);
                updateApp = con.prepareStatement(updateAppSql);
                refundUser = con.prepareStatement(refundSql);
                updatePortfolio = con.prepareStatement(insertPortfolioSql);

                getApps.setInt(1, ipoId);
                rs3 = getApps.executeQuery();
                while (rs3.next()) {
                    int appId = rs3.getInt("app_id");
                    int userId = rs3.getInt("user_id");
                    int appliedQty = rs3.getInt("applied_qty");
                    int allottedQty = 0;

                    if (totalApplied <= totalShares) {
                        // Undersubscribed: full allotment
                        allottedQty = appliedQty;
                    } else {
                        // Oversubscribed: proportional allotment
                        double ratio = (double) totalShares / totalApplied;
                        allottedQty = (int) Math.floor(appliedQty * ratio);
                        // Refund excess
                        double refundAmount = (appliedQty - allottedQty) * ipoPrice;
                        if (refundAmount > 0) {
                            refundUser.setDouble(1, refundAmount);
                            refundUser.setInt(2, userId);
                            refundUser.executeUpdate();
                        }
                    }

                    sharesAllottedTotal += allottedQty;

                    // Save allotment qty in application record
                    updateApp.setInt(1, allottedQty);
                    updateApp.setInt(2, appId);
                    updateApp.executeUpdate();

                    // Add allotted shares to user portfolio
                    if (allottedQty > 0) {
                        updatePortfolio.setInt(1, userId);
                        updatePortfolio.setString(2, ticker);
                        updatePortfolio.setInt(3, allottedQty);
                        updatePortfolio.setDouble(4, ipoPrice);
                        updatePortfolio.executeUpdate();
                    }
                }
            } finally {
                if (rs3 != null) { try { rs3.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
                if (getApps != null) { try { getApps.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
                if (updateApp != null) { try { updateApp.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
                if (refundUser != null) { try { refundUser.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
                if (updatePortfolio != null) { try { updatePortfolio.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            long remainingShares = totalShares - sharesAllottedTotal;

            // 4. Update IPO table status to CLOSED and remaining shares
            String updateIpoSql = "UPDATE ipos SET status = ?, shares_remaining = ? WHERE ipo_id = ?";
            PreparedStatement ps4 = null;
            try {
                ps4 = con.prepareStatement(updateIpoSql);
                ps4.setString(1, IPOStatus.CLOSED.name());
                ps4.setLong(2, remainingShares);
                ps4.setInt(3, ipoId);
                ps4.executeUpdate();
            } finally {
                if (ps4 != null) { try { ps4.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error processing allotment, rolling back. Reason: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { System.err.println("Rollback failed: " + ex.getMessage()); }
            }
            return false;
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
            }
        }
    }

    public static void listStock(int ipoId) {
        Connection con = null;
        try {
            con = DatabaseManager.getConnection();
            con.setAutoCommit(false);

            // Fetch IPO ticker
            String getTickerSql = "SELECT ticker FROM ipos WHERE ipo_id = ?";
            String ticker = "";
            PreparedStatement ps1 = null;
            ResultSet rs1 = null;
            try {
                ps1 = con.prepareStatement(getTickerSql);
                ps1.setInt(1, ipoId);
                rs1 = ps1.executeQuery();
                if (rs1.next()) {
                    ticker = rs1.getString("ticker");
                } else {
                    throw new SQLException("IPO not found");
                }
            } finally {
                if (rs1 != null) { try { rs1.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
                if (ps1 != null) { try { ps1.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            // 1. List stock publicly
            String updateStockSql = "UPDATE stocks SET is_listed = TRUE WHERE ticker = ?";
            PreparedStatement ps2 = null;
            try {
                ps2 = con.prepareStatement(updateStockSql);
                ps2.setString(1, ticker);
                ps2.executeUpdate();
            } finally {
                if (ps2 != null) { try { ps2.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            // 2. Set IPO status to LISTED
            String updateIpoSql = "UPDATE ipos SET status = ? WHERE ipo_id = ?";
            PreparedStatement ps3 = null;
            try {
                ps3 = con.prepareStatement(updateIpoSql);
                ps3.setString(1, IPOStatus.LISTED.name());
                ps3.setInt(2, ipoId);
                ps3.executeUpdate();
            } finally {
                if (ps3 != null) { try { ps3.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            }

            con.commit();
        } catch (SQLException e) {
            System.err.println("Error listing stock: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { System.err.println("Rollback failed: " + ex.getMessage()); }
            }
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
            }
        }
    }

    public static IPO getIPO(int ipoId) {
        String sql = "SELECT * FROM ipos WHERE ipo_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DatabaseManager.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, ipoId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return new IPO(
                    rs.getInt("ipo_id"),
                    rs.getString("ticker"),
                    rs.getString("company_name"),
                    rs.getInt("sector_id"),
                    rs.getDouble("ipo_price"),
                    rs.getLong("total_shares"),
                    rs.getLong("shares_remaining"),
                    rs.getTimestamp("open_time"),
                    rs.getTimestamp("close_time"),
                    IPOStatus.valueOf(rs.getString("status"))
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting IPO: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (con != null) { try { con.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
        return null;
    }

    public static class IPOApplicationInfo {
        public final int appId;
        public final int ipoId;
        public final String ticker;
        public final String companyName;
        public final double ipoPrice;
        public final int appliedQty;
        public final int allottedQty;
        public final String status;
        
        public IPOApplicationInfo(int appId, int ipoId, String ticker, String companyName, double ipoPrice, int appliedQty, int allottedQty, String status) {
            this.appId = appId;
            this.ipoId = ipoId;
            this.ticker = ticker;
            this.companyName = companyName;
            this.ipoPrice = ipoPrice;
            this.appliedQty = appliedQty;
            this.allottedQty = allottedQty;
            this.status = status;
        }
    }

    public static ds.CustomLinkedList getOpenIPOs() {
        ds.CustomLinkedList openIpos = new ds.CustomLinkedList();
        String sql = "SELECT * FROM ipos WHERE status = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DatabaseManager.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, IPOStatus.OPEN.name());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                openIpos.addLast(new IPO(
                    rs.getInt("ipo_id"),
                    rs.getString("ticker"),
                    rs.getString("company_name"),
                    rs.getInt("sector_id"),
                    rs.getDouble("ipo_price"),
                    rs.getLong("total_shares"),
                    rs.getLong("shares_remaining"),
                    rs.getTimestamp("open_time"),
                    rs.getTimestamp("close_time"),
                    IPOStatus.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching open IPOs: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { } }
            if (con != null) { try { con.close(); } catch (SQLException e) { } }
        }
        return openIpos;
    }

    public static ds.CustomLinkedList getUserIPOApplications(int userId) {
        ds.CustomLinkedList apps = new ds.CustomLinkedList();
        String sql = "SELECT a.app_id, a.ipo_id, i.ticker, i.company_name, i.ipo_price, a.applied_qty, a.allotted_qty, i.status " +
                     "FROM ipo_applications a " +
                     "JOIN ipos i ON a.ipo_id = i.ipo_id " +
                     "WHERE a.user_id = ? " +
                     "ORDER BY a.app_id DESC";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DatabaseManager.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                apps.addLast(new IPOApplicationInfo(
                    rs.getInt("app_id"),
                    rs.getInt("ipo_id"),
                    rs.getString("ticker"),
                    rs.getString("company_name"),
                    rs.getDouble("ipo_price"),
                    rs.getInt("applied_qty"),
                    rs.getInt("allotted_qty"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user IPO applications: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { } }
            if (con != null) { try { con.close(); } catch (SQLException e) { } }
        }
        return apps;
    }
}
