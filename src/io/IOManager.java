package io;

import model.Trade;
import model.StopLossOrder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IOManager {

    static {
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }
    }

    public static synchronized void logTrade(Trade trade) {
        String logPath = "logs/trades.log";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date(trade.getExecutedAt().getTime()));

        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(logPath, true);
            pw = new PrintWriter(fw);
            pw.printf("[%s] TRADE MATCHED: Ticker: %s | Buy Order: %d | Sell Order: %d | Price: ₹%.2f | Qty: %d | Total: ₹%.2f%n",
                    timestamp, trade.getTicker(), trade.getBuyOrderId(), trade.getSellOrderId(),
                    trade.getExecutedPrice(), trade.getQuantity(), (trade.getExecutedPrice() * trade.getQuantity()));
        } catch (IOException e) {
            System.err.println("Error writing to trades.log: " + e.getMessage());
        } finally {
            if (pw != null) { pw.close(); }
            if (fw != null) { try { fw.close(); } catch (IOException e) { System.err.println("Error closing FileWriter: " + e.getMessage()); } }
        }
    }

    public static synchronized void logStopLossTrigger(StopLossOrder sl) {
        String logPath = "logs/stoploss.log";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date(sl.getCreatedAt().getTime()));

        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(logPath, true);
            pw = new PrintWriter(fw);
            pw.printf("[%s] STOP LOSS TRIGGERED: Ticker: %s | User ID: %d | SL Order: %d | Stop Price: ₹%.2f | Qty: %d%n",
                    timestamp, sl.getTicker(), sl.getUserId(), sl.getSlId(),
                    sl.getStopPrice(), sl.getQuantity());
        } catch (IOException e) {
            System.err.println("Error writing to stoploss.log: " + e.getMessage());
        } finally {
            if (pw != null) { pw.close(); }
            if (fw != null) { try { fw.close(); } catch (IOException e) { System.err.println("Error closing FileWriter: " + e.getMessage()); } }
        }
    }
}
