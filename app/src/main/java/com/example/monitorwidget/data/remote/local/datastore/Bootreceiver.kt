package com.example.monitorwidget.data.remote.local.datastore

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.monitorwidget.data.remote.DollarCheckWorker
import com.example.monitorwidget.data.remote.DollarCheckWorker_Factory
import com.example.monitorwidget.presentation.monitor_widget.MonitorWorker



class BootReceiver : BroadcastReceiver() {
	
	override fun onReceive(context: Context, intent: Intent?) {
		val action = intent?.action ?: return
		if (action != Intent.ACTION_BOOT_COMPLETED &&
			action != Intent.ACTION_MY_PACKAGE_REPLACED
		) return
		
		Log.d(TAG, "Boot/update detectado — re-programando workers...")
		
		MonitorWorker.enqueue(context)
		
		DollarCheckWorker.enqueue(context)
		
		WorkManager.getInstance(context)
			.enqueue(OneTimeWorkRequestBuilder<MonitorWorker>().build())
		
		Log.d(TAG, "Workers re-programados tras arranque")
	}
	
	companion object {
		private const val TAG = "BootReceiver"
	}
}