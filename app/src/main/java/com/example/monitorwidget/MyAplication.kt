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
import com.example.monitorwidget.data.remote.DollarCheckWorker
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
		
		// ✅ Usa esto SOLO si deshabilitaste el Initializer en el Manifest.
		// Si NO lo deshabilitaste, comenta/borra esta línea.
		WorkManager.initialize(this, workManagerConfiguration)
		
		val wm = WorkManager.getInstance(this)
		
		val constraints = Constraints.Builder()
			.setRequiredNetworkType(NetworkType.CONNECTED)
			.build()
		
		// ⏲️ Ejecutar cada 30 minutos (sin expedited)
		val periodic = PeriodicWorkRequestBuilder<DollarCheckWorker>(
			30, TimeUnit.MINUTES
		)
			.setConstraints(constraints)
			.build()
		
		wm.enqueueUniquePeriodicWork(
			"DollarCheck",
			ExistingPeriodicWorkPolicy.UPDATE,
			periodic
		)
	}
}
