package com.example.testwalletbinance.presentation.screen_add_transaction.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testwalletbinance.data.model.TransactionEntity
import com.example.testwalletbinance.data.repository.TransactionRepository
import com.example.testwalletbinance.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    //add the transaction
    fun addTransaction(amount: Double, category: String) {
        viewModelScope.launch {
            walletRepository.getWallet()
                .mapCatching { wallet ->
                    val newBalance = (wallet.balance ?: 0.0) - amount
                    walletRepository.updateBalance(newBalance).getOrThrow()
                }.mapCatching {
                    transactionRepository.addTransaction(
                        TransactionEntity(
                            amount = amount,
                            category = category,
                            timestamp = System.currentTimeMillis()
                        )
                    ).getOrThrow()
                }.onFailure {
                    Log.e("TransactionViewModel", "Add transaction failed: ${it.message}")
                }
        }
    }
}
