package com.example.salesassociate.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.salesassociate.data.Customer
import com.example.salesassociate.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentCustomer by viewModel.currentCustomer.collectAsStateWithLifecycle()
    
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var editingCustomer by remember { mutableStateOf<Customer?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customers") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddCustomerDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Customer")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.customers) { customer ->
                CustomerCard(
                    customer = customer,
                    isSelected = currentCustomer?.id == customer.id,
                    onSelect = { viewModel.setCurrentCustomer(customer) },
                    onEdit = { editingCustomer = customer },
                    onDelete = {
                        viewModel.deleteCustomer(customer)
                    }
                )
            }
        }
    }
    
    // Add Customer Dialog
    if (showAddCustomerDialog) {
        CustomerDialog(
            customer = null,
            onDismiss = { showAddCustomerDialog = false },
            onSave = { name, note, place, pharmacyName ->
                viewModel.addCustomer(name, note, place, pharmacyName)
                showAddCustomerDialog = false
            }
        )
    }
    
    // Edit Customer Dialog
    editingCustomer?.let { customer ->
        CustomerDialog(
            customer = customer,
            onDismiss = { editingCustomer = null },
            onSave = { name, note, place, pharmacyName ->
                viewModel.updateCustomer(customer, name, note, place, pharmacyName)
                editingCustomer = null
            }
        )
    }
}

@Composable
fun CustomerCard(
    customer: Customer,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = customer.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = customer.pharmacyName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = customer.place,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    customer.note?.let { note ->
                        if (note.isNotEmpty()) {
                            Text(
                                text = "Note: $note",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Column {
                    if (isSelected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
            
            if (!isSelected) {
                Button(
                    onClick = onSelect,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Select as Current Customer")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDialog(
    customer: Customer?,
    onDismiss: () -> Unit,
    onSave: (String, String?, String, String) -> Unit
) {
    var name by remember { mutableStateOf(customer?.name ?: "") }
    var note by remember { mutableStateOf(customer?.note ?: "") }
    var place by remember { mutableStateOf(customer?.place ?: "") }
    var pharmacyName by remember { mutableStateOf(customer?.pharmacyName ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (customer == null) "Add Customer" else "Edit Customer")
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = pharmacyName,
                    onValueChange = { pharmacyName = it },
                    label = { Text("Pharmacy Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = place,
                    onValueChange = { place = it },
                    label = { Text("Place") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotEmpty() && pharmacyName.isNotEmpty() && place.isNotEmpty()) {
                        onSave(name, note.takeIf { it.isNotEmpty() }, place, pharmacyName)
                    }
                },
                enabled = name.isNotEmpty() && pharmacyName.isNotEmpty() && place.isNotEmpty()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 