package com.example.testwalletbinance.presentation.screen_wallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.testwalletbinance.data.model.TransactionEntity
import com.example.testwalletbinance.data.model.WalletEntity
import com.example.testwalletbinance.data.repository.WalletRepository
import com.example.testwalletbinance.presentation.screen_add_transaction.TransactionCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    private val _walletFlow = MutableStateFlow<WalletEntity?>(null)
    val walletFlow: StateFlow<WalletEntity?> = _walletFlow.asStateFlow()

    //pagination
    val transactionsFlow = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        repository.getTransactionsPaged()
    }.flow.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            //create wallet
            val wallet = repository.getWallet()
            if (wallet == null) {
                repository.insertWallet(WalletEntity(balance = 0.0))
            }
        }
        loadWallet()
    }

    private fun loadWallet() {
        viewModelScope.launch {
            repository.walletFlow.collectLatest { wallet ->
                _walletFlow.value = wallet
            }
        }
    }

    fun topUpBalance(amount: Double) {
        viewModelScope.launch {
            val currentBalance = repository.getWallet()?.balance ?: 0.0
            val newBalance = currentBalance + amount
            repository.updateBalance(newBalance)
            repository.addTransaction(
                TransactionEntity(
                    amount = amount,
                    category = TransactionCategory.TOP_UP.displayName,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

}
