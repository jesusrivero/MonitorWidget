package com.example.monitorwidget.data.remote.local.datastore

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.monitorwidget.domain.model.DollarRates
import kotlinx.coroutines.flow.first

class DollarDataStore(private val context: Context) {
	
	private val prefs: SharedPreferences by lazy {
		context.getSharedPreferences("dollar_rates", Context.MODE_PRIVATE)
	}
	
	companion object {
		private const val KEY_BCV       = "bcv"
		private const val KEY_EUR       = "eur"
		private const val KEY_TIMESTAMP = "timestamp"
	}
	
	// No necesita suspend — SharedPreferences es síncrono
	fun saveRates(rates: DollarRates) {
		prefs.edit()
			.putFloat(KEY_BCV, rates.bcv.toFloat())
			.putFloat(KEY_EUR, rates.eur.toFloat())
			.putLong(KEY_TIMESTAMP, rates.timestamp)
			.apply()  // async, no bloquea
	}
	
	fun getRates(): DollarRates? {
		val bcv       = prefs.getFloat(KEY_BCV, -1f)
		val eur       = prefs.getFloat(KEY_EUR, 0f)
		val timestamp = prefs.getLong(KEY_TIMESTAMP, -1L)
		
		return if (bcv >= 0f && timestamp >= 0L) {
			DollarRates(
				bcv       = bcv.toDouble(),
				eur       = eur.toDouble(),
				timestamp = timestamp
			)
		} else null
	}
	
	fun hasRates(): Boolean = prefs.contains(KEY_BCV)
}










