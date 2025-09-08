package com.example.monitorwidget.domain.repository

import com.example.monitorwidget.domain.model.DollarRates

interface DollarRepository {
    suspend fun getDollarRates(): DollarRates
}