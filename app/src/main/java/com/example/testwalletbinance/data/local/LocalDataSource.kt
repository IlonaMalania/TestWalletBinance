package com.example.testwalletbinance.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)


    fun getLastUpdateTime(): Long {
        return prefs.getLong(LAST_UPDATE_TIME_KEY, 0)
    }

    fun setLastUpdateTime(time: Long) {
        prefs.edit().putLong(LAST_UPDATE_TIME_KEY, time).apply()
    }

    fun getBitcoinRate(): Double {
        return prefs.getFloat(BITCOIN_RATE_KEY, 0f).toDouble()
    }

    fun setBitcoinRate(rate: Double) {
        prefs.edit().putFloat(BITCOIN_RATE_KEY, rate.toFloat()).apply()
    }

    companion object {
        private const val LAST_UPDATE_TIME_KEY = "last_update_time"
        private const val BITCOIN_RATE_KEY = "bitcoin_rate"
    }
}
