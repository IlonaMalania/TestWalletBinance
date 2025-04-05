package com.example.testwalletbinance.data.repository

import androidx.paging.PagingSource
import com.example.testwalletbinance.data.dao.TransactionDao
import com.example.testwalletbinance.data.model.TransactionEntity
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getTransactionsPaged(): PagingSource<Int, TransactionEntity> {
        return transactionDao.getTransactionsPaged()
    }

    override suspend fun addTransaction(transaction: TransactionEntity): Result<Unit> {
        return runCatching {
            transactionDao.insertTransaction(transaction)
        }
    }

    override suspend fun clearTransactions(): Result<Unit> {
        return runCatching {
            transactionDao.clearTransactions()
        }
    }
}
