package com.example.testwalletbinance.data.remote

import com.example.testwalletbinance.data.remote.api.BitcoinApi
import javax.inject.Inject

class BitcoinRemoteDataSource @Inject constructor(
    private val bitcoinApi: BitcoinApi
) {
    suspend fun fetchBitcoinRate(): Double? {
        val response = bitcoinApi.getBitcoinRate()
        return if (response.isSuccessful) {
            response.body()?.data?.priceUsd
        } else null
    }
}
