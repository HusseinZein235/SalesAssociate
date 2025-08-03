package com.example.salesassociate.utils

import android.content.Context
import com.example.salesassociate.data.Product
import kotlinx.datetime.LocalDate
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.DateUtil
import java.io.File
import java.io.FileInputStream
import java.util.Calendar

class ExcelReader(private val context: Context) {
    
    fun readProductsFromExcel(filePath: String): List<Product> {
        val products = mutableListOf<Product>()
        
        try {
            val file = File(filePath)
            if (!file.exists()) {
                throw Exception("Excel file not found at: $filePath")
            }
            
            println("DEBUG: Reading Excel file: $filePath")
            println("DEBUG: File exists: ${file.exists()}")
            println("DEBUG: File size: ${file.length()} bytes")
            
            val inputStream = FileInputStream(file)
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0) // First sheet
            
            println("DEBUG: Sheet has ${sheet.lastRowNum + 1} rows")
            println("DEBUG: Sheet name: ${sheet.sheetName}")
            
            // Find column indices from header row
            val headerRow = sheet.getRow(0)
            if (headerRow != null) {
                println("DEBUG: Header row:")
                for (colIndex in 0..10) {
                    val cell = headerRow.getCell(colIndex)
                    println("  Column $colIndex: '${cell?.stringCellValue ?: "BLANK"}'")
                }
            }
            
            // Find the expiry date column index
            val expiryDateColumnIndex = findExpiryDateColumn(headerRow)
            println("DEBUG: Expiry date column index: $expiryDateColumnIndex")
            
            // Skip header row
            for (rowIndex in 1..sheet.lastRowNum) {
                try {
                    val row = sheet.getRow(rowIndex)
                    if (row == null) {
                        println("DEBUG: Row $rowIndex is null, skipping")
                        continue
                    }
                    
                    // Debug: Print all cell values for this row
                    println("DEBUG: Row $rowIndex values:")
                    for (colIndex in 0..10) {
                        val cell = row.getCell(colIndex)
                        val cellValue = when (cell?.cellType) {
                            org.apache.poi.ss.usermodel.CellType.STRING -> "'${cell.stringCellValue}'"
                            org.apache.poi.ss.usermodel.CellType.NUMERIC -> {
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    "DATE: ${cell.dateCellValue}"
                                } else {
                                    "${cell.numericCellValue}"
                                }
                            }
                            org.apache.poi.ss.usermodel.CellType.BOOLEAN -> "${cell.booleanCellValue}"
                            org.apache.poi.ss.usermodel.CellType.BLANK -> "BLANK"
                            else -> "OTHER: ${cell?.cellType}"
                        }
                        println("  Column $colIndex: $cellValue")
                    }
                    
                    // Get expiry date cell specifically
                    val expiryDateCell = row.getCell(expiryDateColumnIndex)
                    println("DEBUG: Expiry date cell (column $expiryDateColumnIndex): ${expiryDateCell?.cellType}")
                    if (expiryDateCell != null) {
                        when (expiryDateCell.cellType) {
                            org.apache.poi.ss.usermodel.CellType.NUMERIC -> {
                                println("DEBUG: Numeric cell value: ${expiryDateCell.numericCellValue}")
                                println("DEBUG: Is date formatted: ${DateUtil.isCellDateFormatted(expiryDateCell)}")
                                if (DateUtil.isCellDateFormatted(expiryDateCell)) {
                                    val date = expiryDateCell.dateCellValue
                                    println("DEBUG: Excel date value: $date")
                                }
                            }
                            org.apache.poi.ss.usermodel.CellType.STRING -> {
                                println("DEBUG: String cell value: '${expiryDateCell.stringCellValue}'")
                            }
                            org.apache.poi.ss.usermodel.CellType.BLANK -> {
                                println("DEBUG: Expiry date cell is BLANK")
                            }
                            else -> println("DEBUG: Other cell type: ${expiryDateCell.cellType}")
                        }
                    } else {
                        println("DEBUG: Expiry date cell is null")
                    }
                    
                    val product = Product(
                        item = getCellValue(row.getCell(0)) ?: "", // Item
                        description = getCellValue(row.getCell(1)) ?: "", // Description
                        units = getCellValue(row.getCell(2)) ?: "", // Units
                        category = getCellValue(row.getCell(3)) ?: "", // Category
                        amount = getCellValueAsInt(row.getCell(4)), // Amount
                        costPerUnit = getCellValueAsDouble(row.getCell(5)) ?: 0.0, // CostPerUnit
                        wholesalePrice = getCellValueAsDouble(row.getCell(6)) ?: 0.0, // WholesalePrice
                        notes = getCellValue(row.getCell(7)) ?: "", // Notes
                        expiryDate = getCellValueAsDate(row.getCell(expiryDateColumnIndex)), // Use found column index
                        barcode = getCellValue(row.getCell(9)) ?: "", // Barcode
                        address = getCellValue(row.getCell(10)) ?: "" // Address (column 10)
                    )
                    
                    println("DEBUG: Created product ${product.item} with expiry date: ${product.expiryDate}")
                    
                    if (product.item.isNotEmpty()) {
                        products.add(product)
                        println("DEBUG: Added product ${product.item} with expiry date: ${product.expiryDate}")
                    }
                } catch (e: Exception) {
                    println("DEBUG: Error loading row $rowIndex: ${e.message}")
                    e.printStackTrace()
                    // Skip invalid rows
                    continue
                }
            }
            
            println("DEBUG: Total products loaded: ${products.size}")
            
            workbook.close()
            inputStream.close()
            
        } catch (e: Exception) {
            throw Exception("Error reading Excel file: ${e.message}")
        }
        
        return products
    }
    
    private fun findExpiryDateColumn(headerRow: org.apache.poi.ss.usermodel.Row?): Int {
        if (headerRow == null) {
            println("DEBUG: Header row is null, using default column 8")
            return 8 // Default fallback
        }
        
        for (colIndex in 0..20) { // Check up to 20 columns
            val cell = headerRow.getCell(colIndex)
            val headerValue = cell?.stringCellValue?.lowercase() ?: ""
            
            println("DEBUG: Checking column $colIndex: '$headerValue'")
            
            if (headerValue.contains("expiry") || 
                headerValue.contains("expire") || 
                headerValue.contains("expiration") ||
                headerValue.contains("expirydate") ||
                headerValue.contains("expire date") ||
                headerValue.contains("expiration date")) {
                println("DEBUG: Found expiry date column at index $colIndex")
                return colIndex
            }
        }
        
        println("DEBUG: No expiry date column found, using default column 8")
        return 8 // Default fallback
    }
    
    private fun getCellValue(cell: org.apache.poi.ss.usermodel.Cell?): String? {
        return when (cell?.cellType) {
            org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> cell.numericCellValue.toString()
            org.apache.poi.ss.usermodel.CellType.BOOLEAN -> cell.booleanCellValue.toString()
            else -> null
        }
    }
    
    // New method to properly read integer values
    private fun getCellValueAsInt(cell: org.apache.poi.ss.usermodel.Cell?): Int {
        return when (cell?.cellType) {
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> cell.numericCellValue.toInt()
            org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue.toIntOrNull() ?: 0
            else -> 0
        }
    }
    
    // New method to properly read double values
    private fun getCellValueAsDouble(cell: org.apache.poi.ss.usermodel.Cell?): Double? {
        return when (cell?.cellType) {
            org.apache.poi.ss.usermodel.CellType.NUMERIC -> cell.numericCellValue
            org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue.toDoubleOrNull()
            else -> null
        }
    }
    
    // New method to properly read Excel dates
    private fun getCellValueAsDate(cell: org.apache.poi.ss.usermodel.Cell?): LocalDate? {
        if (cell == null) {
            println("DEBUG: Date cell is null")
            return null
        }
        
        return try {
            when (cell.cellType) {
                org.apache.poi.ss.usermodel.CellType.NUMERIC -> {
                    println("DEBUG: Numeric date cell value: ${cell.numericCellValue}")
                    if (DateUtil.isCellDateFormatted(cell)) {
                        val date = cell.dateCellValue
                        println("DEBUG: Excel date value: $date")
                        val calendar = Calendar.getInstance()
                        calendar.time = date
                        LocalDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
                    } else {
                        // Try to parse as a number that might represent a date
                        val numericValue = cell.numericCellValue
                        println("DEBUG: Numeric value (not date formatted): $numericValue")
                        null
                    }
                }
                org.apache.poi.ss.usermodel.CellType.STRING -> {
                    val stringValue = cell.stringCellValue
                    println("DEBUG: String date cell value: '$stringValue'")
                    parseDate(stringValue)
                }
                org.apache.poi.ss.usermodel.CellType.BLANK -> {
                    println("DEBUG: Date cell is blank")
                    null
                }
                else -> {
                    println("DEBUG: Date cell has unexpected type: ${cell.cellType}")
                    null
                }
            }
        } catch (e: Exception) {
            println("DEBUG: Error parsing date from cell: ${e.message}")
            e.printStackTrace()
            null
        }
    }
    
    private fun parseDate(dateString: String?): LocalDate? {
        if (dateString.isNullOrEmpty() || dateString.trim().isEmpty()) {
            println("DEBUG: Date string is null or empty")
            return null
        }
        
        val trimmedDate = dateString.trim()
        println("DEBUG: Parsing date string: '$trimmedDate'")
        
        return try {
            // Try different date formats
            when {
                trimmedDate.matches(Regex("\\d{4}-\\d{1,2}-\\d{1,2}")) -> {
                    println("DEBUG: Matches YYYY-MM-DD format")
                    LocalDate.parse(trimmedDate)
                }
                trimmedDate.matches(Regex("\\d{1,2}/\\d{1,2}/\\d{4}")) -> {
                    println("DEBUG: Matches DD/MM/YYYY format")
                    val parts = trimmedDate.split("/")
                    LocalDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
                }
                trimmedDate.matches(Regex("\\d{1,2}-\\d{1,2}-\\d{4}")) -> {
                    println("DEBUG: Matches DD-MM-YYYY format")
                    val parts = trimmedDate.split("-")
                    LocalDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
                }
                trimmedDate.matches(Regex("\\d{1,2}\\.\\d{1,2}\\.\\d{4}")) -> {
                    println("DEBUG: Matches DD.MM.YYYY format")
                    val parts = trimmedDate.split(".")
                    LocalDate(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
                }
                trimmedDate.matches(Regex("\\d{4}/\\d{1,2}/\\d{1,2}")) -> {
                    println("DEBUG: Matches YYYY/MM/DD format")
                    val parts = trimmedDate.split("/")
                    LocalDate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
                }
                // New format: MM/YYYY (like 08/02027)
                trimmedDate.matches(Regex("\\d{1,2}/\\d{4}")) -> {
                    println("DEBUG: Matches MM/YYYY format")
                    val parts = trimmedDate.split("/")
                    val month = parts[0].toInt()
                    val yearString = parts[1]
                    
                    // Handle leading zero in year (like 02027 -> 2027)
                    val year = if (yearString.startsWith("0")) {
                        yearString.substring(1).toInt()
                    } else {
                        yearString.toInt()
                    }
                    
                    println("DEBUG: Parsed month: $month, year: $year (from $yearString)")
                    // Set day to 1st of the month
                    LocalDate(year, month, 1)
                }
                else -> {
                    println("DEBUG: Unrecognized date format: '$trimmedDate'")
                    // Try to parse as ISO date format
                    try {
                        LocalDate.parse(trimmedDate)
                    } catch (e: Exception) {
                        println("DEBUG: Failed to parse as ISO date: ${e.message}")
                        null
                    }
                }
            }
        } catch (e: Exception) {
            println("DEBUG: Error parsing date string '$trimmedDate': ${e.message}")
            null
        }
    }
} 