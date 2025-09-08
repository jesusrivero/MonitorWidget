package com.example.monitorwidget.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.monitorwidget.data.remote.DollarApiService
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.domain.model.DollarRates
import com.example.monitorwidget.domain.repository.DollarRepository
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
	        
            val bcv = ratesList.find { it.fuente == "oficial" }?.promedio

            if (bcv == null) {
                throw Exception("No se encontraron todas las tasas")
            }

            val rates = DollarRates(
                bcv = bcv,
                timestamp = Instant.now().epochSecond
            )

            dataStore.saveRates(rates)
            rates
        } catch (e: Exception) {
            dataStore.getRates() ?: throw e
        }
    }
}
