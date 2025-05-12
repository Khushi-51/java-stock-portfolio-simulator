import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a stock portfolio with a collection of stocks
 */
public class Portfolio implements Serializable {
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private List<Stock> stocks;
    
    public Portfolio(String name, String description) {
        this.name = name;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.stocks = new ArrayList<>();
    }
    
    public void addStock(Stock stock) {
        // Check if stock already exists
        for (int i = 0; i < stocks.size(); i++) {
            Stock existingStock = stocks.get(i);
            if (existingStock.getSymbol().equals(stock.getSymbol())) {
                // Update existing stock
                int newQuantity = existingStock.getQuantity() + stock.getQuantity();
                double newAvgPrice = (existingStock.getQuantity() * existingStock.getPurchasePrice() +
                                   stock.getQuantity() * stock.getPurchasePrice()) / newQuantity;
                
                existingStock.setQuantity(newQuantity);
                existingStock.setPurchasePrice(newAvgPrice);
                this.lastUpdated = LocalDateTime.now();
                return;
            }
        }
        
        // Add new stock
        stocks.add(stock);
        this.lastUpdated = LocalDateTime.now();
    }
    
    public boolean removeStock(Stock stock) {
        boolean removed = stocks.remove(stock);
        if (removed) {
            this.lastUpdated = LocalDateTime.now();
        }
        return removed;
    }
    
    public double getTotalValue() {
        double total = 0;
        for (Stock stock : stocks) {
            total += stock.getCurrentValue();
        }
        return total;
    }
    
    public double getTotalCost() {
        double total = 0;
        for (Stock stock : stocks) {
            total += stock.getCostBasis();
        }
        return total;
    }
    
    public double getTotalGainLoss() {
        return getTotalValue() - getTotalCost();
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name; 
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description; 
        this.lastUpdated = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public List<Stock> getStocks() { return stocks; }
    
    public String toCsv() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s,%s\n", name, description.replace(",", ";")));
        
        for (Stock stock : stocks) {
            builder.append(stock.toCsv()).append("\n");
        }
        
        return builder.toString();
    }
}
