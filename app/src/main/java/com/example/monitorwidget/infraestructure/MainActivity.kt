package com.example.monitorwidget.infraestructure

import android.os.Bundle
import android.util.Log
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
import androidx.navigation.compose.rememberNavController
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.domain.model.DollarRates
import com.example.monitorwidget.domain.viewmodels.ThemeViewModel
import com.example.monitorwidget.presentation.monitor_widget.MonitorWorker
import com.example.monitorwidget.presentation.navegacion.AppRoutes
import com.example.monitorwidget.presentation.navegacion.NavigationHost
import com.example.monitorwidget.presentation.theme.MonitorWidgetTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		FirebaseApp.initializeApp(this)
		FirebaseMessaging.getInstance().subscribeToTopic("dollar_updates")
			.addOnCompleteListener { task ->
				if (task.isSuccessful) {
					Log.d("FCM", "✅ Suscrito al topic dollar_updates")
				} else {
					Log.e("FCM", "❌ Error al suscribirse", task.exception)
				}
			}
		
		enableEdgeToEdge()
		setContent {
			val themeViewModel: ThemeViewModel = hiltViewModel()
			val isDarkMode by themeViewModel.isDarkMode.collectAsState()
			
			MonitorWidgetTheme(darkTheme = isDarkMode) {
				val navController = rememberNavController()
				NavigationHost(navController = navController, intent = intent)
			}
		}
	}
}
