package com.example.testwalletbinance.data.repository


interface BitcoinRepository {

    suspend fun getBitcoinRate(): Double
}
