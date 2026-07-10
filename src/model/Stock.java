package model;

public class Stock {
    private String ticker;
    private String companyName;
    private int sectorId;
    private double currentPrice;
    private double openPrice;
    private double prevClose;
    private double marketCap;
    private double peRatio;
    private double pbRatio;
    private double roe;
    private double roa;
    private long totalShares;
    private double promoterHold;
    private double instHold;
    private double retailHold;
    private boolean isListed;
    private String exchange;

    public Stock(String ticker, String companyName, int sectorId, double currentPrice, double openPrice,
                 double prevClose, double marketCap, double peRatio, double pbRatio, double roe, double roa,
                 long totalShares, double promoterHold, double instHold, double retailHold, boolean isListed,
                 String exchange) {
        this.ticker = ticker;
        this.companyName = companyName;
        this.sectorId = sectorId;
        this.currentPrice = currentPrice;
        this.openPrice = openPrice;
        this.prevClose = prevClose;
        this.marketCap = marketCap;
        this.peRatio = peRatio;
        this.pbRatio = pbRatio;
        this.roe = roe;
        this.roa = roa;
        this.totalShares = totalShares;
        this.promoterHold = promoterHold;
        this.instHold = instHold;
        this.retailHold = retailHold;
        this.isListed = isListed;
        this.exchange = exchange;
    }

    // Getters and Setters
    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public int getSectorId() { return sectorId; }
    public void setSectorId(int sectorId) { this.sectorId = sectorId; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public double getOpenPrice() { return openPrice; }
    public void setOpenPrice(double openPrice) { this.openPrice = openPrice; }

    public double getPrevClose() { return prevClose; }
    public void setPrevClose(double prevClose) { this.prevClose = prevClose; }

    public double getMarketCap() { return marketCap; }
    public void setMarketCap(double marketCap) { this.marketCap = marketCap; }

    public double getPeRatio() { return peRatio; }
    public void setPeRatio(double peRatio) { this.peRatio = peRatio; }

    public double getPbRatio() { return pbRatio; }
    public void setPbRatio(double pbRatio) { this.pbRatio = pbRatio; }

    public double getRoe() { return roe; }
    public void setRoe(double roe) { this.roe = roe; }

    public double getRoa() { return roa; }
    public void setRoa(double roa) { this.roa = roa; }

    public long getTotalShares() { return totalShares; }
    public void setTotalShares(long totalShares) { this.totalShares = totalShares; }

    public double getPromoterHold() { return promoterHold; }
    public void setPromoterHold(double promoterHold) { this.promoterHold = promoterHold; }

    public double getInstHold() { return instHold; }
    public void setInstHold(double instHold) { this.instHold = instHold; }

    public double getRetailHold() { return retailHold; }
    public void setRetailHold(double retailHold) { this.retailHold = retailHold; }

    public boolean isListed() { return isListed; }
    public void setListed(boolean listed) { isListed = listed; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }
}
