package com.example.salesassociate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.salesassociate.data.AppDatabase
import com.example.salesassociate.data.Repository
import com.example.salesassociate.ui.screens.*
import com.example.salesassociate.ui.components.PurchaseListDialog
import com.example.salesassociate.ui.theme.SalesAssociateTheme
import com.example.salesassociate.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize database
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "sales_associate_db_v3" // Changed database name to force recreation
        ).fallbackToDestructiveMigration().build()
        
        // Initialize repository
        val repository = Repository(database, applicationContext)
        
        setContent {
            SalesAssociateTheme {
                SalesAssociateApp(repository = repository)
            }
        }
    }
}

// ViewModel Factory to handle repository dependency
class MainViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun SalesAssociateApp(repository: Repository) {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(repository))
    
    var showPurchaseListDialog by remember { mutableStateOf(false) }
    
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onNavigateToCustomers = { navController.navigate("customers") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToSettings = { navController.navigate("settings") },
                onShowPurchaseList = { showPurchaseListDialog = true }
            )
        }
        
        composable("customers") {
            CustomersScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("stats") {
            StatsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLoadExcelFile = { filePath ->
                    viewModel.loadProductsFromExcel(filePath)
                }
            )
        }
    }
    
    // Purchase List Dialog
    val currentCustomer by viewModel.currentCustomer.collectAsStateWithLifecycle()
    PurchaseListDialog(
        customer = currentCustomer,
        isVisible = showPurchaseListDialog,
        onDismiss = { showPurchaseListDialog = false },
        onUpdateItemAmount = { itemName, newAmount ->
            viewModel.updatePurchaseItemAmount(itemName, newAmount)
        },
        onRemoveItem = { itemName ->
            viewModel.removeItemFromPurchaseList(itemName)
        },
        onConfirmSale = {
            viewModel.confirmSale()
            showPurchaseListDialog = false
        },
        onResetCart = {
            // Reset cart logic
            showPurchaseListDialog = false
        }
    )
}