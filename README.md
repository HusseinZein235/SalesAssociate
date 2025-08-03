# Sales Associate App

A comprehensive Android application for managing product sales and inventory using Excel data and product photos.

## 🚀 Quick Start Guide

### 1. Running the App
1. Open Android Studio
2. Open the project folder: `SalesAssociate`
3. Wait for Gradle sync to complete
4. Start an Android emulator (API 35 or higher)
5. Click the "Run" button (green play icon) or press `Shift + F10`
6. The app will install and launch on the emulator

### 2. Initial Setup - Adding Your Data

#### Excel File Requirements:
- **File Name**: Any name with `.xlsx` or `.xls` extension (e.g., `products.xlsx`)
- **Required Columns** (in this exact order):
  1. **Item** - Product name
  2. **Description** - Product description
  3. **Units** - Unit of measurement (e.g., pieces, kg, etc.)
  4. **Category** - Product category
  5. **Amount** - Available quantity (must be a number)
  6. **CostPerUnit** - Price per unit (must be a number)
  7. **WholesalePrice** - Wholesale price (must be a number)
  8. **Notes** - Additional notes (optional)
  9. **ExpiryDate** - Expiry date (format: MM/YYYY, DD/MM/YYYY, or YYYY-MM-DD)
  10. **Barcode** - Product barcode
  11. **Address** - Photo filename (e.g., `product1.jpg`)

#### Photos Folder Requirements:
- **Folder Name**: Any name (e.g., `product_photos`)
- **Photo Files**: Must match the filenames in the Excel "Address" column
- **Supported Formats**: JPG, JPEG, PNG, GIF

### 3. How to Add Your Data

#### Step 1: Access Settings
1. Open the app
2. Click the **Settings** button (gear icon) in the top-right corner

#### Step 2: Upload Excel File
1. In Settings, click **"Upload Excel File"**
2. Navigate to your Excel file
3. Select and upload it
4. Wait for the upload confirmation

#### Step 3: Upload Photos Folder
1. In Settings, click **"Upload Photos Folder"**
2. Navigate to your photos folder
3. Select the entire folder
4. Wait for the upload confirmation
5. The app will automatically copy all photos to internal storage

#### Step 4: Verify Data
1. Go back to the main screen
2. You should see your products displayed in a grid
3. Each product should show its photo, name, amount, price, and expiry date

## 📱 App Usage Flow

### Main Screen (Page 0)
- **Product Grid**: Shows all products grouped by category in a 2-column layout
- **Search Bar**: Search for products by name or description
- **Customer Button**: Shows current customer name or "Customers"
- **Stats Button**: View daily sales statistics
- **Cart Button**: View current purchase list

### Product Details (Window 1)
- **Tap any product** to open detailed view
- **Product Info**: Shows item, description, units, category, amount, cost, expiry date, barcode
- **Admin Button**: Reveals wholesale price and notes, makes fields editable
- **Add to Cart**: Add product to current customer's purchase list

### Customer Management (Page 1)
- **Add Customers**: Create new customers with name, pharmacy name, place, notes
- **Select Customer**: Choose current customer for purchases
- **Edit/Delete**: Manage existing customers
- **Back Button**: Return to main screen

### Purchase List (Window 2)
- **View Items**: See all items in current purchase list
- **Edit Quantities**: Modify amounts for each item
- **Total Calculation**: Automatic price calculation
- **Confirm Sale**: Process the sale and update inventory
- **Reset**: Clear the current purchase list

### Statistics (Page 2)
- **Daily Stats**: View total sales, customer count, and item count
- **Sales History**: See detailed sales with customer information
- **Date Filter**: Filter statistics by specific date
- **Back Button**: Return to main screen

## 🔧 Troubleshooting

### App Not Appearing on Emulator
1. **Check Build**: Ensure the build completed successfully (no red errors)
2. **Restart Emulator**: Close and restart the Android emulator
3. **Clean Build**: In Android Studio, go to `Build → Clean Project`, then `Build → Rebuild Project`
4. **Check Logcat**: Look for error messages in the Logcat window

### Excel Data Not Loading
1. **Check File Format**: Ensure Excel file has `.xlsx` or `.xls` extension
2. **Verify Column Order**: Columns must be in the exact order specified above
3. **Check Data Types**: Amount and price columns must contain numbers
4. **View Debug Logs**: Check Logcat for detailed error messages

### Photos Not Displaying
1. **Check Filenames**: Photo filenames must exactly match the "Address" column in Excel
2. **Verify Upload**: Ensure photos folder was uploaded successfully
3. **Check Formats**: Only JPG, JPEG, PNG, GIF files are supported
4. **Restart App**: Close and reopen the app after uploading photos

### Expiry Dates Not Showing
1. **Check Date Format**: Use MM/YYYY, DD/MM/YYYY, or YYYY-MM-DD format
2. **Verify Column**: Ensure "ExpiryDate" column exists and is in the correct position
3. **Check Data**: Ensure date cells are not empty or contain invalid data

### Purchase Issues
1. **Check Stock**: Ensure product has sufficient quantity
2. **Select Customer**: Make sure a customer is selected before adding items
3. **Verify Amounts**: Purchase amounts cannot exceed available stock

## 📊 Data Management

### Database Location
The app stores all data locally on the device:
- **Database**: `sales_associate_db_v3` (SQLite)
- **Files**: Stored in app's internal storage
- **Photos**: Copied to app's internal photos directory

### Data Persistence
- All data persists between app sessions
- Sample data is automatically loaded if no Excel file is uploaded
- Excel data replaces sample data when uploaded

### Excel File Synchronization
The app now automatically updates your Excel file when changes are made:

#### What Gets Updated:
1. **Product Amounts**: When you confirm a sale, the product quantities are automatically reduced in your Excel file
2. **Product Notes**: When you edit notes in admin mode, changes are saved back to the Excel file
3. **Expiry Dates**: Any changes to expiry dates are reflected in the Excel file

#### How It Works:
1. **Upload Excel**: First, upload your Excel file through the Settings screen
2. **Make Changes**: Use the app normally - make sales, edit notes, etc.
3. **Automatic Sync**: The app automatically updates your Excel file in the background
4. **File Location**: Updated Excel file is stored in the app's internal storage

#### Important Notes:
- The Excel file must be uploaded through the app's Settings screen for synchronization to work
- Only the uploaded Excel file is updated - other Excel files on your device remain unchanged
- The app maintains the original Excel structure and formatting
- All updates are performed safely with error handling

### Backup and Restore
- Currently, data is stored locally only
- To backup: Export your Excel file and photos folder
- To restore: Re-upload the Excel file and photos folder
- **Excel Updates**: Your Excel file is automatically kept up-to-date with all app changes

## 🎯 Key Features

### Inventory Management
- Real-time stock tracking
- Automatic quantity updates after sales
- Out-of-stock indicators
- Expiry date monitoring
- **Excel File Synchronization**: Automatically updates your Excel file when inventory changes

### Customer Management
- Add, edit, and delete customers
- Track customer purchase history
- Pharmacy name display
- Customer-specific purchase lists

### Sales Tracking
- Daily sales statistics
- Customer-specific sales history
- Item-level sales tracking
- Automatic total calculations

### Admin Features
- Wholesale price access
- Editable product notes
- Stock management
- Sales analytics

### Excel File Integration
- **Automatic Updates**: When you make a sale, the app automatically updates your Excel file
- **Notes Editing**: Changes to product notes in admin mode are saved back to Excel
- **Inventory Sync**: Product amounts are updated in Excel when sales are confirmed
- **Date Formatting**: Expiry dates are properly formatted in Excel
- **Real-time Sync**: All changes are immediately reflected in your Excel file

## 🔍 Debug Information

The app includes extensive debug logging to help troubleshoot issues:
- Excel file reading process
- Photo path resolution
- Database operations
- Sales processing

To view debug logs:
1. Open Android Studio
2. Go to `View → Tool Windows → Logcat`
3. Filter by your app package: `com.example.salesassociate`
4. Look for "DEBUG:" messages

## 📞 Support

If you encounter issues:
1. Check the debug logs in Logcat
2. Verify your Excel file format and data
3. Ensure photos are properly uploaded
4. Restart the app and try again

The app is designed to be robust and user-friendly, with comprehensive error handling and detailed logging to help identify and resolve any issues quickly. 