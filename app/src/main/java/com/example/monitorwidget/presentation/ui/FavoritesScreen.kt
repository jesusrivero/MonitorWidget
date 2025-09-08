package com.example.monitorwidget.presentation.ui


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.text.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.monitorwidget.domain.model.entity.FavoriteAmountEntity
import com.example.monitorwidget.domain.viewmodels.FavoritesViewModel
import com.example.monitorwidget.presentation.navegacion.AppRoutes
import java.text.DecimalFormat
import com.example.monitorwidget.presentation.ui.commons.DrawerScaffold
import com.example.monitorwidget.presentation.ui.commons.NavigationRoute
import com.example.monitorwidget.ui.theme.domain.model.viewmodel.DollarViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
	fun FavoriteScreen(
	dollarViewModel: DollarViewModel = hiltViewModel(),
	favoritesViewModel: FavoritesViewModel = hiltViewModel(),
	navController: NavController,
) {
	val rates = dollarViewModel.rates.collectAsState()
	val favorites by favoritesViewModel.favorites.collectAsState()
	
	// Estados para el diálogo de agregar favorito
	var showAddDialog by remember { mutableStateOf(false) }
	var favoriteName by remember { mutableStateOf("") }
	var favoriteAmount by remember { mutableStateOf("") }
	
	// Estados para Snackbar y Clipboard
	val snackbarHostState = remember { SnackbarHostState() }
	val coroutineScope = rememberCoroutineScope()
	val clipboardManager = LocalClipboardManager.current
	
	DrawerScaffold(
		currentRoute = NavigationRoute.LIVE_RATES,
		navController = navController,
		topBar = { drawerState, scope ->
			CenterAlignedTopAppBar(
				title = {
					Text(
						text = "Favoritos",
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
				actions = {
					IconButton(
						onClick = { dollarViewModel.fetchRates() }
					) {
						Icon(
							imageVector = Icons.Default.Refresh,
							contentDescription = "Refrescar",
							tint = MaterialTheme.colorScheme.onPrimary
						)
					}
				},
				colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
					containerColor = MaterialTheme.colorScheme.primary
				)
			)
		},
	) { padding ->
		Column(
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
				.padding(16.dp)
				.verticalScroll(rememberScrollState()),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			if (rates.value == null) {
				ErrorState(
					message = "No hay tasas disponibles",
					onRetry = { dollarViewModel.fetchRates() }
				)
				return@Column
			}
			
			// Header con información de la tasa actual
			Card(
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(16.dp),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer
				)
			) {
				Column(
					modifier = Modifier
						.fillMaxWidth() // para centrar horizontalmente en todo el ancho
						.padding(vertical = 8.dp, horizontal = 20.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(4.dp) // reduce espacio vertical
				) {
					Text(
						text = "💱 Tasa Actual BCV",
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold,
						fontSize = (20.sp),
					
					)
					val formatter = DecimalFormat("#,##0.00")
					Text(
						text = "${formatter.format(rates.value?.bcv ?: 0.0)} Bs",
						style = MaterialTheme.typography.headlineMedium,
						fontWeight = FontWeight.Bold,
						fontSize = (25.sp),
						color = MaterialTheme.colorScheme.primary,

					)
					Text(
						text = "Por cada $1 USD",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
						fontSize = (15.sp),
					)
				}
			}
				
				Spacer(modifier = Modifier.height(24.dp))
			
			// Sección de favoritos
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = "💡 Mis Favoritos",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)
				
				IconButton(
					onClick = { showAddDialog = true },
					modifier = Modifier
						.background(
							MaterialTheme.colorScheme.primary,
							CircleShape
						)
				) {
					Icon(
						imageVector = Icons.Default.Add,
						contentDescription = "Agregar favorito",
						tint = MaterialTheme.colorScheme.onPrimary
					)
				}
			}
			
			Spacer(modifier = Modifier.height(16.dp))
			
			if (favorites.isEmpty()) {
				Card(
					modifier = Modifier.fillMaxWidth(),
					shape = RoundedCornerShape(12.dp),
					colors = CardDefaults.cardColors(
						containerColor = MaterialTheme.colorScheme.surface
					),
					border = BorderStroke(
						1.dp,
						MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
					)
				) {
					Column(
						modifier = Modifier.padding(24.dp),
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Text(
							text = "🎯",
							style = MaterialTheme.typography.displaySmall
						)
						Spacer(modifier = Modifier.height(8.dp))
						Text(
							text = "Aún no tienes favoritos",
							style = MaterialTheme.typography.titleMedium,
							fontWeight = FontWeight.Medium
						)
						Text(
							text = "Guarda tus montos más consultados para acceso rápido",
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
						)
						Spacer(modifier = Modifier.height(16.dp))
					}
				}
			} else {
				LazyColumn(
					modifier = Modifier
						.fillMaxWidth()
						.heightIn(max = 400.dp),
					verticalArrangement = Arrangement.spacedBy(12.dp)
				) {
					items(favorites) { favorite ->
						FavoriteCard(
							favorite = favorite,
							rate = rates.value?.bcv ?: 0.0,
							onRemove = { favoritesViewModel.deleteFavorite(it) },
							onCopy = { amountBs ->
								clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(amountBs))
								coroutineScope.launch {
									snackbarHostState.showSnackbar("Monto en Bs copiado al portapapeles")
								}
							}
						)
					}
				}
			}
			
			Spacer(modifier = Modifier.height(24.dp))
			
			// Sugerencias de favoritos comunes
			if (favorites.isEmpty()) {
				Text(
					text = "💭 Sugerencias populares",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.align(Alignment.Start)
				)
				
				Spacer(modifier = Modifier.height(12.dp))
				
				LazyRow(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					contentPadding = PaddingValues(horizontal = 4.dp)
				) {
					items(getSuggestedFavorites()) { suggestion ->
						SuggestionChip(
							onClick = {
								favoriteName = suggestion.name
								favoriteAmount = suggestion.amountUsd.toString()
								showAddDialog = true
							},
							label = {
								Text(
									text = "${suggestion.icon} ${suggestion.name} $${suggestion.amountUsd}",
									style = MaterialTheme.typography.bodySmall
								)
							}
						)
					}
				}
			}
		}
	}
	
	// Diálogo para agregar favorito
	if (showAddDialog) {
		AlertDialog(
			onDismissRequest = {
				showAddDialog = false
				favoriteName = ""
				favoriteAmount = ""
			},
			title = {
				Text(
					text = "💡 Nuevo Favorito",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold
				)
			},
			text = {
				Column(
					verticalArrangement = Arrangement.spacedBy(16.dp)
				) {
					OutlinedTextField(
						value = favoriteName,
						onValueChange = { favoriteName = it },
						label = { Text("Nombre del favorito") },
						placeholder = { Text("Ej: Alquiler, Netflix, Sueldo...") },
						leadingIcon = {
							Icon(
								imageVector = Icons.Default.Label,
								contentDescription = null
							)
						},
						modifier = Modifier.fillMaxWidth(),
						singleLine = true
					)
					
					OutlinedTextField(
						value = favoriteAmount,
						onValueChange = {
							// Filtrar solo números y punto decimal
							if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
								favoriteAmount = it
							}
						},
						label = { Text("Monto en USD") },
						placeholder = { Text("0.00") },
						leadingIcon = {
							Text(
								text = "$",
								style = MaterialTheme.typography.titleMedium,
								color = MaterialTheme.colorScheme.primary
							)
						},
						modifier = Modifier.fillMaxWidth(),
						keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
						singleLine = true
					)
					
					// Preview del resultado
					val previewAmount = favoriteAmount.toDoubleOrNull() ?: 0.0
					if (previewAmount > 0 && rates.value != null) {
						val formatter = DecimalFormat("#,##0.00")
						val bolivares = previewAmount * (rates.value?.bcv ?: 0.0)
						
						Card(
							modifier = Modifier.fillMaxWidth(),
							colors = CardDefaults.cardColors(
								containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
							)
						) {
							Text(
								text = "Vista previa: $${previewAmount} = ${formatter.format(bolivares)} Bs",
								modifier = Modifier.padding(12.dp),
								style = MaterialTheme.typography.bodyMedium,
								fontWeight = FontWeight.Medium,
								color = MaterialTheme.colorScheme.onSurface
							)
						}
					}
				}
			},
			confirmButton = {
				Button(
					onClick = {
						val amount = favoriteAmount.toDoubleOrNull()
						if (favoriteName.isNotBlank() && amount != null && amount > 0) {
							favoritesViewModel.addFavorite(favoriteName.trim(), amount)
							showAddDialog = false
							favoriteName = ""
							favoriteAmount = ""
						}
					},
					enabled = favoriteName.isNotBlank() &&
							favoriteAmount.toDoubleOrNull()?.let { it > 0 } == true
				) {
					Text("Guardar")
				}
			},
			dismissButton = {
				TextButton(
					onClick = {
						showAddDialog = false
						favoriteName = ""
						favoriteAmount = ""
					}
				) {
					Text("Cancelar")
				}
			}
		)
	}
}



@Composable
fun FavoriteCard(
	favorite: FavoriteAmountEntity,
	rate: Double,
	onRemove: (FavoriteAmountEntity) -> Unit,
	onCopy: (String) -> Unit
) {
	val formatter = DecimalFormat("#,##0.00")
	val bolivares = favorite.amountUsd * rate
	val bolivaresFormatted = formatter.format(bolivares)
	
	Column {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 12.dp, vertical = 6.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(modifier = Modifier.weight(1f)) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Text(
						text = "💡 ${favorite.name}",
						style = MaterialTheme.typography.bodyLarge,
						fontWeight = FontWeight.SemiBold,
						color = MaterialTheme.colorScheme.onSurface
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						text = "$bolivaresFormatted Bs",
						style = MaterialTheme.typography.bodyMedium,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.primary
					)
				}
				
				Text(
					text = "$${favorite.amountUsd} USD",
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
				)
			}
			
			Row {
				IconButton(
					onClick = { onCopy(bolivaresFormatted) },
					modifier = Modifier.size(32.dp)
				) {
					Icon(
						imageVector = Icons.Default.ContentCopy,
						contentDescription = "Copiar monto en Bs",
						tint = MaterialTheme.colorScheme.primary
					)
				}
				
				IconButton(
					onClick = { onRemove(favorite) },
					modifier = Modifier.size(32.dp)
				) {
					Icon(
						imageVector = Icons.Default.Delete,
						contentDescription = "Eliminar favorito",
						tint = MaterialTheme.colorScheme.error
					)
				}
			}
		}
		
		// Línea divisora simple
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(1.dp)
				.background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
				.align(Alignment.CenterHorizontally)
		)
	}
}



// Data class para las sugerencias
data class FavoriteSuggestion(
	val name: String,
	val amountUsd: Double,
	val icon: String
)

// Función para obtener sugerencias populares
fun getSuggestedFavorites(): List<FavoriteSuggestion> {
	return listOf(
		FavoriteSuggestion("Alquiler", 150.0, "🏠"),
		FavoriteSuggestion("Netflix", 15.0, "📺"),
		FavoriteSuggestion("Gym", 300.0, "💰"),
		FavoriteSuggestion("Mercado", 50.0, "🛒"),
		FavoriteSuggestion("Gasolina", 20.0, "⛽"),
		FavoriteSuggestion("Internet", 25.0, "🌐"),
	)
}

data class FavoriteAmount(
	val name: String,
	val amountUsd: Double
)