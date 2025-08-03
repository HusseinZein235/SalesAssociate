package com.example.salesassociate.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val item: String,
    val description: String,
    val units: String,
    val category: String,
    val amount: Int,
    val costPerUnit: Double,
    val wholesalePrice: Double,
    val notes: String,
    val expiryDate: LocalDate,
    val barcode: String,
    val address: String // Photo file path
)

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val note: String?,
    val place: String,
    val pharmacyName: String,
    val currentPurchaseList: List<PurchaseItem> = emptyList(),
    val purchaseHistory: List<Sale> = emptyList()
)

data class PurchaseItem(
    val item: String,
    val currentSaleAmount: Int,
    val totalCost: Double
)

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: Int,
    val items: List<PurchaseItem>,
    val totalAmount: Double,
    val date: LocalDate,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_stats")
data class DailyStats(
    @PrimaryKey val date: LocalDate,
    val totalSales: Double,
    val customerCount: Int
) 