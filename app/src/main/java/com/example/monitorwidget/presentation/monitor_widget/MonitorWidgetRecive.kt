package com.example.monitorwidget.presentation.monitor_widget


import androidx.glance.appwidget.GlanceAppWidgetReceiver

class MonitorWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: MonitorGlanceWidget
        get() = MonitorGlanceWidget()
}