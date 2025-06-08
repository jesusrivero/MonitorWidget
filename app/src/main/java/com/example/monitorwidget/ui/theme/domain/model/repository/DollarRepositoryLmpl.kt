package com.example.monitorwidget.ui.theme.domain.model.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.monitorwidget.data.remote.DollarApiService
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.ui.theme.domain.model.DollarRates
import java.time.Instant

class DollarRepositoryImpl(
    private val api: DollarApiService,
    private val dataStore: DollarDataStore
) : DollarRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getDollarRates(): DollarRates {
        return try {
            val ratesList = api.getDolarRates()
            Log.e("MonitorWrket", "error ")

            val usdt = ratesList.find { it.fuente == "bitcoin" }?.promedio
            val bcv = ratesList.find { it.fuente == "oficial" }?.promedio
            val paralelo = ratesList.find { it.fuente == "paralelo" }?.promedio

            if (usdt == null || bcv == null || paralelo == null) {
                throw Exception("No se encontraron todas las tasas")
            }

            val rates = DollarRates(
                usdt = usdt,
                bcv = bcv,
                promedio = paralelo,
                timestamp = Instant.now().epochSecond
            )

            dataStore.saveRates(rates)
            rates
        } catch (e: Exception) {
            dataStore.getRates() ?: throw e
        }
    }
}
