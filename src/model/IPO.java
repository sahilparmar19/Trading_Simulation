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
    private IPOStatus status;

    public IPO(int ipoId, String ticker, String companyName, int sectorId, double ipoPrice,
               long totalShares, long sharesRemaining, Timestamp openTime, Timestamp closeTime, IPOStatus status) {
        this.ipoId = ipoId;
        this.ticker = ticker;
        this.companyName = companyName;
        this.sectorId = sectorId;
        setIpoPrice(ipoPrice);
        setTotalShares(totalShares);
        setSharesRemaining(sharesRemaining);
        this.openTime = openTime;
        setCloseTime(closeTime);
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
    public void setIpoPrice(double ipoPrice) {
        if (ipoPrice <= 0) {
            throw new IllegalArgumentException("IPO price must be greater than zero.");
        }
        this.ipoPrice = ipoPrice;
    }

    public long getTotalShares() { return totalShares; }
    public void setTotalShares(long totalShares) {
        if (totalShares <= 0) {
            throw new IllegalArgumentException("Total shares must be greater than zero.");
        }
        if (sharesRemaining > totalShares) {
            throw new IllegalArgumentException("Shares remaining cannot exceed total shares.");
        }
        this.totalShares = totalShares;
    }

    public long getSharesRemaining() { return sharesRemaining; }
    public void setSharesRemaining(long sharesRemaining) {
        if (sharesRemaining < 0 || sharesRemaining > totalShares) {
            throw new IllegalArgumentException("Shares remaining must be between zero and total shares.");
        }
        this.sharesRemaining = sharesRemaining;
    }

    public Timestamp getOpenTime() { return openTime; }
    public void setOpenTime(Timestamp openTime) {
        validateTimeRange(openTime, closeTime);
        this.openTime = openTime;
    }

    public Timestamp getCloseTime() { return closeTime; }
    public void setCloseTime(Timestamp closeTime) {
        validateTimeRange(openTime, closeTime);
        this.closeTime = closeTime;
    }

    public IPOStatus getStatus() { return status; }
    public void setStatus(IPOStatus status) { this.status = status; }

    private static void validateTimeRange(Timestamp openTime, Timestamp closeTime) {
        if (openTime != null && closeTime != null && closeTime.before(openTime)) {
            throw new IllegalArgumentException("Close time cannot be before open time.");
        }
    }
}
