package com.example.monitorwidget.data.remote.local.datastore

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.monitorwidget.presentation.monitor_widget.MonitorWorker


class NotificationAlarmReceiver : BroadcastReceiver() {
	
	override fun onReceive(context: Context, intent: Intent?) {
		Log.d(TAG, "AlarmManager disparado — encolando MonitorWorker one-time")
		WorkManager.getInstance(context)
			.enqueue(OneTimeWorkRequestBuilder<MonitorWorker>().build())
	}
	companion object {
		private const val TAG = "NotificationAlarmReceiver"
	}
}