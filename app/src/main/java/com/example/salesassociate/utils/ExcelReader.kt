package com.example.salesassociate.utils

import android.content.Context
import com.example.salesassociate.data.Product
import kotlinx.datetime.LocalDate
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream

class ExcelReader(private val context: Context) {
    
    fun readProductsFromExcel(filePath: String): List<Product> {
        val products = mutableListOf<Product>()
        
        try {
            val file = File(filePath)
            if (!file.exists()) {
                throw Exception("Excel file not found at: $filePath")
            }
            
            val inputStream = FileInputStream(file)
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0) // First sheet
            
            // Skip header row
            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue
                
                try {
                    val product = Product(
                        item = getCellValue(row.getCell(0)) ?: "",
                        description = getCellValue(row.getCell(1)) ?: "",
                        units = getCellValue(row.getCell(2)) ?: "",
                        category = getCellValue(row.getCell(3)) ?: "",
                        amount = getCellValue(row.getCell(4))?.toIntOrNull() ?: 0,
                        costPerUnit = getCellValue(row.getCell(5))?.toDoubleOrNull() ?: 0.0,
                        wholesalePrice = getCellValue(row.getCell(6))?.toDoubleOrNull() ?: 0.0,
                        notes = getCellValue(row.getCell(7)) ?: "",
                        expiryDate = parseDate(getCellValue(row.getCell(8))),
                        barcode = getCellValue(row.getCell(9)) ?: "",
                        address = getCellValue(row.getCell(10)) ?: ""
                    )
                    
                    if (product.item.isNotEmpty()) {
                        products.add(product)
                    }
                } catch (e: Exception) {
                    // Skip invalid rows
                    continue
                }
            }
            
            workbook.close()
            inputStream.close()
            
        } catch (e: Exception) {
            throw Exception("Error reading Excel file: ${e.message}")
        }
        
        return products
    }
    
    private fun getCellValue(cell: org.apache.poi.ss.usermodel.Cell?): String? {
        return when (cell?.cellType) {
            org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> cell.numericCellValue.toString()
            org.apache.poi.ss.usermodel.CellType.BOOLEAN -> cell.booleanCellValue.toString()
            else -> null
        }
    }
    
    private fun parseDate(dateString: String?): LocalDate {
        if (dateString.isNullOrEmpty()) {
            return LocalDate.parse("2025-12-31") // Default expiry date
        }
        
        return try {
            // Try different date formats
            when {
                dateString.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> LocalDate.parse(dateString)
                dateString.matches(Regex("\\d{2}/\\d{2}/\\d{4}")) -> {
                    val parts = dateString.split("/")
                    LocalDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
                }
                dateString.matches(Regex("\\d{2}-\\d{2}-\\d{4}")) -> {
                    val parts = dateString.split("-")
                    LocalDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
                }
                else -> LocalDate.parse("2025-12-31")
            }
        } catch (e: Exception) {
            LocalDate.parse("2025-12-31")
        }
    }
} 