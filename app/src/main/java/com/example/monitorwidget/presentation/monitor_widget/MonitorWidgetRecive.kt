package com.example.monitorwidget.presentation.monitor_widget


import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.monitorwidget.presentation.ui.MonitorGlanceWidget

class MonitorWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: MonitorGlanceWidget
        get() = MonitorGlanceWidget()
}