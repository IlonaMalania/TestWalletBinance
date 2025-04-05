package com.example.testwalletbinance.data.repository

import androidx.paging.PagingSource
import com.example.testwalletbinance.data.dao.TransactionDao
import com.example.testwalletbinance.data.dao.WalletDao
import com.example.testwalletbinance.data.model.TransactionEntity
import com.example.testwalletbinance.data.model.WalletEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class WalletRepositoryImpl @Inject constructor(
    private val walletDao: WalletDao
) : WalletRepository {

    override val walletFlow: Flow<WalletEntity?> = walletDao.getWalletFlow()

    override suspend fun getWallet(): Result<WalletEntity> {
        return runCatching {
            walletDao.getWallet() ?: throw IllegalStateException("Wallet not found")
        }
    }

    override suspend fun updateBalance(newBalance: Double): Result<Unit> {
        return runCatching {
            walletDao.updateBalance(newBalance)
        }
    }

    override suspend fun insertWallet(walletEntity: WalletEntity): Result<Unit> {
        return runCatching {
            walletDao.insertWallet(walletEntity)
        }
    }
}
