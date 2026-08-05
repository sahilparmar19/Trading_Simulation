package display;

import db.DatabaseManager;
import ds.CustomLinkedList;
import model.PriceHistory;
import model.Stock;

import java.util.Scanner;

public class StockDetailView {

    public static void render(String ticker, Scanner scanner) {
        Stock stock = DatabaseManager.getStock(ticker);
        if (stock == null) {
            System.out.println("Stock not found with ticker: " + ticker);
            return;
        }

        while (true) {
            System.out.println("\n=================================================================");
            System.out.printf(" %s (%s) [%s]%n", stock.getCompanyName().toUpperCase(), stock.getTicker(), stock.getExchange());
            System.out.println("=================================================================");
            System.out.printf(" Price:        ₹%-12.2f | Market Cap:  ₹%.2f Cr%n", stock.getCurrentPrice(), stock.getMarketCap() / 10000000.0);
            System.out.printf(" PE Ratio:     %-12.2f | P/B Ratio:   %-12.2f%n", stock.getPeRatio(), stock.getPbRatio());
            System.out.printf(" ROE:          %-11.2f%% | ROA:         %-11.2f%%%n", stock.getRoe() * 100.0, stock.getRoa() * 100.0);
            System.out.println("-----------------------------------------------------------------");
            System.out.println(" Share Holding Pattern:");
            System.out.printf("   Promoters:    %-10.2f%% | Institutions: %-10.2f%% | Retail: %-10.2f%%%n",
                    stock.getPromoterHold(), stock.getInstHold(), stock.getRetailHold());
            System.out.println("=================================================================");
            System.out.println(" PRICE HISTORY & TRENDS");
            System.out.println(" [1] 7 Days");
            System.out.println(" [2] 1 Month");
            System.out.println(" [3] 6 Months");
            System.out.println(" [4] 52 Week High/Low");
            System.out.println(" [5] Go Back");
            System.out.print(" Select option: ");

            String choice = scanner.nextLine().trim();
            if (choice.equals("5")) {
                break;
            }

            String period = "";
            switch (choice) {
                case "1": period = "7D"; break;
                case "2": period = "1M"; break;
                case "3": period = "6M"; break;
                case "4": period = "52W"; break;
                default:
                    System.out.println("Invalid option. Try again.");
                    continue;
            }

            showHistoryAndTrend(stock.getTicker(), period);
        }
    }

    private static void showHistoryAndTrend(String ticker, String period) {
        CustomLinkedList history = DatabaseManager.getPriceHistory(ticker, period);
        if (history.size() == 0) {
            System.out.println("No price history records found for this period.");
            return;
        }

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        PriceHistory first = (PriceHistory) history.get(0);
        PriceHistory last  = (PriceHistory) history.get(history.size() - 1);
        double firstPrice = first.getPrice();
        double lastPrice  = last.getPrice();

        System.out.println("\n--- Historical Prices ---");
        for (int i = 0; i < history.size(); i++) {
            PriceHistory record = (PriceHistory) history.get(i);
            double price = record.getPrice();
            if (price < min) min = price;
            if (price > max) max = price;
            System.out.printf("  [%s] ₹%.2f%n", record.getRecordedAt().toString(), price);
        }

        double diff = lastPrice - firstPrice;
        double diffPct = (diff / firstPrice) * 100.0;
        String trend = "FLAT";
        if (diff > 0.01) {
            trend = "UPWARD (\u2197)";
        } else if (diff < -0.01) {
            trend = "DOWNWARD (\u2198)";
        }

        System.out.println("-------------------------");
        System.out.printf(" Min Price:  ₹%.2f%n", min);
        System.out.printf(" Max Price:  ₹%.2f%n", max);
        System.out.printf(" Performance: %+.2f%% (%s)%n", diffPct, trend);
        System.out.println("-------------------------");
    }

    public static void searchStocksMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- STOCK SEARCH ---");

            CustomLinkedList sectors = DatabaseManager.getAllSectors();
            if (sectors.size() == 0) {
                System.out.println("No sectors found.");
                return;
            }

            System.out.println(" Select a Sector:");
            for (int i = 0; i < sectors.size(); i++) {
                DatabaseManager.SectorInfo sec = (DatabaseManager.SectorInfo) sectors.get(i);
                System.out.printf("  [%d] %s%n", i + 1, sec.sectorName);
            }
            System.out.printf("  [%d] Go Back%n", sectors.size() + 1);
            System.out.print(" Choose option: ");
            String sectorChoice = scanner.nextLine().trim();

            int sectorIndex;
            try {
                sectorIndex = Integer.parseInt(sectorChoice);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                continue;
            }

            if (sectorIndex == sectors.size() + 1) {
                break;
            }
            if (sectorIndex < 1 || sectorIndex > sectors.size()) {
                System.out.println("Invalid selection.");
                continue;
            }

            DatabaseManager.SectorInfo selectedSector = (DatabaseManager.SectorInfo) sectors.get(sectorIndex - 1);

            // Display stocks in the selected sector
            while (true) {
                CustomLinkedList stocks = DatabaseManager.getStocksBySectorId(selectedSector.sectorId);
                System.out.println("\n--- Stocks in " + selectedSector.sectorName + " ---");

                if (stocks.size() == 0) {
                    System.out.println("No listed stocks found in this sector.");
                    break;
                }

                System.out.printf(" %-5s | %-10s | %-32s | %-14s | %-10s%n",
                        "No.", "Ticker", "Company Name", "Current Price", "Change (%)");
                System.out.println(" ---------------------------------------------------------------------------------");
                for (int i = 0; i < stocks.size(); i++) {
                    Stock s = (Stock) stocks.get(i);
                    double change = 0.0;
                    if (s.getPrevClose() > 0) {
                        change = ((s.getCurrentPrice() - s.getPrevClose()) / s.getPrevClose()) * 100.0;
                    }
                    System.out.printf(" [%-3d] | %-10s | %-32s | ₹%-13.2f | %+.2f%%%n",
                            i + 1, s.getTicker(), s.getCompanyName(), s.getCurrentPrice(), change);
                }
                System.out.printf(" [%d] Go Back%n", stocks.size() + 1);
                System.out.print(" Select stock: ");
                String stockChoice = scanner.nextLine().trim();

                int stockIndex;
                try {
                    stockIndex = Integer.parseInt(stockChoice);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                    continue;
                }

                if (stockIndex == stocks.size() + 1) {
                    break;
                }
                if (stockIndex < 1 || stockIndex > stocks.size()) {
                    System.out.println("Invalid selection.");
                    continue;
                }

                Stock selectedStock = (Stock) stocks.get(stockIndex - 1);
                render(selectedStock.getTicker(), scanner);
            }
        }
    }

    // Bubble sort stocks by price (sem 2 DS concept: sorting using array + swap)
    private static void sortStocks(CustomLinkedList list, boolean ascending) {
        int n = list.size();

        // Step 1: Copy linked list elements into a plain array using index-based get()
        Stock[] arr = new Stock[n];
        for (int i = 0; i < n; i++) {
            arr[i] = (Stock) list.get(i);
        }

        // Step 2: Bubble sort the array by current price
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                boolean swap = false;
                if (ascending && arr[j].getCurrentPrice() > arr[j + 1].getCurrentPrice()) {
                    swap = true;
                } else if (!ascending && arr[j].getCurrentPrice() < arr[j + 1].getCurrentPrice()) {
                    swap = true;
                }
                if (swap) {
                    Stock temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }

        // Step 3: Rebuild linked list in sorted order
        while (!list.isEmpty()) {
            list.remove(list.get(0));
        }
        for (int i = 0; i < arr.length; i++) {
            list.addLast(arr[i]);
        }
    }
}
