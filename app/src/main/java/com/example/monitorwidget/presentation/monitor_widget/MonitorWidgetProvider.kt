package com.example.monitorwidget.presentation.monitor_widget


import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
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

        MonitorWorker.enqueue(context)

        CoroutineScope(Dispatchers.Default).launch {
            val glanceIds = GlanceAppWidgetManager(context)
                .getGlanceIds(MonitorGlanceWidget::class.java)

            glanceIds.forEach { glanceId ->
                MonitorGlanceWidget().update(context, glanceId)
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        MonitorWorker.enqueue(context)
    }
}