package com.example.testwalletbinance
import android.util.Log
import com.example.testwalletbinance.data.model.WalletEntity
import com.example.testwalletbinance.data.repository.BitcoinRepository
import com.example.testwalletbinance.data.repository.TransactionRepository
import com.example.testwalletbinance.data.repository.WalletRepository
import com.example.testwalletbinance.presentation.screen_add_transaction.TransactionCategory
import com.example.testwalletbinance.presentation.screen_wallet.viewmodel.WalletViewModel
import com.example.testwalletbinance.ui.RateUiState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WalletViewModelTest {

    private lateinit var viewModel: WalletViewModel
    private lateinit var bitcoinRepository: BitcoinRepository
    private lateinit var walletRepository: WalletRepository
    private lateinit var transactionRepository: TransactionRepository

    private val testDispatcher = StandardTestDispatcher()


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        bitcoinRepository = mockk()
        walletRepository = mockk()
        transactionRepository = mockk()


        coEvery { walletRepository.getWallet() } returns Result.success(WalletEntity(balance = 100.0))
        coEvery { walletRepository.walletFlow } returns MutableStateFlow(WalletEntity(balance = 100.0))
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0

        viewModel = WalletViewModel(walletRepository, bitcoinRepository, transactionRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadRate_success_setsSuccessState() = runTest {
        // Given
        val rate = 50000.0
        coEvery { bitcoinRepository.getBitcoinRate() } returns rate

        // When
        viewModel.loadRate()
        advanceUntilIdle()

        // Then
        val state = viewModel.rateState.value
        assertTrue(state is RateUiState.Success)
        assertEquals(rate, (state as RateUiState.Success).rate, 1.0)
    }

    @Test
    fun loadRate_failure_setsErrorState() = runTest {
        // Given
        coEvery { bitcoinRepository.getBitcoinRate() } throws RuntimeException("API error")

        // When
        viewModel.loadRate()
        advanceUntilIdle()

        // Then
        val state = viewModel.rateState.value
        assertTrue(state is RateUiState.Error)
        assertEquals("API error", (state as RateUiState.Error).message)
    }

    @Test
    fun topUpBalance_success_updatesWalletAndAddsTransaction() = runTest {
        // Given
        val initialBalance = 100.0
        val topUpAmount = 50.0
        val updatedBalance = initialBalance + topUpAmount

        val wallet = WalletEntity(balance = initialBalance)

        coEvery { walletRepository.getWallet() } returns Result.success(wallet)
        coEvery { walletRepository.updateBalance(updatedBalance) } returns Result.success(Unit)
        coEvery { transactionRepository.addTransaction(any()) } returns Result.success(Unit)

        // When
        viewModel.topUpBalance(topUpAmount)
        advanceUntilIdle()

        // Then
        coVerify { walletRepository.updateBalance(updatedBalance) }
        coVerify {
            transactionRepository.addTransaction(withArg {
                assertEquals(topUpAmount, it.amount, 0.01)
                assertEquals(TransactionCategory.TOP_UP.displayName, it.category)
            })
        }
    }

    @Test
    fun topUpBalance_walletFetchFails_logsError() = runTest {
        // Given
        coEvery { walletRepository.getWallet() } returns Result.failure(Exception("DB error"))

        // When
        viewModel.topUpBalance(50.0)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { walletRepository.updateBalance(any()) }
        coVerify(exactly = 0) { transactionRepository.addTransaction(any()) }
    }

    @Test
    fun walletFlow_emitsWalletEntity() = runTest {
        // Given
        val wallet = WalletEntity(balance = 123.0)
        val flow = MutableStateFlow<WalletEntity?>(wallet)

        every { walletRepository.walletFlow } returns flow
        coEvery { walletRepository.getWallet() } returns Result.success(wallet)

        viewModel = WalletViewModel(walletRepository, bitcoinRepository, transactionRepository)

        // When
        val result = viewModel.walletFlow.first { it != null }

        // Then
        assertEquals(wallet, result)
    }

    @Test
    fun init_walletDoesNotExist_insertsWallet() = runTest {
        // Given
        coEvery { walletRepository.getWallet() } returns Result.failure(Exception("No wallet"))
        coEvery { walletRepository.insertWallet(any()) } returns Result.success(Unit)
        every { walletRepository.walletFlow } returns MutableStateFlow(null)

        // When
        viewModel = WalletViewModel(walletRepository, bitcoinRepository, transactionRepository)
        advanceUntilIdle()

        // Then
        coVerify {
            walletRepository.insertWallet(
                match { it.balance == 0.0 }
            )
        }
    }

}
