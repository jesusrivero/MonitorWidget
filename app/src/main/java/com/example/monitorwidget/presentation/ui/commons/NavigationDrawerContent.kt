package com.example.monitorwidget.presentation.ui.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.monitorwidget.domain.model.enums.NavigationRoute
import com.example.monitorwidget.domain.viewmodels.ThemeViewModel
import com.example.monitorwidget.presentation.navegacion.AppRoutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun NavigationDrawerContent(
	selectedRoute: NavigationRoute,
	viewmodel: ThemeViewModel = hiltViewModel(),
	onRouteSelected: (NavigationRoute) -> Unit,
) {
	
	val isDarkMode by viewmodel.isDarkMode.collectAsState(initial = false)
	
	Column(
		modifier = Modifier
			.background(
				color = MaterialTheme.colorScheme.surface,
			)
			.fillMaxSize()
			.padding(16.dp)
	
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.background(
					MaterialTheme.colorScheme.primary,
					RoundedCornerShape(12.dp)
				)
				.padding(20.dp)
		) {
			Column {
				Box(modifier = Modifier.fillMaxWidth()) {
					Text(
						text = "Monitor USD",
						style = MaterialTheme.typography.headlineSmall,
						color = MaterialTheme.colorScheme.onPrimary,
						fontWeight = FontWeight.Bold
					)
					Text(
						text = "💰",
						style = MaterialTheme.typography.headlineMedium,
						modifier = Modifier.align(Alignment.TopEnd)
					)
				}
				Text(
					text = "Tu companion financiero",
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
				)
			}
		}
		
		Spacer(modifier = Modifier.height(24.dp))
		
		NavigationRoute.values().forEach { route ->
			NavigationDrawerItem(
				selected = selectedRoute == route,
				onClick = { onRouteSelected(route) },
				icon = {
					Text(
						text = route.icon,
						style = MaterialTheme.typography.titleMedium
					)
				},
				label = {
					Text(
						text = route.title,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = if (selectedRoute == route) FontWeight.Bold else FontWeight.Normal
					)
				},
				shape = RoundedCornerShape(12.dp),
				modifier = Modifier.padding(vertical = 4.dp)
			)
		}
		
		
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp, vertical = 4.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = "Modo oscuro",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Medium
			)
			Switch(
				checked = isDarkMode,
				onCheckedChange = { viewmodel.toggleTheme(it) },
				colors = SwitchDefaults.colors(
					checkedThumbColor = Color.Black,
					checkedTrackColor = Color.Gray,
					uncheckedThumbColor = Color.White,
					uncheckedTrackColor = Color.Gray
				)
			)
		}
		
		
		Spacer(modifier = Modifier.weight(1f))
		
		
		Card(
			modifier = Modifier.fillMaxWidth(),
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
			),
			shape = RoundedCornerShape(8.dp)
		) {
			
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center
			)
			{
				Text(
					text = "📱 Monitor Widget v1.0\n🇻🇪 Venezuela",
					style = MaterialTheme.typography.bodySmall,
					modifier = Modifier.padding(12.dp),
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					textAlign = TextAlign.Center
				)
			}
			
		}
	}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerScaffold(
	currentRoute: NavigationRoute,
	navController: NavController,
	topBar: (@Composable (drawerState: DrawerState, scope: CoroutineScope) -> Unit)? = null,
	floatingActionButton: @Composable (() -> Unit)? = null,
	snackbarHost: @Composable (() -> Unit)? = null,
	content: @Composable (PaddingValues) -> Unit,
) {
	val drawerState = rememberDrawerState(DrawerValue.Closed)
	val scope = rememberCoroutineScope()
	
	ModalNavigationDrawer(
		drawerState = drawerState,
		drawerContent = {
			ModalDrawerSheet(
				modifier = Modifier.width(280.dp)
			) {
				NavigationDrawerContent(
					selectedRoute = currentRoute,
					onRouteSelected = { route ->
						scope.launch { drawerState.close() }
						
						if (route != currentRoute) {
							when (route) {
								NavigationRoute.CALCULATOR -> {
									navController.navigate(AppRoutes.DollarCalculatorScreen) {
										popUpTo(navController.graph.startDestinationId) {
											inclusive = true
										}
										launchSingleTop = true
									}
								}
								
								NavigationRoute.LIVE_RATES -> {
									navController.navigate(AppRoutes.FavoriteScreen) {
										popUpTo(navController.graph.startDestinationId) {
											inclusive = true
										}
										launchSingleTop = true
									}
								}
							}
						}
					}
				)
			}
		}
	) {
		Scaffold(
			topBar = {
				if (topBar == null) {
					CenterAlignedTopAppBar(
						title = {
							Text(
								text = currentRoute.title,
								style = MaterialTheme.typography.titleLarge,
								fontWeight = FontWeight.Bold,
								color = MaterialTheme.colorScheme.onPrimary
							)
						},
						navigationIcon = {
							IconButton(onClick = { scope.launch { drawerState.open() } }) {
								Icon(
									imageVector = Icons.Default.Menu,
									contentDescription = "Abrir menú",
									tint = MaterialTheme.colorScheme.onPrimary
								)
							}
						},
						colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
							containerColor = MaterialTheme.colorScheme.primary
						)
					)
				} else {
					topBar(drawerState, scope)
				}
			},
			floatingActionButton = { floatingActionButton?.invoke() },
			snackbarHost = { snackbarHost?.invoke() },
			content = content
		)
	}
}
