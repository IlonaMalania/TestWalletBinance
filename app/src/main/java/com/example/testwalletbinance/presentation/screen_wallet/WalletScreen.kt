package com.example.testwalletbinance.presentation.screen_wallet

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.paging.LoadState
import com.example.testwalletbinance.R
import com.example.testwalletbinance.presentation.screen_wallet.viewmodel.WalletViewModel


@ExperimentalMaterial3Api
@Composable
fun WalletScreen(
    viewModel: WalletViewModel,
    onAddTransactionClick: () -> Unit
) {
    val walletState by viewModel.walletFlow.collectAsState(initial = null)
    val transactions = viewModel.transactionsFlow.collectAsLazyPagingItems()

    var showDialog by remember { mutableStateOf(false) }
    var topUpAmount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.bitcoin_wallet)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Balance
            Text(
                text = stringResource(R.string.wallet_balance, walletState?.balance ?: 0.0),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Top Up Balance Button
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(stringResource(R.string.top_up))
            }

            // Add Transaction Button
            Button(
                onClick = onAddTransactionClick,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(stringResource(R.string.add_transaction))
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Add list of Transactions
            LazyColumn {
                items(transactions.itemCount) { index ->
                    val transaction = transactions[index]
                    transaction?.let { TransactionItem(it) }
                }

                //To check the state of loading data
                transactions.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            Log.d("Pagination", "Loading initial page...")
                        }
                        loadState.append is LoadState.Loading -> {
                            Log.d("Pagination", "Loading next page...")
                        }
                        loadState.append is LoadState.Error -> {
                            val error = loadState.append as LoadState.Error
                            Log.e("Pagination", "Error loading next page: ${error.error.message}")
                        }
                        loadState.refresh is LoadState.NotLoading -> {
                            Log.d("Pagination", "Initial data loaded")
                        }
                    }
                }
            }
        }

        // Dialog for Top Up
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            val amount = topUpAmount.toDoubleOrNull()
                            if (amount != null && amount > 0) {
                                viewModel.topUpBalance(amount)
                                showDialog = false
                                topUpAmount = ""
                            }
                        }
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                },
                title = { Text(stringResource(R.string.top_up_balance)) },
                text = {
                    OutlinedTextField(
                        value = topUpAmount,
                        onValueChange = { topUpAmount = it },
                        label = { Text(stringResource(R.string.amount_in_btc)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            )
        }
    }
}
