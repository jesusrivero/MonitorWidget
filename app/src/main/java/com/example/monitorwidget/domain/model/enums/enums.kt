package com.example.monitorwidget.domain.model.enums

import com.example.monitorwidget.domain.model.entity.FavoriteAmountEntity
import com.example.monitorwidget.presentation.navegacion.AppRoutes


enum class NavigationRoute(
	val title: String,
	val icon: String,
	val route: Any,
) {
	CALCULATOR("Calculadora", "💱", AppRoutes.DollarCalculatorScreen),
	LIVE_RATES("Gastos recurrentes", "💡", AppRoutes.FavoriteScreen)
}

data class FavoriteAmount(
	val name: String,
	val amountUsd: Double
)

data class FavoriteSuggestion(
	val name: String,
	val amountUsd: Double,
	val icon: String
)

data class FavoritesUiState(
	val favorites: List<FavoriteAmountEntity> = emptyList(),
	val isLoading: Boolean = false,
	val error: String? = null,
	val successMessage: String? = null,
	val message: String? = null
)