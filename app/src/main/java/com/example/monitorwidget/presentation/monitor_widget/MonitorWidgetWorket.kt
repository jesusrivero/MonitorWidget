package com.example.monitorwidget.presentation.monitor_widget


import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.example.monitorwidget.MonitorScheduler
import com.example.monitorwidget.data.remote.RetrofitClient
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.data.repository.DollarRepositoryImpl
import com.example.monitorwidget.domain.usecase.GetDollarRatesUseCase
import com.example.monitorwidget.presentation.ui.MonitorGlanceWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit


// 🔧 Ahora usa @HiltWorker para recibir dependencias inyectadas,
//    eliminando la instanciación manual de RetrofitClient y DataStore.
@HiltWorker
class MonitorWorker @AssistedInject constructor(
	@Assisted private val context: Context,
	@Assisted params: WorkerParameters,
	private val useCase: GetDollarRatesUseCase,
	private val dataStore: DollarDataStore,
	private val scheduler: MonitorScheduler  // 🔧 Para re-programar AlarmManager
) : CoroutineWorker(context, params) {
	
	override suspend fun doWork(): Result {
		return try {
			Log.d(TAG, "Iniciando actualización del widget...")
			
			val apiRates = useCase()
			val updatedRates = apiRates.copy(
				timestamp = System.currentTimeMillis() / 1000
			)
			
			dataStore.saveRates(updatedRates)
			Log.d(TAG, "Datos guardados: bcv=${updatedRates.bcv}, timestamp=${updatedRates.timestamp}")
			
			val glanceIds = GlanceAppWidgetManager(context)
				.getGlanceIds(MonitorGlanceWidget::class.java)
			
			Log.d(TAG, "Widgets encontrados: ${glanceIds.size}")
			glanceIds.forEach { glanceId ->
				MonitorGlanceWidget().update(context, glanceId)
				Log.d(TAG, "Widget actualizado: $glanceId")
			}
			
			// 🔧 CLAVE: re-programa la siguiente alarma para mantener la cadena activa.
			//    Si el AlarmManager fue cancelado (Doze, batería baja, reinicio),
			//    este Worker lo reactiva en cada ejecución exitosa.
			scheduler.scheduleAlarmManager()
			
			Result.success()
		} catch (e: Exception) {
			Log.e(TAG, "Error al actualizar widget: ${e.localizedMessage}", e)
			Result.retry()
		}
	}
	
	companion object {
		private const val TAG = "MonitorWorker"
		private const val WORK_NAME = "monitor_widget_worker"
		
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
					WORK_NAME,
					ExistingPeriodicWorkPolicy.KEEP, // 🔧 KEEP para no reiniciar el timer
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
		
		// 🔧 Nuevo: permite cancelar el worker cuando ya no hay widgets activos
		fun cancel(context: Context) {
			WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
		}
	}
}