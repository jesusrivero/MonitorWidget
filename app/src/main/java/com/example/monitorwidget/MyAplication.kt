package com.example.monitorwidget

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {
	
	@Inject
	lateinit var workerFactory: HiltWorkerFactory
	
	@Inject
	lateinit var monitorScheduler: MonitorScheduler
	
	override val workManagerConfiguration: Configuration
		get() = Configuration.Builder()
			.setWorkerFactory(workerFactory)
			.setMinimumLoggingLevel(Log.DEBUG)
			.build()
	
	override fun onCreate() {
		super.onCreate()
		
		monitorScheduler.scheduleAll()
	}
}