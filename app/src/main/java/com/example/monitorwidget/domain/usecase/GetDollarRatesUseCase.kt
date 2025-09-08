package com.example.monitorwidget.domain.usecase

import com.example.monitorwidget.domain.model.DollarRates
import com.example.monitorwidget.domain.repository.DollarRepository
import javax.inject.Inject


class GetDollarRatesUseCase  @Inject constructor(
    private val repository: DollarRepository
) {
    suspend operator fun invoke(): DollarRates {
        return repository.getDollarRates()
    }
}