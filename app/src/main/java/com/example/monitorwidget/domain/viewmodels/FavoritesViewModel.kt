package com.example.monitorwidget.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitorwidget.domain.model.entity.FavoriteAmountEntity
import com.example.monitorwidget.domain.usecase.GetFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.monitorwidget.domain.usecase.AddFavoriteUseCase
import com.example.monitorwidget.domain.usecase.DeleteFavoriteUseCase


@HiltViewModel
class FavoritesViewModel @Inject constructor(
	private val getFavoritesUseCase: GetFavoritesUseCase,
	private val addFavoriteUseCase: AddFavoriteUseCase,
	private val deleteFavoriteUseCase: DeleteFavoriteUseCase
) : ViewModel() {
	
	val favorites = getFavoritesUseCase()
		.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
	
	fun addFavorite(name: String, amountUsd: Double) {
		viewModelScope.launch {
			addFavoriteUseCase(FavoriteAmountEntity(name = name, amountUsd = amountUsd))
		}
	}
	
	fun deleteFavorite(favorite: FavoriteAmountEntity) {
		viewModelScope.launch {
			deleteFavoriteUseCase(favorite)
		}
	}
}
