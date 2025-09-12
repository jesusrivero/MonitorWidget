package com.example.monitorwidget.data.remote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.monitorwidget.R
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.domain.repository.DollarRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DollarCheckWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParams: WorkerParameters,
	private val repository: DollarRepository,
	private val dataStore: DollarDataStore
) : CoroutineWorker(context, workerParams) {
	
	private val fgChannelId = "rate_watch_fg"
	private val fgNotifId = 2001
	
	override suspend fun doWork(): Result {
		// Solo entrar a foreground si este run viene marcado como expedited
		val isExpedited = inputData.getBoolean("expedited", false)
		if (isExpedited) {
			setForeground(getForegroundInfo())
		}
		
		Log.d("DollarWorker", "🔄 Ejecutando Worker...")
		
		return try {
			// 1) Leer el último valor ANTES de pedir el nuevo
			val lastRates = dataStore.getRates()
			
			// 2) Obtener el nuevo valor (tu repo ya guarda en DataStore al traer)
			val newRates = repository.getDollarRates()
			
			Log.d(
				"DollarWorker",
				"📊 Último=${lastRates?.bcv} → Nuevo=${newRates.bcv}"
			)
			
			// 3) Comparar contra el valor leido ANTES de fetch
			val changed = lastRates == null ||
					kotlin.math.abs(newRates.bcv - lastRates.bcv) >= 0.01  // umbral 0.01 Bs
			
			if (changed) {
				showChangeNotification(newRates.bcv)
			} else {
				Log.d("DollarWorker", "✅ Sin cambios relevantes, no se notifica.")
			}
			
			Result.success()
		} catch (t: Throwable) {
			Log.e("DollarWorker", "❌ Error en Worker", t)
			Result.retry()
		}
	}
	
	// Se usará solo cuando encoles expedited=true (aquí no lo haremos)
	override suspend fun getForegroundInfo(): ForegroundInfo {
		ensureChannel(fgChannelId, "Monitoreo del dólar")
		val notif = NotificationCompat.Builder(applicationContext, fgChannelId)
			.setSmallIcon(R.drawable.ic_logo_splash) // tu ícono
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
		
		val notification = NotificationCompat.Builder(applicationContext, channelId)
			.setContentTitle("💵 Precio del dólar BCV")
			.setContentText("El BCV ahora está en: $price Bs")
			.setSmallIcon(R.drawable.ic_logo_splash)
			.setAutoCancel(true)
			.build()
		
		val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		nm.notify(1001, notification)
	}
	
	private fun ensureChannel(id: String, name: String) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			val ch = NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW)
			nm.createNotificationChannel(ch)
		}
	}
}
