package com.example.monitorwidget.ui.theme.domain.model.viewmodel


import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.domain.model.enums.FavoriteAmount
import com.example.monitorwidget.domain.model.DollarRates
import com.example.monitorwidget.domain.usecase.GetDollarRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DollarViewModel @Inject constructor(
	private val getDollarRatesUseCase: GetDollarRatesUseCase,
	private val dataStore: DollarDataStore          // ← inyectar dataStore
) : ViewModel() {
	
	private val _rates = MutableStateFlow<DollarRates?>(null)
	val rates: StateFlow<DollarRates?> = _rates
	
	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading
	
	private val _error = MutableStateFlow<String?>(null)
	val error: StateFlow<String?> = _error
	
	private val _favorites = mutableStateListOf<FavoriteAmount>()
	val favorites: List<FavoriteAmount> get() = _favorites
	
	init {
		// 1. Cargar caché inmediatamente — la UI se muestra sin esperar la red
		val cached = dataStore.getRates()
		if (cached != null) {
			_rates.value = cached
		}
		
		// 2. Intentar actualizar desde la red en segundo plano
		fetchRates(showLoadingIfNoCache = cached == null)
	}
	
	fun fetchRates(showLoadingIfNoCache: Boolean = true) {
		viewModelScope.launch {
			// Solo muestra el spinner si no hay datos en caché
			if (_rates.value == null || showLoadingIfNoCache) {
				_isLoading.value = true
			}
			_error.value = null
			
			try {
				val result = getDollarRatesUseCase()
				_rates.value = result
			} catch (e: Exception) {
				// Si ya hay datos en caché, no muestra el error — solo lo registra
				if (_rates.value == null) {
					_error.value = "Sin conexión. Verifica tu internet."
				}
				// Si hay caché, el usuario ve los datos viejos sin error molesto
				Log.w("DollarViewModel", "Error al actualizar tasas: ${e.message}")
			} finally {
				_isLoading.value = false
			}
		}
	}
	
	fun addFavorite(name: String, amountUsd: Double) {
		_favorites.add(FavoriteAmount(name, amountUsd))
	}
	
	fun removeFavorite(favorite: FavoriteAmount) {
		_favorites.remove(favorite)
	}
}