package com.example.monitorwidget.data.remote.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.monitorwidget.domain.model.DollarRates
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "dollar_rates")

class DollarDataStore(private val context: Context) {

    companion object {
        private val BCV = doublePreferencesKey("bcv")
        private val TIMESTAMP = longPreferencesKey("timestamp")
    }

    suspend fun saveRates(rates: DollarRates) {
        context.dataStore.edit {
            it[BCV] = rates.bcv
            it[TIMESTAMP] = rates.timestamp
        }
    }

    suspend fun getRates(): DollarRates? {
        val prefs = context.dataStore.data.first()
        val bcv = prefs[BCV]
        val timestamp = prefs[TIMESTAMP]

        return if (bcv != null && timestamp != null) {
            DollarRates(bcv, timestamp)
        } else {
            null
        }
    }
}












