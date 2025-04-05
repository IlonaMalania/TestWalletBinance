package com.example.testwalletbinance.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.testwalletbinance.data.dao.TransactionDao
import com.example.testwalletbinance.data.dao.WalletDao
import com.example.testwalletbinance.data.model.TransactionEntity
import com.example.testwalletbinance.data.model.WalletEntity

@Database(
    entities = [WalletEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun walletDao(): WalletDao
    abstract fun transactionDao(): TransactionDao

}
