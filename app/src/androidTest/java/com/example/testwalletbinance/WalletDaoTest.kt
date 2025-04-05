package com.example.testwalletbinance

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.testwalletbinance.data.dao.WalletDao
import com.example.testwalletbinance.data.db.AppDatabase
import com.example.testwalletbinance.data.model.WalletEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WalletDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var walletDao: WalletDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        walletDao = database.walletDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertWallet_andGetWallet_returnsSameWallet() = runTest {
        val wallet = WalletEntity(id = 1, balance = 100.0)

        walletDao.insertWallet(wallet)
        val result = walletDao.getWallet()

        assertEquals(wallet, result)
    }

    @Test
    fun updateBalance_updatesCorrectly() = runTest {
        val wallet = WalletEntity(id = 1, balance = 100.0)
        walletDao.insertWallet(wallet)

        walletDao.updateBalance(250.0)
        val updatedWallet = walletDao.getWallet()

        assertEquals(250.0, updatedWallet?.balance)
    }

    @Test
    fun getWalletFlow_emitsData() = runTest {
        val wallet = WalletEntity(id = 1, balance = 300.0)
        walletDao.insertWallet(wallet)

        val result = walletDao.getWalletFlow().first()

        assertEquals(wallet, result)
    }
}
