package com.example.testwalletbinance

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.testwalletbinance.data.dao.TransactionDao
import com.example.testwalletbinance.data.dao.WalletDao
import com.example.testwalletbinance.data.db.AppDatabase
import com.example.testwalletbinance.data.model.TransactionEntity
import com.example.testwalletbinance.data.model.WalletEntity
import com.example.testwalletbinance.presentation.screen_add_transaction.TransactionCategory
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var transactionDao: TransactionDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        transactionDao = database.transactionDao()
    }

    @After
    fun teardown() {
        database.close()
    }


    @Test
    fun insertAndFetchTransaction() = runTest {
        val transaction = TransactionEntity(
            id = 1,
            amount = 100.0,
            category = TransactionCategory.GROCERIES.displayName,
            timestamp = System.currentTimeMillis()
        )

        transactionDao.insertTransaction(transaction)

        // PagingSource requires special handling to get actual data
        val pagingSource = transactionDao.getTransactionsPaged()
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val result = (loadResult as PagingSource.LoadResult.Page).data
        assertEquals(1, result.size)
        assertEquals(transaction, result.first())
    }


    @Test
    fun clearTransactions_removesAll() = runTest {
        val transaction = TransactionEntity(
            id = 2,
            amount = 200.0,
            category = TransactionCategory.GROCERIES.displayName,
            timestamp = System.currentTimeMillis()
        )

        transactionDao.insertTransaction(transaction)
        transactionDao.clearTransactions()

        val pagingSource = transactionDao.getTransactionsPaged()
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        val result = (loadResult as PagingSource.LoadResult.Page).data
        assertTrue(result.isEmpty())
    }

}