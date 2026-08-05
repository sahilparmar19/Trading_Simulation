package display;

import db.DatabaseManager;
import ds.CustomLinkedList;
import model.Stock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class MarketDisplay {

    public static void showTopGainers() {
        System.out.println("\n--- TOP 5 GAINERS ---");
        CustomLinkedList gainers = DatabaseManager.getTopGainers(5);
        if (gainers.size() == 0) {
            System.out.println("No data available.");
            return;
        }
        printStocksTable(gainers);
    }

    public static void showTopLosers() {
        System.out.println("\n--- TOP 5 LOSERS ---");
        CustomLinkedList losers = DatabaseManager.getTopLosers(5);
        if (losers.size() == 0) {
            System.out.println("No data available.");
            return;
        }
        printStocksTable(losers);
    }

    public static void showSectorPnL() {
        System.out.println("\n--- SECTOR-WISE P&L ---");
        CustomLinkedList list = DatabaseManager.getSectorPnL();
        if (list.size() == 0) {
            System.out.println("No holdings present in any sector.");
            return;
        }
        System.out.printf("%-18s | %-15s | %-12s%n", "Sector Name", "Avg P&L (INR)", "Avg P&L (%)");
        System.out.println("-------------------------------------------------------");
        for (int i = 0; i < list.size(); i++) {
            DatabaseManager.SectorPnL item = (DatabaseManager.SectorPnL) list.get(i);
            System.out.printf("%-18s | INR %-11.2f | %-10.2f%%%n",
                    item.sectorName, item.avgPnL, item.avgPnLPct);
        }
    }

    public static void showStocksBySector(Scanner sc) {
        System.out.println("\n--- VIEW STOCKS BY SECTOR ---");

        CustomLinkedList sectors = DatabaseManager.getAllSectors();
        if (sectors.size() == 0) {
            System.out.println("No sectors found.");
            return;
        }

        System.out.println(" Available Sectors:");
        for (int i = 0; i < sectors.size(); i++) {
            DatabaseManager.SectorInfo sec = (DatabaseManager.SectorInfo) sectors.get(i);
            System.out.printf("  [%d] %s%n", sec.sectorId, sec.sectorName);
        }
        System.out.print(" Enter Sector ID: ");
        int sectorId;
        try {
            sectorId = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        // Find the sector name for display
        String sectorName = null;
        for (int i = 0; i < sectors.size(); i++) {
            DatabaseManager.SectorInfo sec = (DatabaseManager.SectorInfo) sectors.get(i);
            if (sec.sectorId == sectorId) {
                sectorName = sec.sectorName;
                break;
            }
        }
        if (sectorName == null) {
            System.out.println("Sector ID not found.");
            return;
        }

        CustomLinkedList stocks = DatabaseManager.getStocksBySectorId(sectorId);
        System.out.println("\n--- Stocks in Sector: " + sectorName + " ---");
        if (stocks.size() == 0) {
            System.out.println("No listed stocks found in this sector.");
            return;
        }
        printStocksTable(stocks);
    }

    /**
     * Shows live stock prices for a selected sector, refreshing every 3 seconds.
     * User can press Enter to return to the previous menu.
     */
    public static void showLiveStocksBySector(Scanner sc) {
        System.out.println("\n--- VIEW LIVE STOCK PRICES BY SECTOR ---");

        CustomLinkedList sectors = DatabaseManager.getAllSectors();
        if (sectors.size() == 0) {
            System.out.println("No sectors found.");
            return;
        }

        System.out.println(" Available Sectors:");
        for (int i = 0; i < sectors.size(); i++) {
            DatabaseManager.SectorInfo sec = (DatabaseManager.SectorInfo) sectors.get(i);
            System.out.printf("  [%d] %s%n", sec.sectorId, sec.sectorName);
        }
        System.out.print(" Enter Sector ID: ");
        int sectorId;
        try {
            sectorId = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        // Find sector name
        String sectorName = null;
        for (int i = 0; i < sectors.size(); i++) {
            DatabaseManager.SectorInfo sec = (DatabaseManager.SectorInfo) sectors.get(i);
            if (sec.sectorId == sectorId) {
                sectorName = sec.sectorName;
                break;
            }
        }
        if (sectorName == null) {
            System.out.println("Sector ID not found.");
            return;
        }

        final int finalSectorId = sectorId;
        final String finalSectorName = sectorName;

        // Flag — set to true when user presses Enter
        final boolean[] exitRequested = { false };

        // Background thread: waits for user to press Enter, then signals exit
        Thread inputThread = new Thread(new Runnable() {
            public void run() {
                try {
                    sc.nextLine(); // blocks until Enter is pressed
                } catch (Exception ignored) {
                }
                exitRequested[0] = true;
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        System.out.println("\n[LIVE] Prices refresh every 3 seconds. Press ENTER to go back.\n");

        while (!exitRequested[0]) {
            CustomLinkedList stocks = DatabaseManager.getStocksBySectorId(finalSectorId);

            System.out.println("=======================================================================================");
            System.out.printf(" LIVE PRICES  |  Sector: %-30s |  Updated: %s%n",
                    finalSectorName, sdf.format(new Date()));
            System.out.println("=======================================================================================");

            if (stocks.size() == 0) {
                System.out.println(" No listed stocks found in this sector.");
            } else {
                System.out.printf("%-12s | %-32s | %-14s | %-12s | %-10s%n",
                        "Ticker", "Company Name", "Live Price", "Prev Close", "Change (%)");
                System.out.println("---------------------------------------------------------------------------------------");
                for (int i = 0; i < stocks.size(); i++) {
                    Stock stock = (Stock) stocks.get(i);
                    double change = 0.0;
                    if (stock.getPrevClose() > 0) {
                        change = ((stock.getCurrentPrice() - stock.getPrevClose()) / stock.getPrevClose()) * 100.0;
                    }
                    String arrow = change >= 0 ? "▲" : "▼";
                    System.out.printf("%-12s | %-32s | ₹%-13.2f | ₹%-11.2f | %s %+.2f%%%n",
                            stock.getTicker(), stock.getCompanyName(),
                            stock.getCurrentPrice(), stock.getPrevClose(),
                            arrow, change);
                }
            }
            System.out.println();
            System.out.println(" Press ENTER to go back to the menu.");

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                break;
            }

            if (!exitRequested[0]) {
                int stockCount = stocks.size() == 0 ? 1 : stocks.size();
                int linesToClear = 5 + stockCount + 2;
                for (int i = 0; i < linesToClear; i++) {
                    System.out.print("\033[1A\033[2K");
                }
            }
        }

        System.out.println("\nReturning to menu...");
    }

    private static void printStocksTable(CustomLinkedList stocks) {
        System.out.printf("%-12s | %-32s | %-12s | %-12s | %-10s%n",
                "Ticker", "Company Name", "Current Price", "Prev Close", "Change (%)");
        System.out.println("---------------------------------------------------------------------------------------------");
        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = (Stock) stocks.get(i);
            double change = 0.0;
            if (stock.getPrevClose() > 0) {
                change = ((stock.getCurrentPrice() - stock.getPrevClose()) / stock.getPrevClose()) * 100.0;
            }
            System.out.printf("%-12s | %-32s | ₹%-11.2f | ₹%-11.2f | %+.2f%%%n",
                    stock.getTicker(), stock.getCompanyName(),
                    stock.getCurrentPrice(), stock.getPrevClose(), change);
        }
    }
}
