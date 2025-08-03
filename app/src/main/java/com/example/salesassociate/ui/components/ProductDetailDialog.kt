package com.example.salesassociate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.salesassociate.data.Product
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailDialog(
    product: Product,
    isVisible: Boolean,
    isAdminMode: Boolean,
    selectedAmount: Int,
    onDismiss: () -> Unit,
    onToggleAdminMode: () -> Unit,
    onAmountChange: (Int) -> Unit,
    onAddToPurchaseList: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = product.item,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    // Product Image
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.address)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.item,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Product Details
                    ProductDetailRow("Description", product.description)
                    ProductDetailRow("Units", product.units)
                    ProductDetailRow("Category", product.category)
                    ProductDetailRow("Amount", "${product.amount} ${product.units}")
                    ProductDetailRow("Cost per Unit", "$${product.costPerUnit}")
                    ProductDetailRow("Expiry Date", product.expiryDate.toString())
                    ProductDetailRow("Barcode", product.barcode)
                    
                    // Admin mode details
                    if (isAdminMode) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = "Admin Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        ProductDetailRow("Wholesale Price", "$${product.wholesalePrice}")
                        ProductDetailRow("Notes", product.notes)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Amount Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quantity:",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        
                        IconButton(
                            onClick = { if (selectedAmount > 1) onAmountChange(selectedAmount - 1) }
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
                        }
                        
                        Text(
                            text = selectedAmount.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        IconButton(
                            onClick = { if (selectedAmount < product.amount) onAmountChange(selectedAmount + 1) }
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
                        }
                    }
                    
                    Text(
                        text = "Available: ${product.amount} ${product.units}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Admin Mode Toggle
                    TextButton(onClick = onToggleAdminMode) {
                        Icon(
                            if (isAdminMode) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isAdminMode) "Hide Admin" else "Admin Mode")
                    }
                    
                    // Add to Purchase List
                    Button(
                        onClick = onAddToPurchaseList,
                        enabled = selectedAmount > 0 && selectedAmount <= product.amount
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add to Cart")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProductDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f)
        )
    }
}