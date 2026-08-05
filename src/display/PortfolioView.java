package display;

import auth.Session;
import db.DatabaseManager;
import ds.CustomLinkedList;
import io.ReportGenerator;

import java.util.Scanner;

public class PortfolioView {

    public static void render(Scanner scanner) {
        if (!Session.isLoggedIn()) {
            System.out.println("Please log in first.");
            return;
        }

        int userId = Session.getCurrentUser().getUserId();
        double balance = DatabaseManager.getUserBalance(userId);
        CustomLinkedList holdings = DatabaseManager.getPortfolio(userId);

        System.out.println("\n=========================================================================");
        System.out.printf(" PORTFOLIO VIEW - User: %s%n", Session.getCurrentUser().getName());
        System.out.printf(" Available Balance: ₹%.2f%n", balance);
        System.out.println("=========================================================================");

        if (holdings.size() == 0) {
            System.out.println(" You do not hold any stocks currently.");
            System.out.println("=========================================================================");
        } else {
            System.out.printf("%-12s | %-28s | %-8s | %-12s | %-12s | %-12s | %-10s%n",
                    "Ticker", "Company Name", "Qty", "Avg Buy Price", "Current Price", "P&L (INR)", "P&L (%)");
            System.out.println("---------------------------------------------------------------------------------------------------------");
            double totalCostVal = 0;
            double totalCurrentVal = 0;
            double totalPnL = 0;

            for (int i = 0; i < holdings.size(); i++) {
                DatabaseManager.PortfolioHolding holding = (DatabaseManager.PortfolioHolding) holdings.get(i);
                totalCostVal += holding.avgBuyPrice * holding.quantity;
                totalCurrentVal += holding.currentPrice * holding.quantity;
                totalPnL += holding.getPnL();

                System.out.printf("%-12s | %-28s | %-8d | ₹%-11.2f | ₹%-11.2f | %+-11.2f | %+-9.2f%%%n",
                        holding.ticker, holding.companyName, holding.quantity,
                        holding.avgBuyPrice, holding.currentPrice,
                        holding.getPnL(), holding.getPnLPct());
            }

            double totalPnLPct = totalCostVal == 0 ? 0 : (totalPnL / totalCostVal) * 100.0;
            System.out.println("---------------------------------------------------------------------------------------------------------");
            System.out.printf(" Total Portfolio Cost Value:    ₹%.2f%n", totalCostVal);
            System.out.printf(" Total Portfolio Current Value: ₹%.2f%n", totalCurrentVal);
            System.out.printf(" Total Aggregate P&L:           %+.2f (%+.2f%%)%n", totalPnL, totalPnLPct);
            System.out.println("=========================================================================");
        }

        System.out.println(" [1] Export Portfolio Report (CSV)");
        System.out.println(" [2] Go Back");
        System.out.print(" Select option: ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            String path = ReportGenerator.exportPortfolio(userId);
            if (path != null) {
                System.out.println("Portfolio report exported successfully to: " + path);
            } else {
                System.out.println("Failed to export portfolio report.");
            }
        }
    }
}
