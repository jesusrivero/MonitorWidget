package com.example.monitorwidget.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitorwidget.domain.model.entity.FavoriteAmountEntity
import com.example.monitorwidget.domain.model.enums.FavoritesUiState
import com.example.monitorwidget.domain.usecase.GetFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.monitorwidget.domain.usecase.AddFavoriteUseCase
import com.example.monitorwidget.domain.usecase.DeleteFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

@HiltViewModel
class FavoritesViewModel @Inject constructor(
	private val getFavoritesUseCase: GetFavoritesUseCase,
	private val addFavoriteUseCase: AddFavoriteUseCase,
	private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
	private val updateFavoriteUseCase: AddFavoriteUseCase
) : ViewModel() {
	
	private val _uiState = MutableStateFlow(FavoritesUiState(isLoading = true))
	val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()
	
	init {
		viewModelScope.launch {
			getFavoritesUseCase()
				.onStart { _uiState.update { it.copy(isLoading = true) } }
				.catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
				.collect { favorites ->
					_uiState.update {
						it.copy(favorites = favorites, isLoading = false, error = null)
					}
				}
		}
	}
	
	fun addFavorite(name: String, amountUsd: Double, currency: String = "USD") {  // ← agrega currency
		viewModelScope.launch {
			try {
				addFavoriteUseCase(
					FavoriteAmountEntity(
						name      = name,
						amountUsd = amountUsd,
						currency  = currency   // ← pasa currency
					)
				)
				_uiState.update { it.copy(message = "✅ Favorito agregado con éxito") }
			} catch (e: Exception) {
				_uiState.update { it.copy(error = e.message) }
			}
		}
	}
	
	fun deleteFavorite(favorite: FavoriteAmountEntity) {
		viewModelScope.launch {
			try {
				deleteFavoriteUseCase(favorite)
				_uiState.update { it.copy(message = "🗑️ Favorito eliminado") }
			} catch (e: Exception) {
				_uiState.update { it.copy(error = e.message) }
			}
		}
	}
	
	fun updateFavorite(favorite: FavoriteAmountEntity) {
		viewModelScope.launch {
			try {
				updateFavoriteUseCase(favorite)  // ya trae currency dentro del objeto
				_uiState.update { it.copy(message = "✏️ Favorito actualizado") }
			} catch (e: Exception) {
				_uiState.update { it.copy(error = e.message) }
			}
		}
	}
	
	fun clearMessage() {
		_uiState.update { it.copy(message = null, error = null) }
	}
}
