package com.example.monitorwidget.domain.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_amounts")
data class FavoriteAmountEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int = 0,
	val name: String,
	val amountUsd: Double
)
