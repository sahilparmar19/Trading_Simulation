package admin;

import db.DatabaseManager;
import db.DividendManager;
import db.IPOManager;
import ds.CustomLinkedList;
import io.ReportGenerator;
import model.IPO;
import model.Stock;

import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class AdminPanel {

    public static void render(Scanner sc) {
        while (true) {
            System.out.println("\n=================================================================");
            System.out.println("                         ADMIN CONTROL PANEL");
            System.out.println("=================================================================");
            System.out.println(" [1] Declare Share Dividend");
            System.out.println(" [2] Simulate IPO Process");
            System.out.println(" [3] View All Registered Users & Balances");
            System.out.println(" [4] Freeze/Delist Stock");
            System.out.println(" [5] Go Back / Logout");
            System.out.print(" Choose option: ");
            String choice = sc.nextLine().trim();

            if (choice.equals("5")) {
                break;
            }

            switch (choice) {
                case "1":
                    declareDividendFlow(sc);
                    break;
                case "2":
                    ipoSimulationFlow(sc);
                    break;
                case "3":
                    viewAllUsersFlow();
                    break;
                case "4":
                    freezeStockFlow(sc);
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private static void declareDividendFlow(Scanner sc) {
        System.out.println("\n--- DECLARE DIVIDEND ---");
        System.out.print(" Enter Stock Ticker: ");
        String ticker = sc.nextLine().trim().toUpperCase();
        Stock stock = DatabaseManager.getStock(ticker);
        if (stock == null) {
            System.out.println("Stock not found with ticker: " + ticker);
            return;
        }

        System.out.print(" Enter Dividend Amount per Share (INR): ");
        double amount;
        try {
            amount = Double.parseDouble(sc.nextLine().trim());
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid dividend amount.");
            return;
        }

        // Declare dividend in DB
        int divId = DividendManager.declareDividend(ticker, amount);
        if (divId != -1) {
            System.out.println("Dividend declared successfully. ID: " + divId);
            System.out.print(" Do you want to pay shareholders now? (Y/N): ");
            String payChoice = sc.nextLine().trim().toUpperCase();
            if (payChoice.equals("Y")) {
                boolean success = DividendManager.payAllShareholders(divId);
                if (success) {
                    System.out.println("Dividends paid successfully to all shareholders.");
                    String reportPath = ReportGenerator.exportDividendReport(divId);
                    if (reportPath != null) {
                        System.out.println("Dividend Payout report generated at: " + reportPath);
                    }
                } else {
                    System.out.println("Failed to pay dividends.");
                }
            }
        } else {
            System.out.println("Failed to declare dividend.");
        }
    }

    private static void ipoSimulationFlow(Scanner sc) {
        while (true) {
            System.out.println("\n--- IPO SIMULATION ENGINE ---");
            System.out.println(" [1] Create New IPO (Upcoming)");
            System.out.println(" [2] Open IPO for Subscription");
            System.out.println(" [3] Close Subscription & Process Allotment");
            System.out.println(" [4] List Stock Publicly (Go Live)");
            System.out.println(" [5] Go Back");
            System.out.print(" Choose option: ");
            String choice = sc.nextLine().trim();

            if (choice.equals("5")) {
                break;
            }

            switch (choice) {
                case "1":
                    createIPOFlow(sc);
                    break;
                case "2":
                    openIPOFlow(sc);
                    break;
                case "3":
                    allotIPOFlow(sc);
                    break;
                case "4":
                    listIPOFlow(sc);
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private static void createIPOFlow(Scanner sc) {
        System.out.println("\n--- CREATE IPO ---");
        System.out.print(" Company Name: ");
        String name = sc.nextLine().trim();
        System.out.print(" Ticker (e.g. INFY): ");
        String ticker = sc.nextLine().trim().toUpperCase();
        
        System.out.println(" Available Sectors:");
        System.out.println("  1. Energy  2. IT  3. Banking  4. Automobile  5. Pharmaceuticals");
        System.out.print(" Select Sector ID (1-5): ");
        int sectorId;
        try {
            sectorId = Integer.parseInt(sc.nextLine().trim());
            if (sectorId < 1 || sectorId > 5) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid sector ID.");
            return;
        }

        System.out.print(" IPO Price (INR): ");
        double price;
        try {
            price = Double.parseDouble(sc.nextLine().trim());
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid price.");
            return;
        }

        System.out.print(" Total Shares Offered: ");
        long shares;
        try {
            shares = Long.parseLong(sc.nextLine().trim());
            if (shares <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid share count.");
            return;
        }

        // Set subscription window: starts now, closes in 5 minutes for easy testing
        Timestamp openTime = new Timestamp(System.currentTimeMillis());
        Timestamp closeTime = new Timestamp(System.currentTimeMillis() + (5 * 60 * 1000L)); // 5 mins later

        int ipoId = IPOManager.createIPO(name, ticker, sectorId, price, shares, openTime, closeTime);
        if (ipoId != -1) {
            System.out.println("IPO created successfully with ID: " + ipoId + ". Status is UPCOMING.");
            System.out.println("Subscription window is open from: " + openTime + " to " + closeTime);
        } else {
            System.out.println("Failed to create IPO.");
        }
    }

    private static void openIPOFlow(Scanner sc) {
        System.out.print(" Enter IPO ID to open for subscription: ");
        int ipoId;
        try {
            ipoId = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid IPO ID.");
            return;
        }

        IPO ipo = IPOManager.getIPO(ipoId);
        if (ipo == null) {
            System.out.println("IPO not found.");
            return;
        }

        IPOManager.openIPO(ipoId);
        System.out.println("IPO " + ipo.getTicker() + " is now OPEN for user subscription applications!");
    }

    private static void allotIPOFlow(Scanner sc) {
        System.out.print(" Enter IPO ID to process allotment: ");
        int ipoId;
        try {
            ipoId = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid IPO ID.");
            return;
        }

        boolean success = IPOManager.processAllotment(ipoId);
        if (success) {
            System.out.println("IPO Allotment processed successfully. Refunds credited and holdings added.");
            String reportPath = ReportGenerator.exportIPOAllotment(ipoId);
            if (reportPath != null) {
                System.out.println("IPO Allotment report generated at: " + reportPath);
            }
        } else {
            System.out.println("IPO Allotment failed.");
        }
    }

    private static void listIPOFlow(Scanner sc) {
        System.out.print(" Enter IPO ID to list stock on the exchange: ");
        int ipoId;
        try {
            ipoId = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid IPO ID.");
            return;
        }

        IPOManager.listStock(ipoId);
        System.out.println("IPO completed. Stock is now LISTED and open for trade matching!");
    }

    private static void viewAllUsersFlow() {
        System.out.println("\n--- REGISTERED USERS & BALANCES ---");
        String sql = "SELECT user_id, username, name, balance, is_admin FROM users ORDER BY user_id ASC";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DatabaseManager.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            System.out.printf("%-8s | %-12s | %-20s | %-12s | %-8s%n", "User ID", "Username", "Name", "Balance (INR)", "Admin?");
            System.out.println("---------------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-8d | %-12s | %-20s | ₹%-11.2f | %-8b%n",
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getDouble("balance"),
                        rs.getBoolean("is_admin"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users list: " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); } }
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (con != null) { try { con.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
    }

    private static void freezeStockFlow(Scanner sc) {
        System.out.println("\n--- FREEZE/DELIST STOCK ---");
        System.out.print(" Enter stock ticker to change listing status: ");
        String ticker = sc.nextLine().trim().toUpperCase();
        Stock stock = DatabaseManager.getStock(ticker);
        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }

        System.out.printf(" Current status of %s: Listed = %b%n", ticker, stock.isListed());
        System.out.print(" Set listed status (true = LISTED, false = FROZEN/DELISTED): ");
        boolean status = Boolean.parseBoolean(sc.nextLine().trim());

        String sql = "UPDATE stocks SET is_listed = ? WHERE ticker = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DatabaseManager.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setBoolean(1, status);
            pstmt.setString(2, ticker);
            pstmt.executeUpdate();
            System.out.println("Listing status updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating stock listing status: " + e.getMessage());
        } finally {
            if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); } }
            if (con != null) { try { con.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); } }
        }
    }
}
