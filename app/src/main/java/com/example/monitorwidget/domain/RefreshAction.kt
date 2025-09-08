package com.example.monitorwidget.domain

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.example.monitorwidget.presentation.monitor_widget.MonitorWorker

class RefreshAction : ActionCallback {
    override suspend fun onAction(
	    context: Context,
	    glanceId: GlanceId,
	    parameters: ActionParameters
    ) {
        MonitorWorker.Companion.enqueueOnce(context)
    }
}