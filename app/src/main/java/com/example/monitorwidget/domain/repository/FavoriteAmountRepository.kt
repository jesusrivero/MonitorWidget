package com.example.monitorwidget.domain.repository

import com.example.monitorwidget.domain.model.entity.FavoriteAmountEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteAmountRepository {
	fun getAllFavorites(): Flow<List<FavoriteAmountEntity>>
	suspend fun insertFavorite(favorite: FavoriteAmountEntity)
	suspend fun deleteFavorite(favorite: FavoriteAmountEntity)
}