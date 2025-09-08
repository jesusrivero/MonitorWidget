package com.example.monitorwidget.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ✅ Forma correcta con delegate
val Context.dataStore by preferencesDataStore(name = "settings")

class ThemeDataStore(private val context: Context) {
	
	companion object {
		val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
	}
	
	val isDarkMode: Flow<Boolean> = context.dataStore.data
		.map { preferences ->
			preferences[DARK_MODE_KEY] ?: false
		}
	
	suspend fun setDarkMode(enabled: Boolean) {
		context.dataStore.edit { preferences ->
			preferences[DARK_MODE_KEY] = enabled
		}
	}
}
