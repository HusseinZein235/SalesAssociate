package com.example.salesassociate.data

import android.content.Context
import com.example.salesassociate.utils.ExcelReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class Repository(
    private val database: AppDatabase,
    private val context: Context
) {
    private val excelReader = ExcelReader(context)
    
    // Product operations
    suspend fun loadProductsFromExcel(filePath: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            // Clear existing products to ignore sample data
            database.productDao().deleteAllProducts()
            
            val products = excelReader.readProductsFromExcel(filePath)
            database.productDao().insertProducts(products)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        val products = database.productDao().getAllProducts()
        if (products.isEmpty()) {
            // Load sample data if database is empty
            val sampleProducts = SampleData.getSampleProducts()
            database.productDao().insertProducts(sampleProducts)
            sampleProducts
        } else {
            products
        }
    }
    
    suspend fun getProductsByCategory(category: String): List<Product> = withContext(Dispatchers.IO) {
        database.productDao().getProductsByCategory(category)
    }
    
    suspend fun searchProducts(query: String): List<Product> = withContext(Dispatchers.IO) {
        database.productDao().searchProducts(query)
    }
    
    suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
        database.productDao().updateProduct(product)
    }
    
    suspend fun getProductByName(itemName: String): Product? = withContext(Dispatchers.IO) {
        database.productDao().getProductByName(itemName)
    }
    
    // Customer operations
    suspend fun getAllCustomers(): List<Customer> = withContext(Dispatchers.IO) {
        val customers = database.customerDao().getAllCustomers()
        if (customers.isEmpty()) {
            // Load sample customers if database is empty
            val sampleCustomers = SampleData.getSampleCustomers()
            sampleCustomers.forEach { customer ->
                database.customerDao().insertCustomer(customer)
            }
            sampleCustomers
        } else {
            customers
        }
    }
    
    suspend fun getCustomerById(id: Int): Customer? = withContext(Dispatchers.IO) {
        database.customerDao().getCustomerById(id)
    }
    
    suspend fun insertCustomer(customer: Customer): Long = withContext(Dispatchers.IO) {
        database.customerDao().insertCustomer(customer)
    }
    
    suspend fun updateCustomer(customer: Customer) = withContext(Dispatchers.IO) {
        database.customerDao().updateCustomer(customer)
    }
    
    suspend fun deleteCustomer(customer: Customer) = withContext(Dispatchers.IO) {
        database.customerDao().deleteCustomer(customer)
    }
    
    // Sale operations
    suspend fun insertSale(sale: Sale): Long = withContext(Dispatchers.IO) {
        database.saleDao().insertSale(sale)
    }
    
    suspend fun getSalesByCustomer(customerId: Int): List<Sale> = withContext(Dispatchers.IO) {
        database.saleDao().getSalesByCustomer(customerId)
    }
    
    suspend fun getAllSales(): List<Sale> = withContext(Dispatchers.IO) {
        database.saleDao().getAllSales()
    }
    
    // Daily stats operations
    suspend fun getDailyStats(date: LocalDate): DailyStats? = withContext(Dispatchers.IO) {
        database.dailyStatsDao().getDailyStats(date)
    }
    
    suspend fun getSalesByDate(date: LocalDate): List<Sale> = withContext(Dispatchers.IO) {
        database.saleDao().getSalesByDate(date)
    }
    
    suspend fun updateDailyStats(sale: Sale) = withContext(Dispatchers.IO) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val currentStats = database.dailyStatsDao().getDailyStats(today)
        
        val itemCount = sale.items.sumOf { it.currentSaleAmount }
        
        val updatedStats = currentStats?.copy(
            totalSales = currentStats.totalSales + sale.totalAmount,
            customerCount = currentStats.customerCount + 1,
            itemCount = currentStats.itemCount + itemCount
        ) ?: DailyStats(
            date = today,
            totalSales = sale.totalAmount,
            customerCount = 1,
            itemCount = itemCount
        )
        
        database.dailyStatsDao().insertDailyStats(updatedStats)
    }
    
    suspend fun getAllDailyStats(): List<DailyStats> = withContext(Dispatchers.IO) {
        database.dailyStatsDao().getAllDailyStats()
    }
    
    // Business logic
    suspend fun addItemToPurchaseList(
        customer: Customer,
        product: Product,
        amount: Int
    ): Customer {
        // Check if amount exceeds available stock
        val currentList = customer.currentPurchaseList.toMutableList()
        val existingItem = currentList.find { it.item == product.item }
        val currentAmountInCart = existingItem?.currentSaleAmount ?: 0
        val totalRequested = currentAmountInCart + amount
        
        if (totalRequested > product.amount) {
            throw IllegalArgumentException("Cannot add ${amount} units. Only ${product.amount - currentAmountInCart} units available.")
        }
        
        if (existingItem != null) {
            val index = currentList.indexOf(existingItem)
            currentList[index] = existingItem.copy(
                currentSaleAmount = existingItem.currentSaleAmount + amount,
                totalCost = (existingItem.currentSaleAmount + amount) * product.costPerUnit
            )
        } else {
            currentList.add(
                PurchaseItem(
                    item = product.item,
                    currentSaleAmount = amount,
                    totalCost = amount * product.costPerUnit
                )
            )
        }
        
        val updatedCustomer = customer.copy(currentPurchaseList = currentList)
        updateCustomer(updatedCustomer)
        return updatedCustomer
    }
    
    suspend fun removeItemFromPurchaseList(
        customer: Customer,
        productItem: String
    ): Customer {
        val currentList = customer.currentPurchaseList.filter { it.item != productItem }
        val updatedCustomer = customer.copy(currentPurchaseList = currentList)
        updateCustomer(updatedCustomer)
        return updatedCustomer
    }
    
    suspend fun updatePurchaseItemAmount(
        customer: Customer,
        productItem: String,
        newAmount: Int
    ): Customer {
        val product = getProductByName(productItem)
        if (product == null) {
            throw IllegalArgumentException("Product not found: $productItem")
        }
        
        if (newAmount > product.amount) {
            throw IllegalArgumentException("Cannot set amount to ${newAmount}. Only ${product.amount} units available.")
        }
        
        val currentList = customer.currentPurchaseList.map { item ->
            if (item.item == productItem) {
                item.copy(
                    currentSaleAmount = newAmount,
                    totalCost = newAmount * product.costPerUnit
                )
            } else {
                item
            }
        }
        val updatedCustomer = customer.copy(currentPurchaseList = currentList)
        updateCustomer(updatedCustomer)
        return updatedCustomer
    }
    
    suspend fun confirmSale(customer: Customer): Sale {
        val sale = Sale(
            customerId = customer.id,
            customerName = customer.name,
            customerPharmacyName = customer.pharmacyName,
            items = customer.currentPurchaseList,
            totalAmount = customer.currentPurchaseList.sumOf { it.totalCost },
            date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        )
        
        val saleId = insertSale(sale)
        val confirmedSale = sale.copy(id = saleId.toInt())
        
        // Update product amounts
        customer.currentPurchaseList.forEach { purchaseItem ->
            val product = getProductByName(purchaseItem.item)
            product?.let {
                updateProduct(it.copy(amount = it.amount - purchaseItem.currentSaleAmount))
            }
        }
        
        // Clear current purchase list
        val updatedCustomer = customer.copy(currentPurchaseList = emptyList())
        updateCustomer(updatedCustomer)
        
        // Update daily stats
        updateDailyStats(confirmedSale)
        
        return confirmedSale
    }
} 