package com.example.monitorwidget.domain.model

data class DollarRates(
    val bcv: Double,
    val eur: Double = 0.0,
    val timestamp: Long
)