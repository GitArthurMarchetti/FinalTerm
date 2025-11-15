# ☕ Cafe Order Kiosk

A modern, user-friendly point-of-sale (POS) system for cafes and coffee shops, built with Java Swing. This application provides an intuitive graphical interface for customers to browse menu items, manage their cart, and complete orders with automatic receipt generation.

## Features

- **Modern UI Design**: Beautiful coffee-themed interface with intuitive navigation
- **Category-Based Browsing**: Browse items by category (Drinks, Bakery, Sandwiches)
- **Shopping Cart Management**: Add, remove, and update quantities of items
- **Real-Time Calculations**: Automatic subtotal, tax, and total calculations
- **Receipt Generation**: Automatic receipt creation and file saving
- **Multiple Repository Support**: In-memory, file-based, and SQLite database options
- **Editable Cart**: Edit item quantities directly in the cart table
- **Responsive Design**: Clean, modern layout optimized for kiosk displays


## Requirements

- Java 17 or higher
- Maven 3.6 or higher
- SQLite JDBC driver (included as dependency)

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd FinalTermArthur
```

2. Build the project:
```bash
mvn clean package
```

## Usage

### Running the Application

After building, run the application with:

```bash
java -cp target/classes com.arthur.marchetti.Main
```

Or use Maven to compile and run:

```bash
mvn compile exec:java -Dexec.mainClass="com.arthur.marchetti.Main"
```

### Using the Kiosk

1. **Browse Categories**: Click on category buttons (☕ Drinks, 🥐 Bakery, 🥪 Sandwiches) to filter items
2. **Select Items**: Click on an item from the list to select it
3. **Set Quantity**: Use the quantity spinner to set the desired amount
4. **Add to Cart**: Click "➕ Add to Cart" to add the item
5. **Manage Cart**: 
   - Edit quantities directly in the cart table
   - Use "🗑️ Remove" to remove selected items
   - Use "🗑️ Clear" to empty the entire cart
6. **Checkout**: Click "💳 Checkout" and enter the customer name
7. **View Receipt**: A receipt dialog will display the order details and file location

### Receipt Storage

Receipts are automatically saved to:
```
~/kiosk-receipts/receipt_YYYYMMDD_HHMMSS.txt
```

## Project Structure

```
src/main/java/com/arthur/marchetti/
├── cli/                    # UI components
│   ├── MainFrame.java      # Main application window
│   ├── CartTableModel.java # Cart table data model
│   ├── ReceiptDialog.java  # Receipt display dialog
│   └── ReceiptFormatter.java # Receipt formatting utility
├── interfaces/             # Interface definitions
│   └── TaxCalculator/
│       ├── CatalogRepository.java
│       ├── ReceiptRepository.java
│       └── TaxCalculator.java
├── model/                  # Domain models
│   ├── Cart.java
│   ├── CartItem.java
│   ├── MenuItem.java
│   ├── Order.java
│   ├── Category.java
│   └── FlatRateTaxCalculator.java
├── repo/                   # Repository implementations
│   ├── InMemoryCatalogRepository.java
│   ├── FileReceiptRepository.java
│   └── SqliteReceiptRepository.java
└── services/               # Business logic
    └── ReceiptService.java
```

## Architecture

The application follows a clean architecture pattern with clear separation of concerns:

- **Presentation Layer**: Swing UI components in `cli/`
- **Business Logic**: Domain models and services
- **Data Access**: Repository pattern with multiple implementations
- **Interfaces**: Contracts for repositories and calculators

### Key Components

- **MainFrame**: Main application window with three-panel layout
- **Cart**: Shopping cart with item management and calculation capabilities
- **CatalogRepository**: Interface for accessing menu items
- **TaxCalculator**: Interface for tax calculation strategies
- **ReceiptRepository**: Interface for receipt persistence

## Configuration

### Tax Rate

The default tax rate is set to 6% (0.06) in `MainFrame.java`. To change it:

```java
private final TaxCalculator taxCalc = new FlatRateTaxCalculator(new BigDecimal("0.10")); // 10%
```

### Menu Items

Menu items are defined in `InMemoryCatalogRepository.java`. To add or modify items, edit the repository implementation.

## Testing

Run the test suite with:

```bash
mvn test
```

The project includes comprehensive unit tests for:
- Cart functionality
- Order creation
- Receipt generation
- Repository implementations

## Technologies Used

- **Java 17**: Core programming language
- **Java Swing**: GUI framework
- **Maven**: Build and dependency management
- **JUnit 5**: Testing framework
- **SQLite**: Database support (optional)

## Development

### Building

```bash
mvn clean compile
```

### Running Tests

```bash
mvn test
```

### Packaging

```bash
mvn package
```

This creates a JAR file in `target/FinalTermArthur-1.0-SNAPSHOT.jar`

## License

This project is part of a college assignment and is for educational purposes.

## Author

Arthur Marchetti

## Acknowledgments

Built as part of a final term project demonstrating object-oriented design principles, clean architecture, and modern Java development practices.

