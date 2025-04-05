package com.example.testwalletbinance

import com.example.testwalletbinance.data.model.WalletEntity
import com.example.testwalletbinance.data.repository.TransactionRepository
import com.example.testwalletbinance.data.repository.WalletRepository
import com.example.testwalletbinance.presentation.screen_add_transaction.TransactionCategory
import com.example.testwalletbinance.presentation.screen_add_transaction.viewmodel.TransactionViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    private lateinit var viewModel: TransactionViewModel
    private lateinit var walletRepository: WalletRepository
    private lateinit var transactionRepository: TransactionRepository

    private val testDispatcher = StandardTestDispatcher()


    @Before
    fun setup(){
        Dispatchers.setMain(testDispatcher)

        walletRepository = mockk()
        transactionRepository = mockk()

        viewModel = TransactionViewModel(transactionRepository, walletRepository)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addTransaction_success_updatesWalletAndAddsTransaction() = runTest {

        // Given
        val initialBalance = 100.0
        val amount = 50.0
        val updatedBalance = initialBalance - amount

        val category = TransactionCategory.GROCERIES.displayName

        val wallet = WalletEntity(balance = initialBalance)

        coEvery { walletRepository.getWallet() } returns Result.success(wallet)
        coEvery { walletRepository.updateBalance(updatedBalance) } returns Result.success(Unit)
        coEvery { transactionRepository.addTransaction(any()) } returns Result.success(Unit)

        // When
        viewModel.addTransaction(amount, category)
        advanceUntilIdle()

        // Then
        coVerify { walletRepository.updateBalance(updatedBalance) }
        coVerify {
            transactionRepository.addTransaction(withArg {
                assertEquals(amount, it.amount, 0.01)
                assertEquals(TransactionCategory.GROCERIES.displayName, it.category)
            })
        }

    }


}
