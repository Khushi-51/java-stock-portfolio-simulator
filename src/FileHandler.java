import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file I/O operations for portfolio data persistence
 */
public class FileHandler {
    private final String filePath;
    
    public FileHandler(String filePath) {
        this.filePath = filePath;
    }
    
    /**
     * Loads portfolios from a CSV file
     */
    public List<Portfolio> loadPortfolios() throws IOException {
        List<Portfolio> portfolios = new ArrayList<>();
        File file = new File(filePath);
        
        // If file doesn't exist yet, return empty list
        if (!file.exists()) {
            return portfolios;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Portfolio currentPortfolio = null;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                
                // Section separator
                if (line.equals("---")) {
                    if (currentPortfolio != null) {
                        portfolios.add(currentPortfolio);
                    }
                    currentPortfolio = null;
                    continue;
                }
                
                // If we don't have a current portfolio, this line is a portfolio header
                if (currentPortfolio == null) {
                    String[] portfolioData = line.split(",", 2);
                    String name = portfolioData[0];
                    String description = portfolioData.length > 1 ? portfolioData[1].replace(";", ",") : "";
                    
                    currentPortfolio = new Portfolio(name, description);
                } else {
                    // This is a stock line
                    try {
                        Stock stock = Stock.fromCsv(line);
                        currentPortfolio.addStock(stock);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error parsing stock data: " + e.getMessage());
                    }
                }
            }
            
            // Add the last portfolio if there is one
            if (currentPortfolio != null) {
                portfolios.add(currentPortfolio);
            }
        }
        
        return portfolios;
    }
    
    /**
     * Saves portfolios to a CSV file
     */
    public void savePortfolios(List<Portfolio> portfolios) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < portfolios.size(); i++) {
                Portfolio portfolio = portfolios.get(i);
                
                // Write portfolio data
                writer.write(portfolio.toCsv());
                
                // Add a separator between portfolios
                if (i < portfolios.size() - 1) {
                    writer.write("---\n");
                }
            }
        }
    }
}
