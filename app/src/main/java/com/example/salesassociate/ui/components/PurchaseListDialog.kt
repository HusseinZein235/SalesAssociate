package com.example.salesassociate.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.salesassociate.data.Customer
import com.example.salesassociate.data.PurchaseItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseListDialog(
    customer: Customer?,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onUpdateItemAmount: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onConfirmSale: () -> Unit,
    onResetCart: () -> Unit
) {
    if (isVisible && customer != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Purchase List - ${customer.pharmacyName}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    if (customer.currentPurchaseList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No items in cart",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 400.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(customer.currentPurchaseList) { item ->
                                PurchaseItemCard(
                                    item = item,
                                    onUpdateAmount = { newAmount ->
                                        onUpdateItemAmount(item.item, newAmount)
                                    },
                                    onRemove = {
                                        onRemoveItem(item.item)
                                    }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Total
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Total: $${String.format("%.2f", customer.currentPurchaseList.sumOf { it.totalCost })}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Items: ${customer.currentPurchaseList.size}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onResetCart) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset")
                    }
                    
                    Button(
                        onClick = onConfirmSale,
                        enabled = customer.currentPurchaseList.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirm Sale")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun PurchaseItemCard(
    item: PurchaseItem,
    onUpdateAmount: (Int) -> Unit,
    onRemove: () -> Unit
) {
    var showAmountDialog by remember { mutableStateOf(false) }
    var tempAmount by remember { mutableStateOf(item.currentSaleAmount.toString()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.item,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Quantity: ${item.currentSaleAmount}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Total: $${String.format("%.2f", item.totalCost)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                IconButton(onClick = { showAmountDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Amount")
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                }
            }
        }
    }
    
    // Amount Edit Dialog
    if (showAmountDialog) {
        AlertDialog(
            onDismissRequest = { showAmountDialog = false },
            title = { Text("Edit Quantity") },
            text = {
                OutlinedTextField(
                    value = tempAmount,
                    onValueChange = { tempAmount = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        tempAmount.toIntOrNull()?.let { amount ->
                            if (amount > 0) {
                                onUpdateAmount(amount)
                            }
                        }
                        showAmountDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAmountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 