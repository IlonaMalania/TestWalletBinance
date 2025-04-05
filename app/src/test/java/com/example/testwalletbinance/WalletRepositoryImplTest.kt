package com.example.testwalletbinance

import com.example.testwalletbinance.data.dao.WalletDao
import com.example.testwalletbinance.data.model.WalletEntity
import com.example.testwalletbinance.data.repository.WalletRepositoryImpl
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WalletRepositoryImplTest {

    private lateinit var repository: WalletRepositoryImpl
    private val walletDao: WalletDao = mockk()

    private val testWallet = WalletEntity(id = 1, balance = 100.0)

    @Before
    fun setup() {

        every { walletDao.getWalletFlow() } returns flowOf(null)
        repository = WalletRepositoryImpl(walletDao)
    }

    @Test
    fun `getWallet returns success when wallet exists`() = runTest {
        // Given
        coEvery { walletDao.getWallet() } returns testWallet

        // When
        val result = repository.getWallet()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(testWallet, result.getOrNull())
    }

    @Test
    fun `getWallet returns failure when wallet is null`() = runTest {
        // Given
        coEvery { walletDao.getWallet() } returns null

        // When
        val result = repository.getWallet()

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `insertWallet calls DAO and returns success`() = runTest {
        // Given
        coEvery { walletDao.insertWallet(testWallet) } just Runs

        // When
        val result = repository.insertWallet(testWallet)

        // Then
        assertTrue(result.isSuccess)
        coVerify { walletDao.insertWallet(testWallet) }
    }

    @Test
    fun `updateBalance calls DAO and returns success`() = runTest {
        // Given
        val newBalance = 500.0
        coEvery { walletDao.updateBalance(newBalance) } just Runs

        // When
        val result = repository.updateBalance(newBalance)

        // Then
        assertTrue(result.isSuccess)
        coVerify { walletDao.updateBalance(newBalance) }
    }
}
