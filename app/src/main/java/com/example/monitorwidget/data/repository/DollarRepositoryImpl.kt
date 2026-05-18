package com.example.monitorwidget.data.repository

import com.example.monitorwidget.data.remote.DollarApiService
import com.example.monitorwidget.data.remote.HexaRateApiService
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.domain.model.DollarRates
import com.example.monitorwidget.domain.repository.DollarRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
class DollarRepositoryImpl(
	private val api: DollarApiService,
	private val hexaApi: HexaRateApiService,
	private val dataStore: DollarDataStore
) : DollarRepository {
	
	override suspend fun getDollarRates(): DollarRates = coroutineScope {
		try {
			val dolaresDeferred = async { api.getDolarRates() }
			val eurosDeferred   = async { runCatching { api.getEuroRates() }.getOrNull() }
			
			val dolares = dolaresDeferred.await()
			val euros   = eurosDeferred.await()
			
			val bcv = dolares
				.find { it.fuente.lowercase() == "oficial" }?.promedio
				?: throw Exception("BCV no encontrado en DolarAPI")
			
			val eur = euros
				?.find { it.fuente.lowercase() == "oficial" }?.promedio
				?: 0.0
			
			val rates = DollarRates(
				bcv       = bcv,
				eur       = eur,
				timestamp = System.currentTimeMillis() / 1000L
			)
			
			dataStore.saveRates(rates)  // ya no es suspend
			rates
			
		} catch (e: Exception) {
			// Fallback 1: HexaRate
			try {
				val fallback = hexaApi.getUsdToVes()
				if (fallback.status_code != 200) throw Exception("HexaRate inválido")
				
				val rates = DollarRates(
					bcv       = fallback.data.mid,
					eur       = 0.0,
					timestamp = System.currentTimeMillis() / 1000L
				)
				dataStore.saveRates(rates)
				rates
				
			} catch (fallbackEx: Exception) {
				// Fallback 2: caché local — si no hay nada, propaga el error
				dataStore.getRates() ?: throw fallbackEx
			}
		}
	}
}
