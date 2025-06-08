package com.example.monitorwidget.ui.theme.domain.model.repository

import com.example.monitorwidget.ui.theme.domain.model.DollarRates

interface DollarRepository {
    suspend fun getDollarRates(): DollarRates
}