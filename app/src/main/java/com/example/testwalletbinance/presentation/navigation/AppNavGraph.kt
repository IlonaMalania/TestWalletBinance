package com.example.testwalletbinance.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.testwalletbinance.presentation.navigation.AppNavGraph.ADD_TRANSACTION
import com.example.testwalletbinance.presentation.navigation.AppNavGraph.WALLET_SCREEN
import com.example.testwalletbinance.presentation.screen_add_transaction.AddTransactionScreen
import com.example.testwalletbinance.presentation.screen_wallet.WalletScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = WALLET_SCREEN) {
        composable(WALLET_SCREEN) {
            WalletScreen(hiltViewModel()) {
                navController.navigate(ADD_TRANSACTION)
            }
        }
        composable(ADD_TRANSACTION) {
            AddTransactionScreen(
                onTransactionAdded = { navController.popBackStack() },
                viewModel = hiltViewModel()
            )
        }
    }
}
object AppNavGraph {
    const val WALLET_SCREEN = "wallet"
    const val ADD_TRANSACTION = "add_transaction"
}