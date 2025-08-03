package com.example.salesassociate.utils

import android.content.Context
import com.example.salesassociate.data.Product
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Calendar
import java.util.Date

class ExcelWriter(private val context: Context) {
    
    fun updateExcelFile(filePath: String, products: List<Product>): Boolean {
        return try {
            val file = File(filePath)
            if (!file.exists()) {
                println("DEBUG: Excel file not found for updating: $filePath")
                return false
            }
            
            println("DEBUG: Updating Excel file: $filePath")
            println("DEBUG: File exists: ${file.exists()}")
            println("DEBUG: File size: ${file.length()} bytes")
            
            // Read existing workbook
            val inputStream = FileInputStream(file)
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0) // First sheet
            
            println("DEBUG: Sheet has ${sheet.lastRowNum + 1} rows")
            println("DEBUG: Sheet name: ${sheet.sheetName}")
            
            // Find column indices from header row
            val headerRow = sheet.getRow(0)
            if (headerRow == null) {
                println("DEBUG: Header row is null, cannot update")
                return false
            }
            
            // Find the expiry date column index
            val expiryDateColumnIndex = findExpiryDateColumn(headerRow)
            println("DEBUG: Expiry date column index: $expiryDateColumnIndex")
            
            // Create helper for date formatting
            val creationHelper = workbook.creationHelper
            
            // Update each product in the Excel file
            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex)
                if (row == null) continue
                
                // Get the item name from the first column
                val itemCell = row.getCell(0)
                val itemName = when (itemCell?.cellType) {
                    CellType.STRING -> itemCell.stringCellValue
                    CellType.NUMERIC -> itemCell.numericCellValue.toString()
                    else -> null
                }
                
                if (itemName.isNullOrEmpty()) continue
                
                // Find the corresponding product
                val product = products.find { it.item == itemName }
                if (product != null) {
                    println("DEBUG: Updating product: ${product.item}")
                    
                    // Update Amount (column 4)
                    val amountCell = row.getCell(4)
                    if (amountCell != null) {
                        amountCell.setCellValue(product.amount.toDouble())
                        println("DEBUG: Updated amount to: ${product.amount}")
                    }
                    
                    // Update Notes (column 7)
                    val notesCell = row.getCell(7)
                    if (notesCell != null) {
                        notesCell.setCellValue(product.notes)
                        println("DEBUG: Updated notes to: ${product.notes}")
                    }
                    
                    // Update Expiry Date (using found column index)
                    val expiryCell = row.getCell(expiryDateColumnIndex)
                    if (expiryCell != null && product.expiryDate != null) {
                        // Convert LocalDate to Date for Excel
                        val calendar = Calendar.getInstance()
                        calendar.set(product.expiryDate.year, product.expiryDate.monthNumber - 1, product.expiryDate.dayOfMonth)
                        val date = calendar.time
                        
                        expiryCell.setCellValue(date)
                        val cellStyle = workbook.createCellStyle()
                        cellStyle.dataFormat = creationHelper.createDataFormat().getFormat("dd/mm/yyyy")
                        expiryCell.cellStyle = cellStyle
                        
                        println("DEBUG: Updated expiry date to: ${product.expiryDate}")
                    }
                }
            }
            
            // Save the updated workbook
            val outputStream = FileOutputStream(file)
            workbook.write(outputStream)
            outputStream.close()
            workbook.close()
            inputStream.close()
            
            println("DEBUG: Successfully updated Excel file")
            true
            
        } catch (e: Exception) {
            println("DEBUG: Error updating Excel file: ${e.message}")
            e.printStackTrace()
            false
        }
    }
    
    private fun findExpiryDateColumn(headerRow: org.apache.poi.ss.usermodel.Row): Int {
        for (colIndex in 0..20) { // Check up to 20 columns
            val cell = headerRow.getCell(colIndex)
            val headerValue = cell?.stringCellValue?.lowercase() ?: ""
            
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
    
    fun getCurrentExcelFilePath(): String? {
        val fileManager = FileManager(context)
        val excelFiles = fileManager.getExcelFiles()
        println("DEBUG: Found ${excelFiles.size} Excel files in directory")
        excelFiles.forEach { file ->
            println("DEBUG: Excel file: ${file.name} at ${file.absolutePath}")
        }
        val firstFile = excelFiles.firstOrNull()
        println("DEBUG: Returning Excel file path: ${firstFile?.absolutePath}")
        return firstFile?.absolutePath
    }
} 