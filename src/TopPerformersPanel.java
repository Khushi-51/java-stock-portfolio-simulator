import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class for analyzing top performing stocks across all portfolios
 */
public class TopPerformersPanel {
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$#,##0.00");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.00%");
    private final List<Portfolio> portfolios;
    
    /**
     * Constructor for TopPerformersPanel
     * @param portfolios List of portfolios to analyze
     */
    public TopPerformersPanel(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }
    
    /**
     * Gets the top performing stocks based on specified criteria
     * @param sortCriteria The criteria to sort by (1=Percentage, 2=Dollar Amount, 3=Value)
     * @param numStocks Number of top stocks to return
     * @return List of top performing stocks with their portfolio names
     */
    public List<StockWithPortfolio> getTopStocks(int sortCriteria, int numStocks) {
        // Collect all stocks from all portfolios
        List<StockWithPortfolio> allStocks = new ArrayList<>();
        for (Portfolio portfolio : portfolios) {
            for (Stock stock : portfolio.getStocks()) {
                allStocks.add(new StockWithPortfolio(stock, portfolio.getName()));
            }
        }
        
        // Sort stocks based on selected criteria
        switch (sortCriteria) {
            case 1: // Gain/Loss %
                Collections.sort(allStocks, Comparator.comparing(StockWithPortfolio::getPercentageGainLoss).reversed());
                break;
            case 2: // Gain/Loss $
                Collections.sort(allStocks, Comparator.comparing(StockWithPortfolio::getGainLoss).reversed());
                break;
            case 3: // Current Value
                Collections.sort(allStocks, Comparator.comparing(StockWithPortfolio::getCurrentValue).reversed());
                break;
            default:
                Collections.sort(allStocks, Comparator.comparing(StockWithPortfolio::getPercentageGainLoss).reversed());
        }
        
        // Return top N stocks
        int count = Math.min(numStocks, allStocks.size());
        return allStocks.subList(0, count);
    }
    
    /**
     * Prints the top performing stocks to the console
     * @param sortCriteria The criteria to sort by (1=Percentage, 2=Dollar Amount, 3=Value)
     * @param numStocks Number of top stocks to display
     */
    public void printTopStocks(int sortCriteria, int numStocks) {
        List<StockWithPortfolio> topStocks = getTopStocks(sortCriteria, numStocks);
        
        if (topStocks.isEmpty()) {
            System.out.println("No stocks found.");
            return;
        }
        
        // Print header
        System.out.printf("%-4s %-6s %-20s %-20s %-15s %-15s %-15s %-10s\n", 
                "Rank", "Symbol", "Name", "Portfolio", "Purchase", "Current", "Gain/Loss", "Gain/Loss %");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        
        // Print each stock
        for (int i = 0; i < topStocks.size(); i++) {
            StockWithPortfolio stockInfo = topStocks.get(i);
            Stock stock = stockInfo.getStock();
            
            System.out.printf("%-4d %-6s %-20s %-20s %-15s %-15s %-15s %-10s\n", 
                    i + 1,
                    stock.getSymbol(),
                    truncateString(stock.getName(), 20),
                    truncateString(stockInfo.getPortfolioName(), 20),
                    CURRENCY_FORMAT.format(stock.getPurchasePrice()),
                    CURRENCY_FORMAT.format(stock.getCurrentPrice()),
                    CURRENCY_FORMAT.format(stock.getGainLoss()),
                    PERCENT_FORMAT.format(stock.getPercentageGainLoss() / 100));
        }
    }
    
    /**
     * Helper method to truncate strings for display
     */
    private String truncateString(String str, int length) {
        if (str == null) return "";
        return str.length() <= length ? str : str.substring(0, length - 3) + "...";
    }
    
    /**
     * Helper class to associate a stock with its portfolio
     */
    public static class StockWithPortfolio {
        private final Stock stock;
        private final String portfolioName;
        
        public StockWithPortfolio(Stock stock, String portfolioName) {
            this.stock = stock;
            this.portfolioName = portfolioName;
        }
        
        public Stock getStock() {
            return stock;
        }
        
        public String getPortfolioName() {
            return portfolioName;
        }
        
        public double getPercentageGainLoss() {
            return stock.getPercentageGainLoss();
        }
        
        public double getGainLoss() {
            return stock.getGainLoss();
        }
        
        public double getCurrentValue() {
            return stock.getCurrentValue();
        }
    }
}
