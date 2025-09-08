package com.example.monitorwidget.data.repository

import com.example.monitorwidget.data.local.FavoriteAmountDao
import com.example.monitorwidget.domain.model.entity.FavoriteAmountEntity
import com.example.monitorwidget.domain.repository.FavoriteAmountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteAmountRepositoryImpl @Inject constructor(
	private val dao: FavoriteAmountDao
) : FavoriteAmountRepository {
	override fun getAllFavorites(): Flow<List<FavoriteAmountEntity>> = dao.getAllFavorites()
	
	override suspend fun insertFavorite(favorite: FavoriteAmountEntity) {
		dao.insertFavorite(favorite)
	}
	
	override suspend fun deleteFavorite(favorite: FavoriteAmountEntity) {
		dao.deleteFavorite(favorite)
	}
	
}