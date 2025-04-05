package com.example.testwalletbinance.data.repository

import com.example.testwalletbinance.data.model.WalletEntity
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    val walletFlow: Flow<WalletEntity?>
    suspend fun getWallet(): Result<WalletEntity>
    suspend fun updateBalance(newBalance: Double): Result<Unit>
    suspend fun insertWallet(walletEntity: WalletEntity): Result<Unit>
}