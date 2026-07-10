package model;

import java.sql.Timestamp;

public class
IPO {
    private int ipoId;
    private String ticker;
    private String companyName;
    private int sectorId;
    private double ipoPrice;
    private long totalShares;
    private long sharesRemaining;
    private Timestamp openTime;
    private Timestamp closeTime;
    private String status; // UPCOMING, OPEN, CLOSED, LISTED

    public IPO(int ipoId, String ticker, String companyName, int sectorId, double ipoPrice,
               long totalShares, long sharesRemaining, Timestamp openTime, Timestamp closeTime, String status) {
        this.ipoId = ipoId;
        this.ticker = ticker;
        this.companyName = companyName;
        this.sectorId = sectorId;
        this.ipoPrice = ipoPrice;
        this.totalShares = totalShares;
        this.sharesRemaining = sharesRemaining;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.status = status;
    }

    public int getIpoId() { return ipoId; }
    public void setIpoId(int ipoId) { this.ipoId = ipoId; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public int getSectorId() { return sectorId; }
    public void setSectorId(int sectorId) { this.sectorId = sectorId; }

    public double getIpoPrice() { return ipoPrice; }
    public void setIpoPrice(double ipoPrice) { this.ipoPrice = ipoPrice; }

    public long getTotalShares() { return totalShares; }
    public void setTotalShares(long totalShares) { this.totalShares = totalShares; }

    public long getSharesRemaining() { return sharesRemaining; }
    public void setSharesRemaining(long sharesRemaining) { this.sharesRemaining = sharesRemaining; }

    public Timestamp getOpenTime() { return openTime; }
    public void setOpenTime(Timestamp openTime) { this.openTime = openTime; }

    public Timestamp getCloseTime() { return closeTime; }
    public void setCloseTime(Timestamp closeTime) { this.closeTime = closeTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
