package com.example.testwalletbinance.presentation.screen_add_transaction.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testwalletbinance.data.model.TransactionEntity
import com.example.testwalletbinance.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {

    fun addTransaction(amount: Double, category: String) {
        viewModelScope.launch {
            // Add the transaction
            val transaction = TransactionEntity(
                amount = amount,
                category = category,
                timestamp = System.currentTimeMillis()
            )
            repository.addTransaction(transaction)

            // Update wallet balance
            val currentBalance = repository.getWallet()?.balance ?: 0.0
            val newBalance = currentBalance - amount
            repository.updateBalance(newBalance)
        }
    }

}
