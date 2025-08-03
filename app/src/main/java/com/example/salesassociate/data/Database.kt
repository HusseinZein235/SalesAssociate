package com.example.salesassociate.data

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.datetime.LocalDate

@Database(
    entities = [Product::class, Customer::class, Sale::class, DailyStats::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun saleDao(): SaleDao
    abstract fun dailyStatsDao(): DailyStatsDao
}

class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.toString()
    }
    
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { 
            try {
                LocalDate.parse(it)
            } catch (e: Exception) {
                null // Return null instead of default date
            }
        }
    }
    
    @TypeConverter
    fun fromPurchaseItemList(value: List<PurchaseItem>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toPurchaseItemList(value: String): List<PurchaseItem> {
        return try {
            val listType = object : TypeToken<List<PurchaseItem>>() {}.type
            gson.fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    @TypeConverter
    fun fromSaleList(value: List<Sale>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toSaleList(value: String): List<Sale> {
        return try {
            val listType = object : TypeToken<List<Sale>>() {}.type
            gson.fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY category, item")
    suspend fun getAllProducts(): List<Product>
    
    @Query("SELECT * FROM products WHERE category = :category")
    suspend fun getProductsByCategory(category: String): List<Product>
    
    @Query("SELECT * FROM products WHERE item LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    suspend fun searchProducts(query: String): List<Product>
    
    @Query("SELECT * FROM products WHERE item = :itemName")
    suspend fun getProductByName(itemName: String): Product?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)
    
    @Update
    suspend fun updateProduct(product: Product)
    
    @Delete
    suspend fun deleteProduct(product: Product)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers")
    suspend fun getAllCustomers(): List<Customer>
    
    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Int): Customer?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long
    
    @Update
    suspend fun updateCustomer(customer: Customer)
    
    @Delete
    suspend fun deleteCustomer(customer: Customer)
}

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY timestamp DESC")
    suspend fun getAllSales(): List<Sale>
    
    @Query("SELECT * FROM sales WHERE customerId = :customerId")
    suspend fun getSalesByCustomer(customerId: Int): List<Sale>
    
    @Query("SELECT * FROM sales WHERE date = :date ORDER BY timestamp DESC")
    suspend fun getSalesByDate(date: LocalDate): List<Sale>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale): Long
}

@Dao
interface DailyStatsDao {
    @Query("SELECT * FROM daily_stats WHERE date = :date")
    suspend fun getDailyStats(date: LocalDate): DailyStats?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyStats(stats: DailyStats)
    
    @Query("SELECT * FROM daily_stats ORDER BY date DESC")
    suspend fun getAllDailyStats(): List<DailyStats>
} 