package com.example.monitorwidget.presentation.monitor_widget


import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.example.monitorwidget.presentation.ui.MonitorGlanceWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlin.jvm.java


class MonitorWidgetProvider : AppWidgetProvider() {
	
	override fun onUpdate(
		context: Context,
		appWidgetManager: AppWidgetManager,
		appWidgetIds: IntArray
	) {
		super.onUpdate(context, appWidgetManager, appWidgetIds)
		
		// 🔧 Se delega TODO al Worker — sin CoroutineScope suelto que genere memory leak.
		// El Worker ya se encarga de actualizar el widget (GlanceIds) y guardar en DataStore.
		MonitorWorker.enqueueOnce(context)
		
		Log.d("MonitorWidgetProvider", "onUpdate → encolado MonitorWorker (one-time)")
	}
	
	override fun onEnabled(context: Context) {
		super.onEnabled(context)
		
		// 🔧 Solo encola el periódico UNA vez cuando se añade el primer widget.
		// onUpdate ya maneja las actualizaciones puntuales.
		MonitorWorker.enqueue(context)
		
		Log.d("MonitorWidgetProvider", "onEnabled → encolado MonitorWorker (periódico)")
	}
	
	override fun onDisabled(context: Context) {
		super.onDisabled(context)
		
		// 🔧 Cancela el worker periódico cuando se eliminan todos los widgets
		// para no consumir batería/red innecesariamente.
		MonitorWorker.cancel(context)
		
		Log.d("MonitorWidgetProvider", "onDisabled → MonitorWorker cancelado")
	}
}