package com.example.monitorwidget.presentation.monitor_widget


import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.example.monitorwidget.data.remote.RetrofitClient
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.ui.theme.domain.model.repository.DollarRepositoryImpl
import com.example.monitorwidget.ui.theme.domain.model.usecase.GetDollarRatesUseCase
import java.util.concurrent.TimeUnit


class MonitorWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            Log.d("MonitorWorker", "Iniciando actualización del widget...")

            val repository = DollarRepositoryImpl(
                RetrofitClient.api,
                DollarDataStore(applicationContext)
            )

            val useCase = GetDollarRatesUseCase(repository)
            val rates = useCase() // llama al invoke

            Log.d("MonitorWorker", "Datos recibidos: usdt=${rates.usdt}, bcv=${rates.bcv}, promedio=${rates.promedio}, timestamp=${rates.timestamp}")

            val glanceIds = GlanceAppWidgetManager(applicationContext)
                .getGlanceIds(MonitorGlanceWidget::class.java)

            Log.d("MonitorWorker", "Número de widgets encontrados: ${glanceIds.size}")

            glanceIds.forEach {
                MonitorGlanceWidget().update(applicationContext, it)
                Log.d("MonitorWorker", "Widget actualizado")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("MonitorWorker", "Error al actualizar widget: ${e.localizedMessage}", e)
            Result.failure()
        }
    }

    companion object {
        fun enqueue(context: Context) {
            val periodicRequest = PeriodicWorkRequestBuilder<MonitorWorker>(1, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "monitor_widget_worker",
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicRequest
                )
        }

        fun enqueueOnce(context: Context) {
            val oneTimeRequest = OneTimeWorkRequestBuilder<MonitorWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueue(oneTimeRequest)
        }
    }
}