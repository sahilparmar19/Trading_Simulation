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
        setAmountPerShare(amountPerShare);
        this.declaredAt = declaredAt;
        this.paidAt = paidAt;
    }

    public int getDividendId() { return dividendId; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public double getAmountPerShare() { return amountPerShare; }
    public void setAmountPerShare(double amountPerShare) {
        if (amountPerShare <= 0) {
            throw new IllegalArgumentException("Dividend amount per share must be greater than zero.");
        }
        this.amountPerShare = amountPerShare;
    }

    public Timestamp getDeclaredAt() { return declaredAt; }

    public Timestamp getPaidAt() { return paidAt; }

    @Override
    public String toString() {
        return "Dividend{" +
                "dividendId=" + dividendId +
                ", ticker='" + ticker + '\'' +
                ", amountPerShare=" + amountPerShare +
                ", declaredAt=" + declaredAt +
                ", paidAt=" + paidAt +
                '}';
    }
}
