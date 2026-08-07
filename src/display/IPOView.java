package display;

import auth.Session;
import db.DatabaseManager;
import db.IPOManager;
import ds.CustomLinkedList;
import model.IPO;
import java.util.Scanner;

public class IPOView {

    public static void render(Scanner sc) {
        if (!Session.isLoggedIn()) {
            System.out.println("Please log in first.");
            return;
        }

        while (true) {
            System.out.println("\n--- IPO Center ---");
            System.out.println(" [1] View Open IPOs");
            System.out.println(" [2] Apply for IPO");
            System.out.println(" [3] View My IPO Applications");
            System.out.println(" [4] Back");
            System.out.print(" Choose option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    viewOpenIPOs();
                    break;
                case "2":
                    applyForIPO(sc);
                    break;
                case "3":
                    viewMyApplications();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid selection.");
            }
        }
    }

    private static void viewOpenIPOs() {
        System.out.println("\n--- Open IPOs ---");
        CustomLinkedList openIPOs = IPOManager.getOpenIPOs();
        if (openIPOs.size() == 0) {
            System.out.println(" There are currently no open IPOs.");
            return;
        }
        
        System.out.printf(" %-5s | %-10s | %-25s | %-12s | %-15s%n", "ID", "Ticker", "Company Name", "IPO Price", "Shares Remaining");
        System.out.println(" --------------------------------------------------------------------------------------");
        for (int i = 0; i < openIPOs.size(); i++) {
            IPO ipo = (IPO) openIPOs.get(i);
            System.out.printf(" %-5d | %-10s | %-25s | ₹%-11.2f | %-15d%n",
                    ipo.getIpoId(), ipo.getTicker(), ipo.getCompanyName(), ipo.getIpoPrice(), ipo.getSharesRemaining());
        }
    }

    private static void applyForIPO(Scanner sc) {
        System.out.println("\n--- Apply for IPO ---");
        CustomLinkedList openIPOs = IPOManager.getOpenIPOs();
        if (openIPOs.size() == 0) {
            System.out.println(" There are currently no open IPOs to apply for.");
            return;
        }

        System.out.printf(" %-5s | %-10s | %-25s | %-12s | %-15s%n", "No.", "Ticker", "Company Name", "IPO Price", "Shares Remaining");
        System.out.println(" --------------------------------------------------------------------------------------");
        for (int i = 0; i < openIPOs.size(); i++) {
            IPO ipo = (IPO) openIPOs.get(i);
            System.out.printf(" [%-3d] | %-10s | %-25s | ₹%-11.2f | %-15d%n",
                    (i + 1), ipo.getTicker(), ipo.getCompanyName(), ipo.getIpoPrice(), ipo.getSharesRemaining());
        }
        System.out.printf(" [%d] Go Back%n", openIPOs.size() + 1);
        System.out.print(" Select IPO: ");
        String choice = sc.nextLine().trim();

        int idx;
        try {
            idx = Integer.parseInt(choice);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        if (idx == openIPOs.size() + 1) {
            return;
        }
        if (idx < 1 || idx > openIPOs.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        IPO selectedIpo = (IPO) openIPOs.get(idx - 1);
        
        System.out.printf(" Selected IPO: %s (%s) at ₹%.2f per share.%n", 
                          selectedIpo.getCompanyName(), selectedIpo.getTicker(), selectedIpo.getIpoPrice());
        System.out.print(" Enter Quantity: ");
        
        int qty;
        try {
            qty = Integer.parseInt(sc.nextLine().trim());
            if (qty <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity. Must be a positive integer.");
            return;
        }

        double totalAmount = qty * selectedIpo.getIpoPrice();
        double userBalance = DatabaseManager.getUserBalance(Session.getCurrentUser().getUserId());
        
        System.out.printf(" Total Amount Required: ₹%.2f (Your Balance: ₹%.2f)%n", totalAmount, userBalance);
        System.out.print(" Confirm application? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        
        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("Application cancelled.");
            return;
        }
        
        boolean success = IPOManager.applyForIPO(Session.getCurrentUser().getUserId(), selectedIpo.getIpoId(), qty);
        if (success) {
            System.out.println("Successfully applied for " + qty + " shares of " + selectedIpo.getTicker() + "!");
        } else {
            System.out.println("Failed to apply for IPO. Please check errors above.");
        }
    }

    private static void viewMyApplications() {
        System.out.println("\n--- My IPO Applications ---");
        int userId = Session.getCurrentUser().getUserId();
        CustomLinkedList apps = IPOManager.getUserIPOApplications(userId);
        
        if (apps.size() == 0) {
            System.out.println(" You have not applied for any IPOs.");
            return;
        }
        
        System.out.printf(" %-8s | %-10s | %-25s | %-12s | %-12s | %-10s%n", "App ID", "Ticker", "Company Name", "Applied Qty", "Allotted Qty", "Status");
        System.out.println(" ------------------------------------------------------------------------------------------");
        for (int i = 0; i < apps.size(); i++) {
            IPOManager.IPOApplicationInfo info = (IPOManager.IPOApplicationInfo) apps.get(i);
            System.out.printf(" %-8d | %-10s | %-25s | %-12d | %-12d | %-10s%n",
                    info.appId, info.ticker, info.companyName, info.appliedQty, info.allottedQty, info.status);
        }
    }
}
