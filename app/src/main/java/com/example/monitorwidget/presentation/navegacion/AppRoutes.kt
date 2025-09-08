package com.example.monitorwidget.presentation.navegacion

import kotlinx.serialization.Serializable

object AppRoutes {
	
	@Serializable
	data object LiveRatesScreen
	
	@Serializable
	data object DollarCalculatorScreen
	
	@Serializable
	data object SplashScreen
	
}