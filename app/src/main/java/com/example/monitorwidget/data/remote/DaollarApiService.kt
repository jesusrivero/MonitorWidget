package com.example.monitorwidget.data.remote


import com.example.monitorwidget.data.remote.local.datastore.DolarApiItem
import com.example.monitorwidget.data.remote.local.datastore.HexaRateResponse
import retrofit2.http.GET
import retrofit2.http.Query

// Fuente principal
interface DollarApiService {
	@GET("v1/dolares")
	suspend fun getDolarRates(): List<DolarApiItem>
}

// Fuente de respaldo
interface HexaRateApiService {
	@GET("api/rates/latest/USD")
	suspend fun getUsdToVes(
		@Query("target") target: String = "VES"
	): HexaRateResponse
}