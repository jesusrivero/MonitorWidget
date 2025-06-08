package com.example.monitorwidget.data.remote.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.monitorwidget.ui.theme.domain.model.DollarRates
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "dollar_rates")

class DollarDataStore(private val context: Context) {

    companion object {
        private val USDT = doublePreferencesKey("usdt")
        private val BCV = doublePreferencesKey("bcv")
        private val PROMEDIO = doublePreferencesKey("promedio")
        private val TIMESTAMP = longPreferencesKey("timestamp")
    }

    suspend fun saveRates(rates: DollarRates) {
        context.dataStore.edit {
            it[USDT] = rates.usdt
            it[BCV] = rates.bcv
            it[PROMEDIO] = rates.promedio
            it[TIMESTAMP] = rates.timestamp
        }
    }

    suspend fun getRates(): DollarRates? {
        val prefs = context.dataStore.data.first()
        val usdt = prefs[USDT]
        val bcv = prefs[BCV]
        val promedio = prefs[PROMEDIO]
        val timestamp = prefs[TIMESTAMP]

        return if (usdt != null && bcv != null && promedio != null && timestamp != null) {
            DollarRates(usdt, bcv, promedio, timestamp)
        } else {
            null
        }
    }
}












