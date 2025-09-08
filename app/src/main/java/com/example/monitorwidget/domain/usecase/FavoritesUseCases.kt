package com.example.monitorwidget.domain.usecase

import com.example.monitorwidget.domain.model.entity.FavoriteAmountEntity
import com.example.monitorwidget.domain.repository.FavoriteAmountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
	private val repository: FavoriteAmountRepository
) {
	operator fun invoke(): Flow<List<FavoriteAmountEntity>> = repository.getAllFavorites()
}

class AddFavoriteUseCase @Inject constructor(
	private val repository: FavoriteAmountRepository
) {
	suspend operator fun invoke(favorite: FavoriteAmountEntity) {
		repository.insertFavorite(favorite)
	}
}

class DeleteFavoriteUseCase @Inject constructor(
	private val repository: FavoriteAmountRepository
) {
	suspend operator fun invoke(favorite: FavoriteAmountEntity) {
		repository.deleteFavorite(favorite)
	}
}