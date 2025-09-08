package com.example.monitorwidget.presentation.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.monitorwidget.presentation.ui.DollarCalculatorScreen
import com.example.monitorwidget.presentation.ui.FavoriteScreen
import com.example.monitorwidget.presentation.splash.SplashScreen

@Composable
fun NavigationHost() {
	val navController = rememberNavController()

	NavHost(
		navController = navController,
		startDestination = AppRoutes.SplashScreen
	) {
		composable<AppRoutes.DollarCalculatorScreen> {
			DollarCalculatorScreen(
				navController = navController,
			)
		}
		
		composable<AppRoutes.FavoriteScreen> {
			FavoriteScreen(
				navController = navController
			)
		}
		
		composable<AppRoutes.SplashScreen> {
			SplashScreen(navController = navController)
		}
	}
}
