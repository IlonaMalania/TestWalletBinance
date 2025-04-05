package com.example.testwalletbinance.data.remote.api

import com.example.testwalletbinance.data.model.BitcoinResponse
import retrofit2.Response
import retrofit2.http.GET

interface BitcoinApi {
    @GET("v2/assets/bitcoin")
    suspend fun getBitcoinRate(): Response<BitcoinResponse>
}
