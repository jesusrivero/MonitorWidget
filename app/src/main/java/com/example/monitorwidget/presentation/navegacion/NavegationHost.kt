package com.example.monitorwidget.presentation.navegacion

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.monitorwidget.presentation.ui.DollarCalculatorScreen
import com.example.monitorwidget.presentation.ui.FavoriteScreen
import com.example.monitorwidget.presentation.splash.SplashScreen

@Composable
fun NavigationHost(
	navController: NavHostController = rememberNavController(),
	intent: Intent? = null
) {

	val startDestination = when (intent?.getStringExtra("navigateTo")) {
		"favorites" -> AppRoutes.FavoriteScreen
		else -> AppRoutes.SplashScreen
	}
	
	NavHost(
		navController = navController,
		startDestination = startDestination
	) {
		composable<AppRoutes.DollarCalculatorScreen> {
			DollarCalculatorScreen(navController = navController)
		}
		
		composable<AppRoutes.FavoriteScreen> {
			FavoriteScreen(navController = navController)
		}
		
		composable<AppRoutes.SplashScreen> {
			SplashScreen(navController = navController)
		}
	}
}
