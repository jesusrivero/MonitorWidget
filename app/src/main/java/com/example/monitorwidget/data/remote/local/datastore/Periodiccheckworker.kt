package com.example.monitorwidget.data.remote.local.datastore

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.monitorwidget.MonitorScheduler
import com.example.monitorwidget.domain.usecase.GetDollarRatesUseCase
import com.example.monitorwidget.presentation.ui.MonitorGlanceWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class PeriodicCheckWorker @AssistedInject constructor(
	@Assisted private val context: Context,
	@Assisted params: WorkerParameters,
	private val useCase: GetDollarRatesUseCase,
	private val dataStore: DollarDataStore,
	private val scheduler: MonitorScheduler
) : CoroutineWorker(context, params) {
	
	override suspend fun doWork(): Result {
		return try {
			Log.d(TAG, "PeriodicCheckWorker ejecutándose...")
			
			val rates = useCase()
			val updated = rates.copy(timestamp = System.currentTimeMillis() / 1000)
			dataStore.saveRates(updated)
			
			val glanceIds = GlanceAppWidgetManager(context)
				.getGlanceIds(MonitorGlanceWidget::class.java)
			
			glanceIds.forEach { MonitorGlanceWidget().update(context, it) }
			
			scheduler.scheduleAlarmManager()
			
			Log.d(TAG, "PeriodicCheckWorker completado — ${glanceIds.size} widgets actualizados")
			Result.success()
		} catch (e: Exception) {
			Log.e(TAG, "Error en PeriodicCheckWorker", e)
			Result.retry()
		}
	}
	
	companion object {
		private const val TAG = "PeriodicCheckWorker"
	}
}