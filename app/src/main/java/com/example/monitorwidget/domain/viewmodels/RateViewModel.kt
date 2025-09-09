package com.example.monitorwidget.ui.theme.domain.model.viewmodel


import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitorwidget.domain.enums.FavoriteAmount
import com.example.monitorwidget.domain.model.DollarRates
import com.example.monitorwidget.domain.usecase.GetDollarRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DollarViewModel @Inject constructor(
	private val getDollarRatesUseCase: GetDollarRatesUseCase
) : ViewModel() {
	
	private val _rates = MutableStateFlow<DollarRates?>(null)
	val rates: StateFlow<DollarRates?> = _rates
	
	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading
	
	private val _error = MutableStateFlow<String?>(null)
	val error: StateFlow<String?> = _error
	
	
	// Lista de favoritos (en memoria por ahora)
	private val _favorites = mutableStateListOf<FavoriteAmount>()
	val favorites: List<FavoriteAmount> get() = _favorites
	
	
	init {
		fetchRates()
	}
	
	fun fetchRates() {
		viewModelScope.launch {
			_isLoading.value = true
			_error.value = null
			try {
				val result = getDollarRatesUseCase()
				_rates.value = result
			} catch (e: Exception) {
				_error.value = e.message ?: "Error desconocido"
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