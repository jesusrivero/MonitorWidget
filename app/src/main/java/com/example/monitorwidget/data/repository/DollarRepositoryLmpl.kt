package com.example.monitorwidget.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.monitorwidget.data.remote.DollarApiService
import com.example.monitorwidget.data.remote.HexaRateApiService
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.domain.model.DollarRates
import com.example.monitorwidget.domain.repository.DollarRepository
import java.time.Instant

class DollarRepositoryImpl(
	private val api: DollarApiService,
	private val hexaApi: HexaRateApiService,
	private val dataStore: DollarDataStore
) : DollarRepository {
	
	@RequiresApi(Build.VERSION_CODES.O)
	override suspend fun getDollarRates(): DollarRates {
		return try {
			// ✅ Intento principal con DolarAPI
			val ratesList = api.getDolarRates()
			val bcv = ratesList.find { it.fuente.lowercase() == "oficial" }?.promedio
			
			if (bcv == null) throw Exception("BCV no encontrado en DolarAPI")
			
			val rates = DollarRates(
				bcv = bcv,
				timestamp = Instant.now().epochSecond
			)
			
			dataStore.saveRates(rates)
			rates
			
		} catch (e: Exception) {
			// ✅ Si falla, intento con HexaRate
			try {
				val fallback = hexaApi.getUsdToVes()
				if (fallback.status_code != 200) throw Exception("HexaRate inválido")
				
				val rates = DollarRates(
					bcv = fallback.data.mid,
					timestamp = Instant.now().epochSecond
				)
				
				dataStore.saveRates(rates)
				rates
			} catch (fallbackEx: Exception) {
				// ✅ Si ambas fallan, devuelvo caché o propago excepción
				dataStore.getRates() ?: throw fallbackEx
			}
		}
	}
}
