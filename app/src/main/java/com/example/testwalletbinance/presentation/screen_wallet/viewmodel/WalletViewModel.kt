package com.example.testwalletbinance.presentation.screen_wallet.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.testwalletbinance.data.model.TransactionEntity
import com.example.testwalletbinance.data.model.WalletEntity
import com.example.testwalletbinance.data.repository.BitcoinRepository
import com.example.testwalletbinance.data.repository.TransactionRepository
import com.example.testwalletbinance.data.repository.WalletRepository
import com.example.testwalletbinance.presentation.screen_add_transaction.TransactionCategory
import com.example.testwalletbinance.ui.RateUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val bitcoinRepository: BitcoinRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val walletFlow: StateFlow<WalletEntity?> = walletRepository.walletFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            null
        )

    private val _rateState = MutableStateFlow<RateUiState>(RateUiState.Loading)
    val rateState: StateFlow<RateUiState> = _rateState.asStateFlow()

    //pagination
    val transactionsFlow = Pager(
        config = PagingConfig(pageSize = 20)
    ) {
        transactionRepository.getTransactionsPaged()
    }.flow.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            walletRepository.getWallet()
                .onFailure {
                    walletRepository.insertWallet(WalletEntity(balance = 0.0))
                }
        }
        loadRate()
    }


    fun loadRate() {
        viewModelScope.launch {
            _rateState.value = RateUiState.Loading

            runCatching {
                bitcoinRepository.getBitcoinRate()
            }.onSuccess { rate ->
                _rateState.value = RateUiState.Success(rate)
            }.onFailure { error ->
                _rateState.value = RateUiState.Error(error.message ?: "Unknown error")
            }
        }
    }



    fun topUpBalance(amount: Double) {
        viewModelScope.launch {
            walletRepository.getWallet()
                .mapCatching { wallet ->
                    val newBalance = (wallet.balance ?: 0.0) + amount
                    walletRepository.updateBalance(newBalance).getOrThrow()
                }.mapCatching {
                    transactionRepository.addTransaction(
                        TransactionEntity(
                            amount = amount,
                            category = TransactionCategory.TOP_UP.displayName,
                            timestamp = System.currentTimeMillis()
                        )
                    ).getOrThrow()
                }.onFailure {
                    Log.e("WalletViewModel", "Top-up failed: ${it.message}")
                }
        }
    }
}
