package com.example.monitorwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.monitorwidget.data.remote.local.datastore.NotificationAlarmReceiver
import com.example.monitorwidget.data.remote.local.datastore.PeriodicCheckWorker
import com.example.monitorwidget.presentation.monitor_widget.MonitorWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.java



@Singleton
class MonitorScheduler @Inject constructor(
	@ApplicationContext private val context: Context
) {
	
	companion object {
		private const val TAG = "MonitorScheduler"
		private const val ALARM_REQUEST_CODE = 3001
		private const val PERIODIC_CHECK_NAME = "monitor_periodic_check"
		private const val INTERVAL_MINUTES = 30L
	}
	

	fun scheduleAll() {
		Log.d(TAG, "Activando todas las capas de notificación...")
		scheduleWorkManager()
		scheduleAlarmManager()
		schedulePeriodicCheck()
		Log.d(TAG, "Todas las capas activas")
	}
	
	fun cancelAll() {
		cancelWorkManager()
		cancelAlarmManager()
		Log.d(TAG, "Todas las notificaciones canceladas")
	}
	
	private fun scheduleWorkManager() {
		val constraints = Constraints.Builder()
			.setRequiredNetworkType(NetworkType.CONNECTED)
			.setRequiresBatteryNotLow(false)
			.setRequiresCharging(false)
			.build()
		
		val request = PeriodicWorkRequestBuilder<MonitorWorker>(
			INTERVAL_MINUTES, TimeUnit.MINUTES
		)
			.setConstraints(constraints)
			.setBackoffCriteria(
				BackoffPolicy.LINEAR,
				WorkRequest.MIN_BACKOFF_MILLIS,
				TimeUnit.MILLISECONDS
			)
			.build()
		
		WorkManager.getInstance(context).enqueueUniquePeriodicWork(
			"monitor_widget_worker",
			ExistingPeriodicWorkPolicy.KEEP,
			request
		)
		Log.d(TAG, "WorkManager periódico programado (${INTERVAL_MINUTES}min)")
	}
	
	private fun cancelWorkManager() {
		WorkManager.getInstance(context).cancelUniqueWork("monitor_widget_worker")
		WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_CHECK_NAME)
	}
	
	fun scheduleAlarmManager() {
		val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		val pendingIntent = buildAlarmPendingIntent()
		
		val triggerAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(INTERVAL_MINUTES)
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			// setExactAndAllowWhileIdle: se ejecuta incluso en Doze/batería baja
			alarmManager.setExactAndAllowWhileIdle(
				AlarmManager.RTC_WAKEUP,
				triggerAt,
				pendingIntent
			)
		} else {
			alarmManager.setExact(
				AlarmManager.RTC_WAKEUP,
				triggerAt,
				pendingIntent
			)
		}
		Log.d(TAG, "AlarmManager programado en ${INTERVAL_MINUTES}min")
	}
	
	private fun cancelAlarmManager() {
		val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		alarmManager.cancel(buildAlarmPendingIntent())
	}
	
	private fun buildAlarmPendingIntent(): PendingIntent {
		val intent = Intent(context, NotificationAlarmReceiver::class.java)
		return PendingIntent.getBroadcast(
			context,
			ALARM_REQUEST_CODE,
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
	}
	
	
	private fun schedulePeriodicCheck() {
		val request = PeriodicWorkRequestBuilder<PeriodicCheckWorker>(6, TimeUnit.HOURS)
			.setConstraints(
				Constraints.Builder()
					.setRequiredNetworkType(NetworkType.CONNECTED)
					.setRequiresBatteryNotLow(false)
					.build()
			)
			.setBackoffCriteria(
				BackoffPolicy.LINEAR,
				WorkRequest.MIN_BACKOFF_MILLIS,
				TimeUnit.MILLISECONDS
			)
			.build()
		
		WorkManager.getInstance(context).enqueueUniquePeriodicWork(
			PERIODIC_CHECK_NAME,
			ExistingPeriodicWorkPolicy.KEEP,
			request
		)
		Log.d(TAG, "PeriodicCheckWorker programado (6h)")
	}
}