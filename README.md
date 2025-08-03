# Sales Associate Android App

A comprehensive Android application for managing sales, inventory, and customer relationships. Built with Jetpack Compose and Room database.

## Features

### 📊 Product Management
- **Excel Integration**: Load product data from Excel files stored on the device
- **Photo Support**: Display product images from a photos folder
- **Grid Layout**: Products displayed in a 2-column grid organized by categories
- **Search Functionality**: Search products by name, description, or category
- **Product Details**: View detailed product information with admin mode for wholesale prices and notes

### 👥 Customer Management
- **Customer Database**: Add, edit, and delete customers
- **Customer Selection**: Set current customer for sales transactions
- **Customer Information**: Store customer name, pharmacy name, place, and notes

### 🛒 Sales Management
- **Purchase List**: Add items to customer's current purchase list
- **Quantity Control**: Adjust quantities with validation against available stock
- **Bill Management**: View and edit current bill with total calculations
- **Sale Confirmation**: Complete sales and update inventory automatically

### 📈 Statistics & Reporting
- **Daily Statistics**: Track daily sales totals and customer counts
- **Date Filtering**: Filter statistics by specific dates
- **Sales History**: View historical sales data

### 💾 Data Persistence
- **Room Database**: Local SQLite database for all data
- **Excel Import**: Read product data from Excel files
- **Photo Management**: Store and display product photos

## Technical Architecture

### Data Models
- **Product**: Item details, pricing, inventory, photos
- **Customer**: Customer information and purchase history
- **Sale**: Transaction records with items and totals
- **DailyStats**: Daily sales statistics

### Key Components
- **Repository Pattern**: Centralized data access layer
- **ViewModel**: UI state management with coroutines
- **Room Database**: Local data persistence
- **Jetpack Compose**: Modern UI framework
- **Navigation**: Screen navigation with NavHost

### Dependencies
- **Apache POI**: Excel file reading
- **Coil**: Image loading and caching
- **Room**: Database ORM
- **Navigation Compose**: Screen navigation
- **Material 3**: Modern UI components

## Usage Instructions

### 1. Initial Setup
1. Place your Excel file in the app's Excel directory
2. Add product photos to the photos directory
3. The Excel file should have columns: Item, Description, Units, Category, Amount, CostPerUnit, WholesalePrice, Notes, ExpiryDate, Barcode, Address

### 2. Loading Data
1. Navigate to Settings (gear icon in top bar)
2. Upload your Excel file
3. Add product photos
4. Return to main screen to see products

### 3. Managing Customers
1. Click the Customers button (shows current customer name)
2. Add new customers with required information
3. Select a customer as current customer
4. The customer name will appear on the main button

### 4. Making Sales
1. Browse products in the main grid
2. Click on a product to view details
3. Select quantity and add to cart
4. Click the Cart button to view purchase list
5. Edit quantities or remove items as needed
6. Confirm sale to complete transaction

### 5. Viewing Statistics
1. Click the Stats button
2. View daily sales statistics
3. Filter by specific dates
4. Track total sales and customer counts

## File Structure

```
app/src/main/java/com/example/salesassociate/
├── data/
│   ├── Models.kt          # Data classes and entities
│   ├── Database.kt        # Room database and DAOs
│   └── Repository.kt      # Data access layer
├── ui/
│   ├── screens/           # Screen components
│   └── components/        # Reusable UI components
├── utils/
│   ├── ExcelReader.kt     # Excel file processing
│   └── FileManager.kt     # File management utilities
├── viewmodel/
│   └── MainViewModel.kt   # UI state management
└── MainActivity.kt        # App entry point
```

## Database Schema

### Products Table
- `item` (Primary Key): Product name
- `description`: Product description
- `units`: Unit of measurement
- `category`: Product category
- `amount`: Available quantity
- `costPerUnit`: Unit price
- `wholesalePrice`: Wholesale price
- `notes`: Additional notes
- `expiryDate`: Expiration date
- `barcode`: Product barcode
- `address`: Photo file path

### Customers Table
- `id` (Primary Key): Auto-generated ID
- `name`: Customer name
- `note`: Optional notes
- `place`: Customer location
- `pharmacyName`: Pharmacy name
- `currentPurchaseList`: JSON array of current items
- `purchaseHistory`: JSON array of past sales

### Sales Table
- `id` (Primary Key): Auto-generated ID
- `customerId`: Reference to customer
- `items`: JSON array of sold items
- `totalAmount`: Total sale amount
- `date`: Sale date
- `timestamp`: Sale timestamp

### DailyStats Table
- `date` (Primary Key): Date
- `totalSales`: Total sales for the day
- `customerCount`: Number of customers for the day

## Development Notes

### Excel File Format
The app expects Excel files with the following column structure:
1. Item (Product name)
2. Description
3. Units
4. Category
5. Amount (Available quantity)
6. CostPerUnit
7. WholesalePrice
8. Notes
9. ExpiryDate
10. Barcode
11. Address (Photo file path)

### Photo Management
- Photos should be stored in the app's photos directory
- Photo file paths in Excel should match the actual file names
- Supported formats: JPG, JPEG, PNG, GIF

### Data Persistence
- All data is stored locally using Room database
- Excel files and photos are stored in app-specific directories
- Data persists between app sessions

## Building and Running

1. Open the project in Android Studio
2. Sync Gradle dependencies
3. Build the project
4. Run on an Android device or emulator (API 35+)

## Requirements

- Android API 35+ (Android 15.0)
- Kotlin DSL build system
- Jetpack Compose UI
- Room database
- Apache POI for Excel reading
- Coil for image loading

## License

This project is developed for educational and commercial use. 