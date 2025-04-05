package com.example.testwalletbinance

import com.example.testwalletbinance.data.local.LocalDataSource
import com.example.testwalletbinance.data.remote.BitcoinRemoteDataSource
import com.example.testwalletbinance.data.repository.BitcoinRepositoryImpl
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class BitcoinRepositoryImplTest {

    private lateinit var repository: BitcoinRepositoryImpl
    private val remoteDataSource: BitcoinRemoteDataSource = mockk()
    private val localDataSource: LocalDataSource = mockk()

    @Before
    fun setup() {
        repository = BitcoinRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun `returns cached rate if last update was less than 1 hour ago`(): Unit = runTest {
        // Given
        val cachedRate = 12345.67
        val currentTime = System.currentTimeMillis()
        coEvery { localDataSource.getLastUpdateTime() } returns currentTime
        coEvery { localDataSource.getBitcoinRate() } returns cachedRate

        // When
        val result = repository.getBitcoinRate()

        // Then
        assertEquals(cachedRate, result)
        coVerify(exactly = 0) { remoteDataSource.fetchBitcoinRate() }
    }

    @Test
    fun `fetches from remote and updates cache if last update was more than 1 hour ago`(): Unit = runTest {
        // Given
        val outdatedTime = System.currentTimeMillis() - 2 * 3600000L
        val newRate = 54321.0
        coEvery { localDataSource.getLastUpdateTime() } returns outdatedTime
        coEvery { remoteDataSource.fetchBitcoinRate() } returns newRate
        coEvery { localDataSource.setBitcoinRate(newRate) } just Runs
        coEvery { localDataSource.setLastUpdateTime(any()) } just Runs

        // When
        val result = repository.getBitcoinRate()

        // Then
        assertEquals(newRate, result)
        coVerify { localDataSource.setBitcoinRate(newRate) }
        coVerify { localDataSource.setLastUpdateTime(any()) }
    }

    @Test
    fun `returns cached rate if remote fetch fails`(): Unit = runTest {
        // Given
        val outdatedTime = System.currentTimeMillis() - 2 * 3600000L
        val fallbackRate = 9999.99
        coEvery { localDataSource.getLastUpdateTime() } returns outdatedTime
        coEvery { remoteDataSource.fetchBitcoinRate() } returns null
        coEvery { localDataSource.getBitcoinRate() } returns fallbackRate

        // When
        val result = repository.getBitcoinRate()

        // Then
        assertEquals(fallbackRate, result)
    }
}
