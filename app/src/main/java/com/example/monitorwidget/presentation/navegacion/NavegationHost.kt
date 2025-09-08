package com.example.monitorwidget.presentation.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.monitorwidget.presentation.ui.DollarCalculatorScreen
import com.example.monitorwidget.presentation.ui.LiveRatesScreen
import com.example.monitorwidget.domain.model.DollarRates
import com.example.monitorwidget.presentation.splash.SplashScreen

@Composable
fun NavigationHost() {
	val navController = rememberNavController()
	val fakeRates = DollarRates(
		bcv = 36.5,
		timestamp = 0L
	)
	NavHost(
		navController = navController,
		startDestination = AppRoutes.SplashScreen
	) {
		composable<AppRoutes.DollarCalculatorScreen> {
			DollarCalculatorScreen(
				navController = navController,
			)
		}
		
		composable<AppRoutes.LiveRatesScreen> {
			LiveRatesScreen(
				navController = navController,
				rates = fakeRates
			)
		}
		
		composable<AppRoutes.SplashScreen> {
			SplashScreen(navController = navController)
		}
	}
}
