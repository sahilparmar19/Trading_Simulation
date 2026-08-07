import admin.AdminPanel;
import auth.AuthManager;
import auth.Session;
import db.DatabaseManager;
import db.MarketInitializationService;
import display.MarketDisplay;
import display.PortfolioView;
import display.StockDetailView;
import display.WatchlistView;
import ds.CustomLinkedList;
import engine.BotTrader;
import engine.MatchingEngine;
import engine.OrderBook;
import engine.StopLossMonitor;
import io.ReportGenerator;
import model.Order;
import model.OrderStatus;
import model.OrderType;
import model.Stock;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("               TRADING SIMULATION ENGINE");
        System.out.println("=================================================");

        // 0. Seed initial market prices from Yahoo Finance (startup only).
        //    After this returns, MatchingEngine is the sole price authority.
        MarketInitializationService.initialize();

        // 1. Load pending orders from database into in-memory BSTs
        loadPendingOrders();

        // 2. Start core trading engine background threads
        System.out.println("Initializing background threads...");

        MatchingEngine matchingEngine = new MatchingEngine();
        matchingEngine.start();

        StopLossMonitor stopLossMonitor = new StopLossMonitor();
        stopLossMonitor.start();


        // Start 3 BotTrader threads
        BotTrader bot1 = new BotTrader("bot1");
        BotTrader bot2 = new BotTrader("bot2");
        BotTrader bot3 = new BotTrader("bot3");
        bot1.start();
        bot2.start();
        bot3.start();

        System.out.println("Simulation engine fully running.");

        Scanner sc = new Scanner(System.in);
        try {
            while (true) {
                if (!Session.isLoggedIn()) {
                    showAuthMenu(sc);
                } else {
                    User user = Session.getCurrentUser();
                    if (user.isAdmin()) {
                        showAdminMenu(sc);
                    } else {
                        showUserMenu(sc);
                    }
                }
            }
        } finally {
            // Shutdown threads gracefully on exit
            System.out.println("Stopping background threads...");
            matchingEngine.shutdown();
            stopLossMonitor.shutdown();
            bot1.shutdown();
            bot2.shutdown();
            bot3.shutdown();
            sc.close();
            System.out.println("Goodbye.");
        }
    }

    private static void loadPendingOrders() {
        String sql = "SELECT * FROM orders WHERE status = ?";
        int count = 0;
        try (Connection con = DatabaseManager.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, OrderStatus.PENDING.name());
            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Order order = new Order(
                            rs.getInt("order_id"),
                            rs.getInt("user_id"),
                            rs.getString("ticker"),
                            rs.getBoolean("is_buy"),
                            OrderType.valueOf(rs.getString("order_type")),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getDouble("stop_price"),
                            rs.getTimestamp("timestamp"),
                            OrderStatus.valueOf(rs.getString("status")));

                    OrderBook book = OrderBook.get(order.getTicker());
                    synchronized (book) {
                        if (order.isBuy()) {
                            book.getBuySide().insert(order);
                        } else {
                            book.getSellSide().insert(order);
                        }
                    }
                    count++;
                }
            }
            System.out.println("Loaded " + count + " pending orders from database into memory order books.");
        } catch (SQLException e) {
            System.err.println("Error loading pending orders from DB: " + e.getMessage());
        }
    }

    private static void showAuthMenu(Scanner sc) {
        System.out.println("\n--- MAIN AUTH MENU ---");
        System.out.println(" [1] Log In (Regular User)");
        System.out.println(" [2] Sign Up (New User)");
        System.out.println(" [3] Admin Log In");
        System.out.println(" [4] Exit");
        System.out.print(" Select option: ");
        String choice = sc.nextLine().trim();

        if (choice.equals("4")) {
            System.out.println("Exiting application.");
            System.exit(0);
        }

        switch (choice) {
            case "1":
                System.out.print(" Username: ");
                String user = sc.nextLine().trim();
                System.out.print(" Password: ");
                String pass = sc.nextLine().trim();
                User loggedUser = AuthManager.login(user, pass);
                if (loggedUser != null && !loggedUser.isAdmin()) {
                    Session.login(loggedUser);
                    System.out.println("Logged in successfully. Welcome " + loggedUser.getName() + "!");
                } else {
                    System.out.println("Invalid credentials or Admin account.");
                }
                break;
            case "2":
                System.out.print(" Choose Username: ");
                String regUser = sc.nextLine().trim();
                System.out.print(" Choose Password: ");
                String regPass = sc.nextLine().trim();
                System.out.print(" Enter Full Name: ");
                String regName = sc.nextLine().trim();
                boolean success = AuthManager.signUp(regUser, regPass, regName);
                if (success) {
                    System.out.println("Signed up successfully! You can now log in.");
                } else {
                    System.out.println("Sign up failed. Please check the error above and try again.");
                }
                break;
            case "3":
                System.out.print(" Admin Username: ");
                String adminUser = sc.nextLine().trim();
                System.out.print(" Admin Password: ");
                String adminPass = sc.nextLine().trim();
                User loggedAdmin = AuthManager.adminLogin(adminUser, adminPass);
                if (loggedAdmin != null) {
                    Session.login(loggedAdmin);
                    System.out.println("Admin login successful. Access granted to Admin Panel.");
                } else {
                    System.out.println("Invalid Admin credentials.");
                }
                break;
            default:
                System.out.println("Invalid selection.");
                break;
        }
    }

    private static void showUserMenu(Scanner sc) {
        User u = Session.getCurrentUser();
        System.out.println("\n--- USER CONSOLE ---");
        System.out.println(" [1] View Portfolio & Balance");
        System.out.println(" [2] View Watchlist");
        System.out.println(" [3] Search Stocks");
        System.out.println(" [4] Market Overview (Top Gainers/Losers/Sectors)");
        System.out.println(" [5] Place Buy Order");
        System.out.println(" [6] Place Sell Order");
        System.out.println(" [7] Set Stop-Loss Order");
        System.out.println(" [8] Export Transaction & Portfolio Report");
        System.out.println(" [9] View Live Stock Prices");
        System.out.println("[10] View Limit Orders");
        System.out.println("[11] Log Out");
        System.out.print(" Choose option: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                PortfolioView.render(sc);
                break;
            case "2":
                WatchlistView.render(sc);
                break;
            case "3":
                StockDetailView.searchStocksMenu(sc);
                break;
            case "4":
                MarketDisplay.showTopGainers();
                MarketDisplay.showTopLosers();
                MarketDisplay.showSectorPnL();
                break;
            case "5":
                placeOrderFlow(true, sc);
                break;
            case "6":
                placeOrderFlow(false, sc);
                break;
            case "7":
                placeStopLossFlow(sc);
                break;
            case "8":
                System.out.println("Exporting reports...");
                String tPath = ReportGenerator.exportTransactionHistory(u.getUserId());
                String pPath = ReportGenerator.exportPortfolio(u.getUserId());
                if (tPath != null && pPath != null) {
                    System.out.println("Reports exported successfully:");
                    System.out.println(" - Transaction History: " + tPath);
                    System.out.println(" - Current Portfolio:   " + pPath);
                } else {
                    System.out.println("Failed to export some reports.");
                }
                break;
            case "9":
                MarketDisplay.showLiveStocksBySector(sc);
                break;
            case "10":
                viewLimitOrders();
                break;
            case "11":
                Session.logout();
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid selection.");
                break;
        }
    }

    private static void showAdminMenu(Scanner sc) {
        System.out.println("\n--- ADMIN CONSOLE ---");
        System.out.println(" [1] Admin Panel (Dividends, IPOs, Users)");
        System.out.println(" [2] Market Overview");
        System.out.println(" [3] Log Out");
        System.out.print(" Choose option: ");
        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1":
                AdminPanel.render(sc);
                break;
            case "2":
                MarketDisplay.showTopGainers();
                MarketDisplay.showTopLosers();
                MarketDisplay.showSectorPnL();
                break;
            case "3":
                Session.logout();
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid selection.");
                break;
        }
    }

    /**
     * Walks the user through sector â†’ stock selection.
     * Returns the selected Stock, or null if the user chose to go back.
     */
    private static Stock selectStockFromSectors(Scanner sc) {
        // Display all sectors
        CustomLinkedList sectors = DatabaseManager.getAllSectors();
        if (sectors.size() == 0) {
            System.out.println("No sectors found.");
            return null;
        }

        System.out.println(" Select a Sector:");
        for (int i = 0; i < sectors.size(); i++) {
            DatabaseManager.SectorInfo sec = (DatabaseManager.SectorInfo) sectors.get(i);
            System.out.printf("  [%d] %s%n", i + 1, sec.sectorName);
        }
        System.out.printf("  [%d] Go Back%n", sectors.size() + 1);
        System.out.print(" Choose option: ");
        String sectorChoice = sc.nextLine().trim();

        int sectorIndex;
        try {
            sectorIndex = Integer.parseInt(sectorChoice);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }

        if (sectorIndex == sectors.size() + 1) {
            return null;
        }
        if (sectorIndex < 1 || sectorIndex > sectors.size()) {
            System.out.println("Invalid selection.");
            return null;
        }

        DatabaseManager.SectorInfo selectedSector = (DatabaseManager.SectorInfo) sectors.get(sectorIndex - 1);

        // Display stocks in the selected sector
        CustomLinkedList stocks = DatabaseManager.getStocksBySectorId(selectedSector.sectorId);
        System.out.println("\n--- Stocks in " + selectedSector.sectorName + " ---");

        if (stocks.size() == 0) {
            System.out.println("No listed stocks found in this sector.");
            return null;
        }

        System.out.printf(" %-5s | %-10s | %-32s | %-14s | %-10s%n", "No.", "Ticker", "Company Name", "Current Price", "Change (%)");
        System.out.println(" ---------------------------------------------------------------------------------");
        for (int i = 0; i < stocks.size(); i++) {
            Stock s = (Stock) stocks.get(i);
            double change = 0.0;
            if (s.getPrevClose() > 0) {
                change = ((s.getCurrentPrice() - s.getPrevClose()) / s.getPrevClose()) * 100.0;
            }
            System.out.printf(" [%-3d] | %-10s | %-32s | INR %-9.2f | %+.2f%%%n",
                    i + 1, s.getTicker(), s.getCompanyName(), s.getCurrentPrice(), change);
        }
        System.out.printf(" [%d] Go Back%n", stocks.size() + 1);
        System.out.print(" Select stock: ");
        String stockChoice = sc.nextLine().trim();

        int stockIndex;
        try {
            stockIndex = Integer.parseInt(stockChoice);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }

        if (stockIndex == stocks.size() + 1) {
            return null;
        }
        if (stockIndex < 1 || stockIndex > stocks.size()) {
            System.out.println("Invalid selection.");
            return null;
        }

        return (Stock) stocks.get(stockIndex - 1);
    }

    private static void placeOrderFlow(boolean isBuy, Scanner sc) {
        System.out.println(isBuy ? "\n--- PLACE BUY ORDER ---" : "\n--- PLACE SELL ORDER ---");

        // Sector â†’ Stock selection
        Stock stock = selectStockFromSectors(sc);
        if (stock == null) {
            return;
        }
        String ticker = stock.getTicker();

        if (!stock.isListed()) {
            System.out.println("Stock is delisted.");
            return;
        }

        System.out.print(" Enter Quantity: ");
        int qty;
        try {
            qty = Integer.parseInt(sc.nextLine().trim());
            if (qty <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }

        if (!isBuy) {
            CustomLinkedList portfolio = DatabaseManager
                    .getPortfolio(Session.getCurrentUser().getUserId());
            int ownedQty = 0;
            for (int i = 0; i < portfolio.size(); i++) {
                DatabaseManager.PortfolioHolding holding = (DatabaseManager.PortfolioHolding) portfolio.get(i);
                if (holding.ticker.equalsIgnoreCase(ticker)) {
                    ownedQty = holding.quantity;
                    break;
                }
            }
            if (ownedQty < qty) {
                System.out.println("You do not hold enough shares of " + ticker + " to sell. Owned: " + ownedQty
                        + ", Requested: " + qty);
                return;
            }
        }

        System.out.println(" Order Type:");
        System.out.println("  [1] LIMIT Order");
        System.out.println("  [2] MARKET Order");
        System.out.print(" Select (1-2): ");
        String typeChoice = sc.nextLine().trim();

        OrderType type = OrderType.LIMIT;
        double price = 0.0;

        if (typeChoice.equals("1")) {
            type = OrderType.LIMIT;
            // Show current market price before asking for limit price
            System.out.printf(" Current Market Price: INR %.2f%n", stock.getCurrentPrice());
            System.out.print(" Enter Limit Price (INR): ");
            try {
                price = Double.parseDouble(sc.nextLine().trim());
                if (price <= 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.out.println("Invalid price.");
                return;
            }
        } else if (typeChoice.equals("2")) {
            type = OrderType.MARKET;

            // Market orders use sentinel prices for matching priority
            price = isBuy ? 9999999.99 : 0.0;
        } else {
            System.out.println("Invalid option.");
            return;
        }

        Order order = new Order(
                0,
                Session.getCurrentUser().getUserId(),
                ticker,
                isBuy,
                type,
                price,
                qty,
                0.0,
                new Timestamp(System.currentTimeMillis()),
                OrderStatus.PENDING);

        int id = DatabaseManager.insertOrder(order);
        if (id != -1) {
            OrderBook book = OrderBook.get(ticker);
            synchronized (book) {
                if (isBuy) {
                    book.getBuySide().insert(order);
                } else {
                    book.getSellSide().insert(order);
                }
            }
            System.out.println("Order submitted successfully! Order ID: " + id);
        } else {
            System.out.println("Order submission failed.");
        }
    }

    private static void placeStopLossFlow(Scanner sc) {
        int userId = Session.getCurrentUser().getUserId();
        System.out.println("\n--- SET STOP-LOSS ORDER ---");
        System.out.print(" Enter Stock Ticker: ");
        String ticker = sc.nextLine().trim().toUpperCase();
        Stock stock = DatabaseManager.getStock(ticker);
        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }
       // Check if user owns the stock
        CustomLinkedList portfolio = DatabaseManager.getPortfolio(userId);
        int ownedQty = 0;
        for (int i = 0; i < portfolio.size(); i++) {
            DatabaseManager.PortfolioHolding holding = (DatabaseManager.PortfolioHolding) portfolio.get(i);
            if (holding.ticker.equalsIgnoreCase(ticker)) {
                ownedQty = holding.quantity;
                break;
            }
        }

        if (ownedQty <= 0) {
            System.out.println("You do not hold any shares of " + ticker + ".");
            return;
        }

        System.out.print(" Enter Quantity (max " + ownedQty + "): ");
        int qty;
        try {
            qty = Integer.parseInt(sc.nextLine().trim());
            if (qty <= 0 || qty > ownedQty)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }

        System.out.printf(" Current Market Price: INR %.2f%n", stock.getCurrentPrice());
        System.out.print(" Enter Stop-Loss Trigger Price (INR): ");
        double stopPrice;
        try {
            stopPrice = Double.parseDouble(sc.nextLine().trim());
            if (stopPrice <= 0 || stopPrice >= stock.getCurrentPrice()) {
                System.out.println("Stop price must be positive and less than current market price (INR "
                        + stock.getCurrentPrice() + ").");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid price.");
            return;
        }

        String sql = "INSERT INTO stop_loss_orders (user_id, ticker, quantity, stop_price, status, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection con = DatabaseManager.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, ticker);
            pstmt.setInt(3, qty);
            pstmt.setDouble(4, stopPrice);
            pstmt.setString(5, model.StopLossStatus.ACTIVE.name());
            pstmt.executeUpdate();
            System.out.println("Stop-loss order set successfully at INR " + stopPrice);
        } catch (SQLException e) {
            System.err.println("Error setting stop-loss: " + e.getMessage());
        }
    }

    private static void viewLimitOrders() {
        int userId = Session.getCurrentUser().getUserId();
        System.out.println("\n--- YOUR PENDING LIMIT ORDERS ---");

        CustomLinkedList orders = DatabaseManager.getPendingLimitOrders(userId);
        if (orders.size() == 0) {
            System.out.println("No pending limit orders found.");
            return;
        }

        System.out.printf(" %-10s | %-10s | %-8s | %-14s | %-10s | %-20s%n",
                "Order ID", "Ticker", "Side", "Limit Price", "Quantity", "Placed At");
        System.out.println(" ------------------------------------------------------------------------------------");
        for (int i = 0; i < orders.size(); i++) {
            Order o = (Order) orders.get(i);
            String side = o.isBuy() ? "BUY" : "SELL";
            Stock stock = DatabaseManager.getStock(o.getTicker());
            System.out.printf(" %-10d | %-10s | %-8s | INR %-9.2f | %-10d | %s%n",
                    o.getOrderId(), o.getTicker(), side, o.getPrice(), o.getQuantity(),
                    o.getTimestamp().toString());
        }

        // Show a summary with current prices
        System.out.println("\n Current Market Prices:");
        for (int i = 0; i < orders.size(); i++) {
            Order o = (Order) orders.get(i);
            Stock stock = DatabaseManager.getStock(o.getTicker());
            if (stock != null) {
                double diff = o.isBuy()
                        ? stock.getCurrentPrice() - o.getPrice()
                        : o.getPrice() - stock.getCurrentPrice();
                String status = diff > 0 ? "(Above limit)" : diff < 0 ? "(Below limit)" : "(At limit)";
                System.out.printf("   %s: INR %.2f %s%n", o.getTicker(), stock.getCurrentPrice(), status);
            }
        }
    }
}
