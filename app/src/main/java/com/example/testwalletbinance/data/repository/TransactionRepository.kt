package com.example.testwalletbinance.data.repository

import androidx.paging.PagingSource
import com.example.testwalletbinance.data.model.TransactionEntity

interface TransactionRepository {
    fun getTransactionsPaged(): PagingSource<Int, TransactionEntity>
    suspend fun addTransaction(transaction: TransactionEntity): Result<Unit>
    suspend fun clearTransactions(): Result<Unit>
}