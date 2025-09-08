package com.example.monitorwidget.infraestructure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.domain.model.DollarRates
import com.example.monitorwidget.domain.viewmodels.ThemeViewModel
import com.example.monitorwidget.presentation.monitor_widget.MonitorWorker
import com.example.monitorwidget.presentation.navegacion.NavigationHost
import com.example.monitorwidget.presentation.theme.MonitorWidgetTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		MonitorWorker.Companion.enqueue(applicationContext)
		MonitorWorker.Companion.enqueueOnce(applicationContext)
		
		enableEdgeToEdge()
		setContent {
			val themeViewModel: ThemeViewModel = hiltViewModel()
			val isDarkMode by themeViewModel.isDarkMode.collectAsState()
			val context = LocalContext.current
			var rates by remember { mutableStateOf<DollarRates?>(null) }
			LaunchedEffect(Unit) {
				val dataStore = DollarDataStore(context)
				rates = dataStore.getRates()
			}
			MonitorWidgetTheme (darkTheme = isDarkMode) {
				NavigationHost()
			}

		}
	}
}