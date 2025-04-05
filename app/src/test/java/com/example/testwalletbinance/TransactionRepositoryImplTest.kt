package com.example.testwalletbinance

import androidx.paging.PagingSource
import com.example.testwalletbinance.data.dao.TransactionDao
import com.example.testwalletbinance.data.model.TransactionEntity
import com.example.testwalletbinance.data.repository.TransactionRepository
import com.example.testwalletbinance.data.repository.TransactionRepositoryImpl
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class TransactionRepositoryImplTest {

    private lateinit var transactionDao: TransactionDao
    private lateinit var repository: TransactionRepository

    @Before
    fun setup() {
        transactionDao = mockk(relaxed = true)
        repository = TransactionRepositoryImpl(transactionDao)
    }

    @Test
    fun `addTransaction should return success`() = runTest {
        // Given
        val transaction = TransactionEntity(1, 123.45, "BTC", System.currentTimeMillis())
        coEvery { transactionDao.insertTransaction(transaction) } just Runs

        // When
        val result = repository.addTransaction(transaction)

        // Then
        assertTrue(result.isSuccess)
        coVerify { transactionDao.insertTransaction(transaction) }
    }

    @Test
    fun `clearTransactions should return success`() = runTest {
        // Given
        coEvery { transactionDao.clearTransactions() } just Runs

        // When
        val result = repository.clearTransactions()

        // Then
        assertTrue(result.isSuccess)
        coVerify { transactionDao.clearTransactions() }
    }

    @Test
    fun `getTransactionsPaged should delegate to DAO`() {
        // Given
        val pagingSource = mockk<PagingSource<Int, TransactionEntity>>()
        every { transactionDao.getTransactionsPaged() } returns pagingSource

        // When
        val result = repository.getTransactionsPaged()

        // Then
        assertEquals(pagingSource, result)
        verify { transactionDao.getTransactionsPaged() }
    }
}
