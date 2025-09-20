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
import com.example.monitorwidget.data.repository.DollarRepositoryImpl
import com.example.monitorwidget.domain.usecase.GetDollarRatesUseCase
import com.example.monitorwidget.presentation.ui.MonitorGlanceWidget
import java.util.concurrent.TimeUnit

class MonitorWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
	
	@RequiresApi(Build.VERSION_CODES.O)
	override suspend fun doWork(): Result {
		return try {
			Log.d("MonitorWorker", "Iniciando actualización del widget...")
			
			val repository = DollarRepositoryImpl(
				RetrofitClient.dollarApi,
				RetrofitClient.hexaApi,
				DollarDataStore(applicationContext)
			)
			
			val useCase = GetDollarRatesUseCase(repository)
			val apiRates = useCase()
			
			// ✅ Siempre actualizamos el timestamp con la hora local (última actualización real del widget)
			val updatedRates = apiRates.copy(
				timestamp = System.currentTimeMillis() / 1000
			)
			
			// ✅ Guardamos los datos en DataStore para que el widget lea el último timestamp
			DollarDataStore(applicationContext).saveRates(updatedRates)
			
			Log.d(
				"MonitorWorker",
				"Datos guardados: bcv=${updatedRates.bcv}, timestamp=${updatedRates.timestamp}"
			)
			
			val glanceIds = GlanceAppWidgetManager(applicationContext)
				.getGlanceIds(MonitorGlanceWidget::class.java)
			
			Log.d("MonitorWorker", "Número de widgets encontrados: ${glanceIds.size}")
			
			glanceIds.forEach {
				// ✅ El widget ahora siempre mostrará la última hora, aunque los valores no cambien
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
			val periodicRequest = PeriodicWorkRequestBuilder<MonitorWorker>(30, TimeUnit.MINUTES)
				.setConstraints(
					Constraints.Builder()
						.setRequiredNetworkType(NetworkType.CONNECTED)
						.build()
				)
				.build()
			
			WorkManager.getInstance(context)
				.enqueueUniquePeriodicWork(
					"monitor_widget_worker",
					ExistingPeriodicWorkPolicy.UPDATE,
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
