package com.example.monitorwidget.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.monitorwidget.domain.model.entity.FavoriteAmountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteAmountDao {
	@Query("SELECT * FROM favorite_amounts ORDER BY id DESC")
	fun getAllFavorites(): Flow<List<FavoriteAmountEntity>>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertFavorite(favorite: FavoriteAmountEntity)
	
	@Delete
	suspend fun deleteFavorite(favorite: FavoriteAmountEntity)
	
	@Update
	suspend fun updateFavorite(favorite: FavoriteAmountEntity) // ✅
	
}