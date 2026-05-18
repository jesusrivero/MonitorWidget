package com.example.monitorwidget.data.remote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.monitorwidget.R
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.domain.repository.DollarRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit


@HiltWorker
class DollarCheckWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParams: WorkerParameters,
	private val repository: DollarRepository,
	private val dataStore: DollarDataStore,
) : CoroutineWorker(context, workerParams) {
	val lastRates = dataStore.getRates()
	private val fgChannelId = "rate_watch_fg"
	private val fgNotifId = 2001
	
	override suspend fun doWork(): Result {
		Log.d(TAG, "Ejecutando DollarCheckWorker...")
		
		return try {
			
			val lastRates = dataStore.getRates()
			val newRates = repository.getDollarRates()
			
			Log.d(TAG, "Último=${lastRates?.bcv} → Nuevo=${newRates.bcv}")
			
			val lastBcv = lastRates?.bcv
				?.toBigDecimal()
				?.setScale(2, RoundingMode.HALF_UP)
				?: BigDecimal.ZERO
			
			val newBcv = newRates.bcv
				.toBigDecimal()
				.setScale(2, RoundingMode.HALF_UP)
			
			val threshold = BigDecimal("0.01")
			val changed = lastRates == null || (newBcv - lastBcv).abs() >= threshold
			
			if (changed) {
				Log.d(TAG, "Cambio detectado: $lastBcv → $newBcv")
				showChangeNotification(newRates.bcv)
			} else {
				Log.d(TAG, "Sin cambios relevantes, no se notifica.")
			}
			
			Result.success()
		} catch (t: Throwable) {
			Log.e(TAG, "Error en DollarCheckWorker", t)
			Result.retry()
		}
	}
	
	@RequiresApi(Build.VERSION_CODES.O)
	override suspend fun getForegroundInfo(): ForegroundInfo {
		ensureChannel(fgChannelId, "Monitoreo del dólar")
		val notif = NotificationCompat.Builder(applicationContext, fgChannelId)
			.setSmallIcon(R.drawable.ic_logo_splash)
			.setContentTitle("Monitoreando precio del dólar")
			.setContentText("Comprobando cambios de BCV/USDT/Promedio…")
			.setOngoing(true)
			.setPriority(NotificationCompat.PRIORITY_LOW)
			.build()
		return ForegroundInfo(fgNotifId, notif)
	}
	
	private fun showChangeNotification(price: Double) {
		val channelId = "dollar_channel"
		ensureChannel(channelId, "Actualizaciones del dólar")
		
		
		val formattedPrice = "%.2f".format(price)
		
		val notification = NotificationCompat.Builder(applicationContext, channelId)
			.setContentTitle("💵 Precio del dólar BCV")
			.setContentText("El BCV ahora está en: $formattedPrice Bs")
			.setSmallIcon(R.drawable.ic_logo_splash)
			.setAutoCancel(true)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.build()
		
		val nm =
			applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		nm.notify(NOTIF_ID_CHANGE, notification)
	}
	
	private fun ensureChannel(id: String, name: String) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val nm =
				applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			val ch = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
			nm.createNotificationChannel(ch)
		}
	}
	
	companion object {
		private const val TAG = "DollarCheckWorker"
		private const val NOTIF_ID_CHANGE = 1001
		
		fun enqueue(context: Context) {
			val request = PeriodicWorkRequestBuilder<DollarCheckWorker>(30, TimeUnit.MINUTES)
				.setConstraints(
					Constraints.Builder()
						.setRequiredNetworkType(NetworkType.CONNECTED)
						.setRequiresBatteryNotLow(false)
						.build()
				)
				.build()
			
			WorkManager.getInstance(context).enqueueUniquePeriodicWork(
				"DollarCheck",
				ExistingPeriodicWorkPolicy.KEEP,
				request
			)
		}
	}
}