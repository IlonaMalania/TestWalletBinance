package com.example.testwalletbinance.data.repository

import com.example.testwalletbinance.data.local.LocalDataSource
import com.example.testwalletbinance.data.remote.BitcoinRemoteDataSource
import javax.inject.Inject

class BitcoinRepositoryImpl @Inject constructor(
    private val remoteDataSource: BitcoinRemoteDataSource,
    private val localDataSource: LocalDataSource
) : BitcoinRepository{

    companion object {
        private const val ONE_HOUR_IN_MILLIS = 3600000L
    }

    override suspend fun getBitcoinRate(): Double {
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = localDataSource.getLastUpdateTime()

        return if (currentTime - lastUpdateTime > ONE_HOUR_IN_MILLIS) {
            val rate = remoteDataSource.fetchBitcoinRate()
            if (rate != null) {
                localDataSource.setBitcoinRate(rate)
                localDataSource.setLastUpdateTime(currentTime)
                rate
            } else {
                localDataSource.getBitcoinRate()
            }
        } else {
            localDataSource.getBitcoinRate()
        }
    }
}
