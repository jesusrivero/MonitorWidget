package com.example.monitorwidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.monitorwidget.presentation.monitor_widget.AboutAppScreen
import com.example.monitorwidget.presentation.monitor_widget.MonitorWorker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MonitorWorker.enqueue(applicationContext)
        MonitorWorker.enqueueOnce(applicationContext)

        enableEdgeToEdge()
        setContent {
            AboutAppScreen()
        }
    }
}

