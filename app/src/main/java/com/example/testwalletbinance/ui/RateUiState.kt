package com.example.testwalletbinance.ui

sealed class RateUiState {
    object Loading : RateUiState()
    data class Success(val rate: Double) : RateUiState()
    data class Error(val message: String) : RateUiState()
}