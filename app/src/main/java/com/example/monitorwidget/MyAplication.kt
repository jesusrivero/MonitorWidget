package com.example.monitorwidget

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.monitorwidget.data.remote.DollarCheckWorker
import com.example.monitorwidget.presentation.monitor_widget.MonitorWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject
@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {
	
	@Inject lateinit var workerFactory: HiltWorkerFactory
	
	override val workManagerConfiguration: Configuration
		get() = Configuration.Builder()
			.setWorkerFactory(workerFactory)
			.setMinimumLoggingLevel(Log.DEBUG)
			.build()
	
	override fun onCreate() {
		super.onCreate()
		
		// Worker que revisa cambios y manda notificaciones
		val constraints = Constraints.Builder()
			.setRequiredNetworkType(NetworkType.CONNECTED)
			.build()
		
		val periodicDollar = PeriodicWorkRequestBuilder<DollarCheckWorker>(
			30, TimeUnit.MINUTES
		)
			.setConstraints(constraints)
			.build()
		
		WorkManager.getInstance(this).enqueueUniquePeriodicWork(
			"DollarCheck",
			ExistingPeriodicWorkPolicy.UPDATE,
			periodicDollar
		)
		
		// Worker que actualiza el widget cada 30min
		MonitorWorker.enqueue(this)  // 👈 aquí lo programas
	}
}
