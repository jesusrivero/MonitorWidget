package com.example.monitorwidget.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitorwidget.data.preferences.ThemeDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
	private val themeDataStore: ThemeDataStore
) : ViewModel() {
	
	val isDarkMode = themeDataStore.isDarkMode.stateIn(
		scope = viewModelScope,
		started = SharingStarted.Eagerly,
		initialValue = false
	)
	
	fun toggleTheme(enabled: Boolean) {
		viewModelScope.launch {
			themeDataStore.setDarkMode(enabled)
		}
	}
}