package com.example.monitorwidget.data.remote.local.datastore

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DolarApiItem(
    val fuente: String,
    val nombre: String,
    val compra: Double?,
    val venta: Double?,
    val promedio: Double,
    val fechaActualizacion: String
)