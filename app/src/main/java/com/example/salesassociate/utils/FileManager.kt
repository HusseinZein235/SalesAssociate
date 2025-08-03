package com.example.salesassociate.utils

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FileManager(private val context: Context) {
    
    fun getAppDirectory(): File {
        val appDir = File(context.filesDir, "sales_associate")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return appDir
    }
    
    fun getExcelDirectory(): File {
        val excelDir = File(getAppDirectory(), "excel")
        if (!excelDir.exists()) {
            excelDir.mkdirs()
        }
        return excelDir
    }
    
    fun getPhotosDirectory(): File {
        val photosDir = File(getAppDirectory(), "photos")
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }
        return photosDir
    }
    
    fun saveExcelFile(uri: Uri, fileName: String): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val file = File(getExcelDirectory(), fileName)
            
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun saveFileFromUri(uri: Uri, type: String): String {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = "uploaded_${type}_${System.currentTimeMillis()}.${getFileExtension(uri)}"
            val directory = when (type) {
                "excel" -> getExcelDirectory()
                "photos" -> getPhotosDirectory()
                else -> getAppDirectory()
            }
            val file = File(directory, fileName)
            
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            
            file.absolutePath
        } catch (e: Exception) {
            throw Exception("Failed to save file: ${e.message}")
        }
    }
    
    fun saveFolderFromUri(uri: Uri, type: String): String {
        return try {
            // For folder upload, we'll copy all photos to internal storage
            if (type == "photos") {
                copyPhotosFromFolder(uri)
            }
            
            // Create a reference file
            val fileName = "folder_reference_${type}_${System.currentTimeMillis()}.txt"
            val directory = when (type) {
                "photos" -> getPhotosDirectory()
                else -> getAppDirectory()
            }
            val file = File(directory, fileName)

            // Write the URI as a reference
            file.writeText(uri.toString())

            file.absolutePath
        } catch (e: Exception) {
            throw Exception("Failed to save folder reference: ${e.message}")
        }
    }
    
    private fun copyPhotosFromFolder(folderUri: Uri) {
        try {
            val photosDir = getPhotosDirectory()
            val documentFile = DocumentFile.fromTreeUri(context, folderUri)
            
            if (documentFile != null && documentFile.isDirectory) {
                println("DEBUG: Copying photos from folder: ${documentFile.name}")
                
                documentFile.listFiles().forEach { file ->
                    if (file.isFile && isImageFile(file.name)) {
                        try {
                            val inputStream = context.contentResolver.openInputStream(file.uri)
                            val outputFile = File(photosDir, file.name ?: "unknown.jpg")
                            
                            inputStream?.use { input ->
                                FileOutputStream(outputFile).use { output ->
                                    input.copyTo(output)
                                }
                            }
                            
                            println("DEBUG: Copied photo: ${file.name} to ${outputFile.absolutePath}")
                        } catch (e: Exception) {
                            println("DEBUG: Error copying photo ${file.name}: ${e.message}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("DEBUG: Error copying photos from folder: ${e.message}")
        }
    }
    
    private fun isImageFile(fileName: String?): Boolean {
        if (fileName == null) return false
        val extension = fileName.lowercase()
        return extension.endsWith(".jpg") || 
               extension.endsWith(".jpeg") || 
               extension.endsWith(".png") || 
               extension.endsWith(".gif")
    }
    
    fun getPhotosFolderPath(): String? {
        val photosDir = getPhotosDirectory()
        val files = photosDir.listFiles { file -> 
            file.name.startsWith("folder_reference_photos_") 
        }
        
        return files?.firstOrNull()?.readText()
    }
    
    fun getFullImagePath(filename: String): String {
        val photosDir = getPhotosDirectory()
        val imageFile = File(photosDir, filename)
        
        println("DEBUG: Looking for image: $filename")
        println("DEBUG: Full path: ${imageFile.absolutePath}")
        println("DEBUG: File exists: ${imageFile.exists()}")
        
        return if (imageFile.exists()) {
            imageFile.absolutePath
        } else {
            // Fallback to just filename
            println("DEBUG: Image file not found, using filename: $filename")
            filename
        }
    }
    
    private fun getFileExtension(uri: Uri): String {
        val mimeType = context.contentResolver.getType(uri)
        return when (mimeType) {
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx"
            "application/vnd.ms-excel" -> "xls"
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/gif" -> "gif"
            else -> "bin"
        }
    }
    
    fun getExcelFiles(): List<File> {
        val excelDir = getExcelDirectory()
        return excelDir.listFiles()?.filter { it.extension.lowercase() in listOf("xlsx", "xls") } ?: emptyList()
    }
    
    fun getPhotoFiles(): List<File> {
        val photosDir = getPhotosDirectory()
        return photosDir.listFiles()?.filter { it.extension.lowercase() in listOf("jpg", "jpeg", "png", "gif") } ?: emptyList()
    }
    
    fun deleteFile(file: File): Boolean {
        return try {
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun getFileSize(file: File): String {
        val bytes = file.length()
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }
} 