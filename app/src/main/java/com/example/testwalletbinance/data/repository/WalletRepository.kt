package com.example.testwalletbinance.data.repository

import androidx.paging.PagingSource
import com.example.testwalletbinance.data.dao.TransactionDao
import com.example.testwalletbinance.data.dao.WalletDao
import com.example.testwalletbinance.data.model.TransactionEntity
import com.example.testwalletbinance.data.model.WalletEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val walletDao: WalletDao,
    private val transactionDao: TransactionDao
) {
    val walletFlow: Flow<WalletEntity?> = walletDao.getWalletFlow()

    fun getTransactionsPaged(): PagingSource<Int, TransactionEntity> {
        return transactionDao.getTransactionsPaged()
    }

    suspend fun updateBalance(newBalance: Double) {
        walletDao.updateBalance(newBalance)
    }

    suspend fun addTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun clearTransactions() {
        transactionDao.clearTransactions()
    }

    suspend fun insertWallet(walletEntity: WalletEntity) {
        walletDao.insertWallet(walletEntity)
    }

    suspend fun getWallet(): WalletEntity? {
        return walletDao.getWallet()
    }
}
