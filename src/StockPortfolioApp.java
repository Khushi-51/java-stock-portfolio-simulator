import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Main application launcher for the Stock Portfolio Manager (Console Version)
 */
public class StockPortfolioApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$#,##0.00");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.00%");
    
    private static List<Portfolio> portfolios;
    private static FileHandler fileHandler;
    private static StockApiService stockAPI;
    
    /**
     * Main method - entry point for the application
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting Stock Portfolio Manager (Console Version)...");
        
        // Initialize services
        String projectPath = System.getProperty("user.dir");
        String filePath = projectPath + File.separator + File.separator + "portfolio_data.csv";
        System.out.println("Using data file: " + filePath); // Debug line to show file location
        fileHandler = new FileHandler(filePath);
        stockAPI = new StockApiService();

        // Load existing portfolios
        try {
            portfolios = fileHandler.loadPortfolios();
            System.out.println("Loaded " + portfolios.size() + " portfolios.");
        } catch (IOException e) {
            System.out.println("Error loading portfolios: " + e.getMessage());
            portfolios = new ArrayList<>();
        }
        
        // Main application loop
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    viewAllPortfolios();
                    break;
                case 2:
                    viewPortfolioDetails();
                    break;
                case 3:
                    createPortfolio();
                    break;
                case 4:
                    addStock();
                    break;
                case 5:
                    removeStock();
                    break;
                case 6:
                    searchStock();
                    break;
                case 7:
                    refreshStockPrices();
                    break;
                case 8:
                    analyzePortfolio();
                    break;
                case 9:
                    showTopPerformers();
                    break;
                case 0:
                    running = false;
                    System.out.println("Thank you for using Stock Portfolio Manager. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        scanner.close();
    }
    
    /**
     * Displays the main menu options
     */
    private static void displayMainMenu() {
        System.out.println("\n===== STOCK PORTFOLIO MANAGER =====");
        System.out.println("1. View All Portfolios");
        System.out.println("2. View Portfolio Details");
        System.out.println("3. Create New Portfolio");
        System.out.println("4. Add Stock to Portfolio");
        System.out.println("5. Remove Stock from Portfolio");
        System.out.println("6. Search Stock");
        System.out.println("7. Refresh Stock Prices");
        System.out.println("8. Analyze Portfolio");
        System.out.println("9. View Top Performers");
        System.out.println("0. Exit");
        System.out.println("==================================");
    }
    
    /**
     * Displays all portfolios in a tabular format
     */
    private static void viewAllPortfolios() {
        System.out.println("\n===== ALL PORTFOLIOS =====");
        
        if (portfolios.isEmpty()) {
            System.out.println("No portfolios found. Create a portfolio first.");
            return;
        }
        
        // Print header
        System.out.printf("%-4s %-20s %-30s %-8s %-15s %-15s\n", 
                "No.", "Name", "Description", "Stocks", "Total Value", "Gain/Loss");
        System.out.println("---------------------------------------------------------------------------------");
        
        // Print each portfolio
        for (int i = 0; i < portfolios.size(); i++) {
            Portfolio portfolio = portfolios.get(i);
            double totalValue = portfolio.getTotalValue();
            double gainLoss = portfolio.getTotalGainLoss();
            
            System.out.printf("%-4d %-20s %-30s %-8d %-15s %-15s\n", 
                    i + 1, 
                    truncateString(portfolio.getName(), 20),
                    truncateString(portfolio.getDescription(), 30),
                    portfolio.getStocks().size(),
                    CURRENCY_FORMAT.format(totalValue),
                    CURRENCY_FORMAT.format(gainLoss));
        }
    }
    
    /**
     * Displays detailed information about a selected portfolio
     */
    private static void viewPortfolioDetails() {
        if (portfolios.isEmpty()) {
            System.out.println("No portfolios found. Create a portfolio first.");
            return;
        }
        
        Portfolio portfolio = selectPortfolio();
        if (portfolio == null) return;
        
        System.out.println("\n===== PORTFOLIO DETAILS =====");
        System.out.println("Name: " + portfolio.getName());
        System.out.println("Description: " + portfolio.getDescription());
        System.out.println("Created: " + portfolio.getCreatedAt());
        System.out.println("Last Updated: " + portfolio.getLastUpdated());
        
        if (portfolio.getStocks().isEmpty()) {
            System.out.println("\nNo stocks in this portfolio.");
            return;
        }
        
        // Print stocks table
        System.out.println("\nStocks:");
        System.out.printf("%-6s %-20s %-10s %-15s %-15s %-15s %-15s %-10s\n", 
                "Symbol", "Name", "Quantity", "Purchase Price", "Current Price", "Value", "Gain/Loss", "Gain/Loss %");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        
        for (Stock stock : portfolio.getStocks()) {
            System.out.printf("%-6s %-20s %-10d %-15s %-15s %-15s %-15s %-10s\n", 
                    stock.getSymbol(),
                    truncateString(stock.getName(), 20),
                    stock.getQuantity(),
                    CURRENCY_FORMAT.format(stock.getPurchasePrice()),
                    CURRENCY_FORMAT.format(stock.getCurrentPrice()),
                    CURRENCY_FORMAT.format(stock.getCurrentValue()),
                    CURRENCY_FORMAT.format(stock.getGainLoss()),
                    PERCENT_FORMAT.format(stock.getPercentageGainLoss() / 100));
        }
        
        // Print summary
        double totalCost = portfolio.getTotalCost();
        double totalValue = portfolio.getTotalValue();
        double gainLoss = portfolio.getTotalGainLoss();
        double gainLossPercent = totalCost > 0 ? (gainLoss / totalCost) * 100 : 0;
        
        System.out.println("\nSummary:");
        System.out.println("Total Cost: " + CURRENCY_FORMAT.format(totalCost));
        System.out.println("Current Value: " + CURRENCY_FORMAT.format(totalValue));
        System.out.println("Gain/Loss: " + CURRENCY_FORMAT.format(gainLoss) + 
                " (" + PERCENT_FORMAT.format(gainLossPercent / 100) + ")");
    }
    
    /**
     * Creates a new portfolio
     */
    private static void createPortfolio() {
        System.out.println("\n===== CREATE NEW PORTFOLIO =====");
        
        String name = getStringInput("Enter portfolio name: ");
        if (name.isEmpty()) {
            System.out.println("Portfolio name cannot be empty.");
            return;
        }
        
        String description = getStringInput("Enter description (optional): ");
        
        Portfolio portfolio = new Portfolio(name, description);
        portfolios.add(portfolio);
        savePortfolios();
        
        System.out.println("Portfolio '" + name + "' created successfully.");
    }
    
    /**
     * Adds a stock to a portfolio
     */
    private static void addStock() {
        if (portfolios.isEmpty()) {
            System.out.println("No portfolios found. Create a portfolio first.");
            return;
        }
        
        Portfolio portfolio = selectPortfolio();
        if (portfolio == null) return;
        
        System.out.println("\n===== ADD STOCK TO " + portfolio.getName().toUpperCase() + " =====");
        
        String symbol = getStringInput("Enter stock symbol (e.g., AAPL): ").toUpperCase();
        if (symbol.isEmpty()) {
            System.out.println("Symbol cannot be empty.");
            return;
        }
        
        int quantity;
        try {
            quantity = getIntInput("Enter quantity: ");
            if (quantity <= 0) {
                System.out.println("Quantity must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity format.");
            return;
        }
        
        double price;
        try {
            price = getDoubleInput("Enter purchase price: ");
            if (price <= 0) {
                System.out.println("Price must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid price format.");
            return;
        }
        
        System.out.println("Fetching stock data for " + symbol + "...");
        
        try {
            Stock stockInfo = stockAPI.getStockQuote(symbol);
            if (stockInfo == null) {
                System.out.println("Stock not found.");
                return;
            }
            
            Stock stock = new Stock(symbol, stockInfo.getName(), quantity, price);
            stock.setCurrentPrice(stockInfo.getCurrentPrice());
            
            portfolio.addStock(stock);
            savePortfolios();
            
            System.out.println("Added " + quantity + " shares of " + symbol + " to " + portfolio.getName());
            System.out.println("Current price: " + CURRENCY_FORMAT.format(stockInfo.getCurrentPrice()));
            
        } catch (Exception e) {
            System.out.println("Error adding stock: " + e.getMessage());
        }
    }
    
    /**
     * Removes a stock from a portfolio
     */
    private static void removeStock() {
        if (portfolios.isEmpty()) {
            System.out.println("No portfolios found. Create a portfolio first.");
            return;
        }
        
        Portfolio portfolio = selectPortfolio();
        if (portfolio == null) return;
        
        if (portfolio.getStocks().isEmpty()) {
            System.out.println("No stocks in this portfolio.");
            return;
        }
        
        System.out.println("\n===== REMOVE STOCK FROM " + portfolio.getName().toUpperCase() + " =====");
        System.out.println("Available stocks:");
        
        List<Stock> stocks = portfolio.getStocks();
        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            System.out.println((i + 1) + ". " + stock.getSymbol() + " - " + stock.getName() + 
                    " (" + stock.getQuantity() + " shares)");
        }
        
        int stockIndex = getIntInput("Enter the number of the stock to remove (0 to cancel): ") - 1;
        
        if (stockIndex == -1) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        if (stockIndex < 0 || stockIndex >= stocks.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Stock stockToRemove = stocks.get(stockIndex);
        portfolio.removeStock(stockToRemove);
        savePortfolios();
        
        System.out.println("Removed " + stockToRemove.getSymbol() + " from " + portfolio.getName());
    }
    
    /**
     * Searches for a stock by symbol
     */
    private static void searchStock() {
        System.out.println("\n===== SEARCH STOCK =====");
        
        String symbol = getStringInput("Enter stock symbol (e.g., AAPL): ").toUpperCase();
        if (symbol.isEmpty()) {
            System.out.println("Symbol cannot be empty.");
            return;
        }
        
        System.out.println("Searching for " + symbol + "...");
        
        try {
            Stock stock = stockAPI.getStockQuote(symbol);
            if (stock == null) {
                System.out.println("Stock not found.");
                return;
            }
            
            System.out.println("\nStock Details:");
            System.out.println("Symbol: " + stock.getSymbol());
            System.out.println("Name: " + stock.getName());
            System.out.println("Current Price: " + CURRENCY_FORMAT.format(stock.getCurrentPrice()));
            System.out.println("Last Updated: " + stock.getLastUpdated());
            
        } catch (Exception e) {
            System.out.println("Error searching for stock: " + e.getMessage());
        }
    }
    
    /**
     * Refreshes stock prices for a portfolio
     */
    private static void refreshStockPrices() {
        if (portfolios.isEmpty()) {
            System.out.println("No portfolios found. Create a portfolio first.");
            return;
        }
        
        Portfolio portfolio = selectPortfolio();
        if (portfolio == null) return;
        
        if (portfolio.getStocks().isEmpty()) {
            System.out.println("No stocks in this portfolio to refresh.");
            return;
        }
        
        System.out.println("\nRefreshing stock prices for " + portfolio.getName() + "...");
        
        try {
            for (Stock stock : portfolio.getStocks()) {
                System.out.print("Updating " + stock.getSymbol() + "... ");
                Stock updated = stockAPI.getStockQuote(stock.getSymbol());
                if (updated != null) {
                    double oldPrice = stock.getCurrentPrice();
                    stock.setCurrentPrice(updated.getCurrentPrice());
                    System.out.println("Done. " + CURRENCY_FORMAT.format(oldPrice) + " -> " + 
                            CURRENCY_FORMAT.format(stock.getCurrentPrice()));
                } else {
                    System.out.println("Failed to update.");
                }
            }
            
            savePortfolios();
            System.out.println("\nStock prices updated successfully.");
            
        } catch (Exception e) {
            System.out.println("Error refreshing prices: " + e.getMessage());
        }
    }
    
    /**
     * Analyzes a portfolio and displays metrics
     */
    private static void analyzePortfolio() {
        if (portfolios.isEmpty()) {
            System.out.println("No portfolios found. Create a portfolio first.");
            return;
        }
        
        Portfolio portfolio = selectPortfolio();
        if (portfolio == null) return;
        
        if (portfolio.getStocks().isEmpty()) {
            System.out.println("No stocks in this portfolio to analyze.");
            return;
        }
        
        System.out.println("\n===== PORTFOLIO ANALYSIS: " + portfolio.getName().toUpperCase() + " =====");
        System.out.println("Updating stock prices for analysis...");
        
        try {
            // Update stock prices
            for (Stock stock : portfolio.getStocks()) {
                Stock updated = stockAPI.getStockQuote(stock.getSymbol());
                if (updated != null) {
                    stock.setCurrentPrice(updated.getCurrentPrice());
                }
            }
            
            // Calculate metrics
            double totalValue = portfolio.getTotalValue();
            double totalCost = portfolio.getTotalCost();
            double totalGainLoss = totalValue - totalCost;
            double percentGainLoss = totalCost > 0 ? (totalGainLoss / totalCost) * 100 : 0;
            
            // Find best and worst performers
            Stock bestStock = findBestPerformingStock(portfolio);
            Stock worstStock = findWorstPerformingStock(portfolio);
            
            // Display analysis
            System.out.println("\nAnalysis Results:");
            System.out.println("Total Cost: " + CURRENCY_FORMAT.format(totalCost));
            System.out.println("Current Value: " + CURRENCY_FORMAT.format(totalValue));
            System.out.println("Total Gain/Loss: " + CURRENCY_FORMAT.format(totalGainLoss) + 
                    " (" + PERCENT_FORMAT.format(percentGainLoss / 100) + ")");
            
            if (bestStock != null) {
                double bestReturn = bestStock.getPercentageGainLoss();
                System.out.println("Best Performing Stock: " + bestStock.getSymbol() + " (" + 
                        PERCENT_FORMAT.format(bestReturn / 100) + ")");
            }
            
            if (worstStock != null) {
                double worstReturn = worstStock.getPercentageGainLoss();
                System.out.println("Worst Performing Stock: " + worstStock.getSymbol() + " (" + 
                        PERCENT_FORMAT.format(worstReturn / 100) + ")");
            }
            
            // Save updated prices
            savePortfolios();
            
        } catch (Exception e) {
            System.out.println("Error analyzing portfolio: " + e.getMessage());
        }
    }
    
    /**
     * Shows top performing stocks across all portfolios
     */
    private static void showTopPerformers() {
        if (portfolios.isEmpty()) {
            System.out.println("No portfolios found. Create a portfolio first.");
            return;
        }
        
        // Check if there are any stocks
        boolean hasStocks = false;
        for (Portfolio p : portfolios) {
            if (!p.getStocks().isEmpty()) {
                hasStocks = true;
                break;
            }
        }
        
        if (!hasStocks) {
            System.out.println("No stocks found in any portfolio. Add stocks first.");
            return;
        }
        
        System.out.println("\n===== TOP PERFORMING STOCKS =====");
        System.out.println("Updating stock prices...");
        
        try {
            // Update all stock prices
            for (Portfolio portfolio : portfolios) {
                for (Stock stock : portfolio.getStocks()) {
                    Stock updated = stockAPI.getStockQuote(stock.getSymbol());
                    if (updated != null) {
                        stock.setCurrentPrice(updated.getCurrentPrice());
                    }
                }
            }
            
            // Save updated prices
            savePortfolios();
            
            // Ask for sort criteria
            System.out.println("\nSort by:");
            System.out.println("1. Gain/Loss %");
            System.out.println("2. Gain/Loss $");
            System.out.println("3. Current Value");
            int sortChoice = getIntInput("Enter your choice: ");
            
            // Ask for number of stocks to show
            int numStocks = getIntInput("How many top stocks to show? ");
            if (numStocks <= 0) {
                numStocks = 5; // Default
            }
            
            // Collect all stocks from all portfolios
            List<StockWithPortfolio> allStocks = new ArrayList<>();
            for (Portfolio portfolio : portfolios) {
                for (Stock stock : portfolio.getStocks()) {
                    allStocks.add(new StockWithPortfolio(stock, portfolio.getName()));
                }
            }
            
            // Sort stocks based on selected criteria
            switch (sortChoice) {
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
            
            // Display top N stocks
            System.out.println("\nTop " + Math.min(numStocks, allStocks.size()) + " Performing Stocks:");
            System.out.printf("%-4s %-6s %-20s %-20s %-15s %-15s %-15s %-10s\n", 
                    "Rank", "Symbol", "Name", "Portfolio", "Purchase", "Current", "Gain/Loss", "Gain/Loss %");
            System.out.println("----------------------------------------------------------------------------------------------------------");
            
            int count = Math.min(numStocks, allStocks.size());
            for (int i = 0; i < count; i++) {
                StockWithPortfolio stockInfo = allStocks.get(i);
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
            
        } catch (Exception e) {
            System.out.println("Error analyzing top performers: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to find the best performing stock in a portfolio
     */
    private static Stock findBestPerformingStock(Portfolio portfolio) {
        if (portfolio.getStocks().isEmpty()) {
            return null;
        }
        
        Stock best = portfolio.getStocks().get(0);
        for (Stock stock : portfolio.getStocks()) {
            if (stock.getPercentageGainLoss() > best.getPercentageGainLoss()) {
                best = stock;
            }
        }
        return best;
    }
    
    /**
     * Helper method to find the worst performing stock in a portfolio
     */
    private static Stock findWorstPerformingStock(Portfolio portfolio) {
        if (portfolio.getStocks().isEmpty()) {
            return null;
        }
        
        Stock worst = portfolio.getStocks().get(0);
        for (Stock stock : portfolio.getStocks()) {
            if (stock.getPercentageGainLoss() < worst.getPercentageGainLoss()) {
                worst = stock;
            }
        }
        return worst;
    }
    
    /**
     * Helper method to select a portfolio from the list
     */
    private static Portfolio selectPortfolio() {
        System.out.println("\nAvailable portfolios:");
        for (int i = 0; i < portfolios.size(); i++) {
            System.out.println((i + 1) + ". " + portfolios.get(i).getName());
        }
        
        int portfolioIndex = getIntInput("Enter the number of the portfolio (0 to cancel): ") - 1;
        
        if (portfolioIndex == -1) {
            System.out.println("Operation cancelled.");
            return null;
        }
        
        if (portfolioIndex < 0 || portfolioIndex >= portfolios.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        
        return portfolios.get(portfolioIndex);
    }
    
    /**
     * Helper method to save portfolios to file
     */
    private static void savePortfolios() {
        try {
            fileHandler.savePortfolios(portfolios);
        } catch (IOException e) {
            System.out.println("Error saving portfolios: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to get string input from user
     */
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    /**
     * Helper method to get integer input from user
     */
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    /**
     * Helper method to get double input from user
     */
    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    /**
     * Helper method to truncate strings for display
     */
    private static String truncateString(String str, int length) {
        if (str == null) return "";
        return str.length() <= length ? str : str.substring(0, length - 3) + "...";
    }
    
    /**
     * Helper class to associate a stock with its portfolio
     */
    private static class StockWithPortfolio {
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
