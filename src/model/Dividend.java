package model;

import java.sql.Timestamp;

public class Dividend {
    private int dividendId;
    private String ticker;
    private double amountPerShare;
    private Timestamp declaredAt;
    private Timestamp paidAt;

    public Dividend(int dividendId, String ticker, double amountPerShare, Timestamp declaredAt, Timestamp paidAt) {
        this.dividendId = dividendId;
        this.ticker = ticker;
        this.amountPerShare = amountPerShare;
        this.declaredAt = declaredAt;
        this.paidAt = paidAt;
    }

    public int getDividendId() { return dividendId; }
    public void setDividendId(int dividendId) { this.dividendId = dividendId; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public double getAmountPerShare() { return amountPerShare; }
    public void setAmountPerShare(double amountPerShare) { this.amountPerShare = amountPerShare; }

    public Timestamp getDeclaredAt() { return declaredAt; }
    public void setDeclaredAt(Timestamp declaredAt) { this.declaredAt = declaredAt; }

    public Timestamp getPaidAt() { return paidAt; }
    public void setPaidAt(Timestamp paidAt) { this.paidAt = paidAt; }
}
