package com.example.monitorwidget.ui.theme.domain.model.usecase

import com.example.monitorwidget.ui.theme.domain.model.DollarRates
import com.example.monitorwidget.ui.theme.domain.model.repository.DollarRepository

class GetDollarRatesUseCase(
    private val repository: DollarRepository
) {
    suspend operator fun invoke(): DollarRates {
        return repository.getDollarRates()
    }
}