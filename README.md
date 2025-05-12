# Virtual Stock Portfolio Manager

A console-based Java application for managing and tracking virtual stock portfolios. This application allows users to create multiple portfolios, add and remove stocks, track performance, and analyze investment returns.

## Features

- **Portfolio Management**: Create, view, and manage multiple stock portfolios
- **Stock Tracking**: Add and remove stocks with purchase information
- **Real-time Data**: Fetch current stock prices from Alpha Vantage API
- **Performance Analysis**: Calculate gains/losses and analyze portfolio performance
- **Top Performers**: Identify best and worst performing stocks across portfolios
- **Data Persistence**: Save and load portfolio data from CSV files

## Data Structures & Algorithms

The application incorporates several data structures and algorithms:

- **Lists and ArrayLists**: Used for storing collections of portfolios and stocks
- **Sorting Algorithms**: Implemented for ranking stocks by performance metrics
- **Comparators**: Custom comparators for sorting stocks by different criteria
- **File I/O**: CSV parsing and writing for data persistence
- **Priority Queues**: Used in the analysis of top performing stocks

## CRUD Operations

The application supports full CRUD (Create, Read, Update, Delete) operations:

- **Create**: Add new portfolios and stocks
- **Read**: View portfolio details and stock information
- **Update**: Refresh stock prices and modify portfolio contents
- **Delete**: Remove stocks from portfolios

## Project Structure

```
Virtual Stock Portfolio Simulator/
├── src/
│   ├── FileHandler.java           # Handles CSV file operations
│   ├── Portfolio.java             # Portfolio model and operations
│   ├── Stock.java                # Stock model and attributes
│   ├── StockApiService.java      # Alpha Vantage API integration
│   ├── StockPortfolioApp.java    # Main application & UI
│   ├── TopPerformersPanel.java   # Performance analysis logic
│   └── portfolio_data.csv        # Sample portfolio data
│
├── .gitignore                    # Git ignore configuration
└── README.md                     # Project documentation
```

### File Descriptions

- **FileHandler.java**: Manages reading and writing portfolio data to CSV files
- **Portfolio.java**: Contains portfolio management logic and calculations
- **Stock.java**: Defines stock properties and methods for stock operations
- **StockApiService.java**: Handles API calls to fetch real-time stock data
- **StockPortfolioApp.java**: Main application entry point with console interface
- **TopPerformersPanel.java**: Analytics for identifying top/bottom performers
- **portfolio_data.csv**: Sample data file with pre-configured portfolios

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Internet connection for fetching stock data

### Running the Application

1. Compile the Java files:
   \`\`\`
   javac src/*.java
   \`\`\`

2. Run the application:
   \`\`\`
   java -cp src StockPortfolioApp
   \`\`\`

### API Key (Optional)

The application uses a demo API key for Alpha Vantage by default. For unlimited access:

1. Get a free API key from [Alpha Vantage](https://www.alphavantage.co/support/#api-key)
2. Set the environment variable:
   \`\`\`
   export ALPHA_VANTAGE_API_KEY=your_api_key
   \`\`\`

## Usage

The application provides a menu-driven interface with the following options:

1. View All Portfolios
2. View Portfolio Details
3. Create New Portfolio
4. Add Stock to Portfolio
5. Remove Stock from Portfolio
6. Search Stock
7. Refresh Stock Prices
8. Analyze Portfolio
9. View Top Performers
0. Exit

## File Structure

- `StockPortfolioApp.java`: Main application with console UI
- `Portfolio.java`: Portfolio model class
- `Stock.java`: Stock model class
- `FileHandler.java`: Handles file I/O operations
- `StockApiService.java`: Fetches stock data from API
- `TopPerformersPanel.java`: Analyzes top performing stocks

## Sample Data

The application comes with sample portfolio data in `src/portfolio_data.csv` including:

- Tech Heavy Portfolio
- Balanced Growth Portfolio
- Dividend-Focused Portfolio
- High-Risk, High-Reward Portfolio
- Conservative Portfolio

## Future Enhancements

- Historical performance tracking
- Portfolio diversification analysis
- Export reports to PDF
- Dividend tracking
- Investment goal setting

## License

This project is available for educational purposes. Feel free to modify and extend it for your own use.
# java-stock-portfolio-simulator
