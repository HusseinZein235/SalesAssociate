package com.example.salesassociate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salesassociate.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class MainViewModel(
    private val repository: Repository
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    // Current customer
    private val _currentCustomer = MutableStateFlow<Customer?>(null)
    val currentCustomer: StateFlow<Customer?> = _currentCustomer.asStateFlow()
    
    // Products grouped by category
    private val _productsByCategory = MutableStateFlow<Map<String, List<Product>>>(emptyMap())
    val productsByCategory: StateFlow<Map<String, List<Product>>> = _productsByCategory.asStateFlow()
    
    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Filtered products
    private val _filteredProducts = MutableStateFlow<Map<String, List<Product>>>(emptyMap())
    val filteredProducts: StateFlow<Map<String, List<Product>>> = _filteredProducts.asStateFlow()
    
    init {
        loadProducts()
        loadCustomers()
    }
    
    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val products = repository.getAllProducts()
                val grouped = products.groupBy { it.category }
                _productsByCategory.value = grouped
                _filteredProducts.value = grouped
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun loadProductsFromExcel(filePath: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val result = repository.loadProductsFromExcel(filePath)
                result.fold(
                    onSuccess = { products ->
                        val grouped = products.groupBy { it.category }
                        _productsByCategory.value = grouped
                        _filteredProducts.value = grouped
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun loadCustomers() {
        viewModelScope.launch {
            try {
                val customers = repository.getAllCustomers()
                _uiState.value = _uiState.value.copy(customers = customers)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun setCurrentCustomer(customer: Customer) {
        _currentCustomer.value = customer
        _uiState.value = _uiState.value.copy(
            currentCustomerName = customer.pharmacyName
        )
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterProducts()
    }
    
    private fun filterProducts() {
        val query = _searchQuery.value.lowercase()
        val allProducts = _productsByCategory.value
        
        if (query.isEmpty()) {
            _filteredProducts.value = allProducts
        } else {
            val filtered = allProducts.mapValues { (_, products) ->
                products.filter { product ->
                    product.item.lowercase().contains(query) ||
                    product.description.lowercase().contains(query) ||
                    product.category.lowercase().contains(query)
                }
            }.filter { (_, products) -> products.isNotEmpty() }
            
            _filteredProducts.value = filtered
        }
    }
    
    fun addItemToPurchaseList(product: Product, amount: Int) {
        val currentCustomer = _currentCustomer.value ?: return
        
        viewModelScope.launch {
            try {
                val updatedCustomer = repository.addItemToPurchaseList(currentCustomer, product, amount)
                _currentCustomer.value = updatedCustomer
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun removeItemFromPurchaseList(productItem: String) {
        val currentCustomer = _currentCustomer.value ?: return
        
        viewModelScope.launch {
            try {
                val updatedCustomer = repository.removeItemFromPurchaseList(currentCustomer, productItem)
                _currentCustomer.value = updatedCustomer
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun updatePurchaseItemAmount(productItem: String, newAmount: Int) {
        val currentCustomer = _currentCustomer.value ?: return
        
        viewModelScope.launch {
            try {
                val updatedCustomer = repository.updatePurchaseItemAmount(currentCustomer, productItem, newAmount)
                _currentCustomer.value = updatedCustomer
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun confirmSale() {
        val currentCustomer = _currentCustomer.value ?: return
        
        viewModelScope.launch {
            try {
                val sale = repository.confirmSale(currentCustomer)
                _currentCustomer.value = currentCustomer.copy(currentPurchaseList = emptyList())
                _uiState.value = _uiState.value.copy(
                    lastSale = sale,
                    showSaleConfirmation = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearSaleConfirmation() {
        _uiState.value = _uiState.value.copy(showSaleConfirmation = false)
    }
    
    // Customer management
    fun addCustomer(name: String, note: String?, place: String, pharmacyName: String) {
        viewModelScope.launch {
            try {
                val customer = Customer(
                    name = name,
                    note = note,
                    place = place,
                    pharmacyName = pharmacyName
                )
                repository.insertCustomer(customer)
                loadCustomers()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun updateCustomer(customer: Customer, name: String, note: String?, place: String, pharmacyName: String) {
        viewModelScope.launch {
            try {
                val updatedCustomer = customer.copy(
                    name = name,
                    note = note,
                    place = place,
                    pharmacyName = pharmacyName
                )
                repository.updateCustomer(updatedCustomer)
                loadCustomers()
                
                // Update current customer if it's the one being edited
                if (_currentCustomer.value?.id == customer.id) {
                    _currentCustomer.value = updatedCustomer
                    _uiState.value = _uiState.value.copy(
                        currentCustomerName = updatedCustomer.pharmacyName
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            try {
                repository.deleteCustomer(customer)
                loadCustomers()
                
                // Clear current customer if it's the one being deleted
                if (_currentCustomer.value?.id == customer.id) {
                    _currentCustomer.value = null
                    _uiState.value = _uiState.value.copy(currentCustomerName = "")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}

data class MainUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val customers: List<Customer> = emptyList(),
    val currentCustomerName: String = "",
    val lastSale: Sale? = null,
    val showSaleConfirmation: Boolean = false
) 