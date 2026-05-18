package com.example.monitorwidget.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.monitorwidget.domain.model.entity.FavoriteAmountEntity

@Database(
	entities = [FavoriteAmountEntity::class],
	version = 2,
	exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun favoriteAmountDao(): FavoriteAmountDao
}