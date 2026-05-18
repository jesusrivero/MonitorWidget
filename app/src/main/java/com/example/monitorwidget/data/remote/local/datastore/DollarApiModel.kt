package com.example.monitorwidget.data.remote.local.datastore

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DolarApiItem(
	val moneda: String? = null,
	val fuente: String,
	val nombre: String,
	val compra: Double?,
	val venta: Double?,
	val promedio: Double,
	val fechaActualizacion: String
)

data class DollarRates(
	val bcv: Double,
	val timestamp: Long
)


@JsonClass(generateAdapter = true)
data class HexaRateResponse(
	val status_code: Int,
	val data: HexaRateData
)

@JsonClass(generateAdapter = true)
data class HexaRateData(
	val base: String,
	val target: String,
	val mid: Double,
	val unit: Int,
	val timestamp: String
)

