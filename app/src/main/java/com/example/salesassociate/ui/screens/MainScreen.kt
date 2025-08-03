package com.example.salesassociate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.salesassociate.data.Product
import com.example.salesassociate.viewmodel.MainViewModel
import com.example.salesassociate.ui.components.ProductDetailDialog
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToCustomers: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onShowPurchaseList: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentCustomer by viewModel.currentCustomer.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var showProductDialog by remember { mutableStateOf(false) }
    var showAdminMode by remember { mutableStateOf(false) }
    var selectedAmount by remember { mutableStateOf(1) }
    
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales Associate") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search and Action Buttons
            SearchAndActionBar(
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                currentCustomerName = uiState.currentCustomerName,
                onNavigateToCustomers = onNavigateToCustomers,
                onNavigateToStats = onNavigateToStats,
                onShowPurchaseList = onShowPurchaseList
            )
            
            // Products Grid
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    filteredProducts.forEach { (category, products) ->
                        item {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        item {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.heightIn(max = 400.dp)
                            ) {
                                items(products) { product ->
                                    ProductCard(
                                        product = product,
                                        onClick = {
                                            selectedProduct = product
                                            showProductDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Product Detail Dialog
    selectedProduct?.let { product ->
        ProductDetailDialog(
            product = product,
            isVisible = showProductDialog,
            isAdminMode = showAdminMode,
            selectedAmount = selectedAmount,
            onDismiss = {
                showProductDialog = false
                selectedProduct = null
                showAdminMode = false
                selectedAmount = 1
            },
            onToggleAdminMode = { showAdminMode = !showAdminMode },
            onAmountChange = { selectedAmount = it },
            onAddToPurchaseList = {
                viewModel.addItemToPurchaseList(product, selectedAmount)
                showProductDialog = false
                selectedProduct = null
                showAdminMode = false
                selectedAmount = 1
            }
        )
    }
    
    // Error Dialog
    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = viewModel::clearError,
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = viewModel::clearError) {
                    Text("OK")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndActionBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    currentCustomerName: String,
    onNavigateToCustomers: () -> Unit,
    onNavigateToStats: () -> Unit,
    onShowPurchaseList: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Search products...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onNavigateToCustomers,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.People, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentCustomerName.ifEmpty { "Customers" },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onNavigateToStats,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Analytics, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Stats")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onShowPurchaseList,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cart")
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
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
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(id = android.R.drawable.ic_menu_gallery)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Product Info
            Text(
                text = product.item,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "Amount: ${product.amount} ${product.units}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Price: $${product.costPerUnit}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Expires: ${product.expiryDate}",
                style = MaterialTheme.typography.bodySmall,
                color = if (product.expiryDate < Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date) {
                    Color.Red
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
} 