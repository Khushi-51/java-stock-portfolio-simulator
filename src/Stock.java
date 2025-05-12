import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a stock in a portfolio
 */
public class Stock implements Serializable {
    private String symbol;
    private String name;
    private int quantity;
    private double purchasePrice;
    private double currentPrice;
    private LocalDateTime lastUpdated;
    
    public Stock(String symbol, String name, int quantity, double purchasePrice) {
        this.symbol = symbol;
        this.name = name;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.currentPrice = purchasePrice; // Default to purchase price until updated
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Default constructor for serialization
    public Stock() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    public double getCurrentValue() {
        return quantity * currentPrice;
    }
    
    public double getCostBasis() {
        return quantity * purchasePrice;
    }
    
    public double getGainLoss() {
        return getCurrentValue() - getCostBasis();
    }
    
    public double getPercentageGainLoss() {
        if (getCostBasis() == 0) return 0;
        return (getGainLoss() / getCostBasis()) * 100;
    }
    
    // Getters and setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }
    
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    
    public String toCsv() {
        return String.format("%s,%s,%d,%.2f,%.2f", 
                symbol, name.replace(",", ";"), quantity, purchasePrice, currentPrice);
    }
    
    public static Stock fromCsv(String csvLine) {
        String[] values = csvLine.split(",");
        if (values.length >= 5) {
            Stock stock = new Stock();
            stock.setSymbol(values[0]);
            stock.setName(values[1].replace(";", ","));
            stock.setQuantity(Integer.parseInt(values[2]));
            stock.setPurchasePrice(Double.parseDouble(values[3]));
            stock.setCurrentPrice(Double.parseDouble(values[4]));
            return stock;
        }
        throw new IllegalArgumentException("Invalid CSV format for Stock");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Stock stock = (Stock) obj;
        return symbol.equals(stock.symbol);
    }
    
    @Override
    public int hashCode() {
        return symbol.hashCode();
    }
}
