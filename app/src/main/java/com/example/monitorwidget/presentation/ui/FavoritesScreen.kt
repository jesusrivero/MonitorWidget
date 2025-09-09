package com.example.monitorwidget.presentation.ui



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.monitorwidget.domain.enums.FavoriteSuggestion
import com.example.monitorwidget.domain.enums.NavigationRoute
import com.example.monitorwidget.domain.model.DollarRates
import com.example.monitorwidget.domain.model.entity.FavoriteAmountEntity
import com.example.monitorwidget.domain.usecase.FavoritesUiState
import com.example.monitorwidget.domain.viewmodels.FavoritesViewModel
import java.text.DecimalFormat
import com.example.monitorwidget.presentation.ui.commons.DrawerScaffold
import com.example.monitorwidget.presentation.utils.getSuggestedFavorites
import com.example.monitorwidget.ui.theme.domain.model.viewmodel.DollarViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
	dollarViewModel: DollarViewModel = hiltViewModel(),
	favoritesViewModel: FavoritesViewModel = hiltViewModel(),
	navController: NavController,
) {
	val isRatesLoading by dollarViewModel.isLoading.collectAsState()
	val rates = dollarViewModel.rates.collectAsState()
	val uiState by favoritesViewModel.uiState.collectAsState()
	

	var dialogState by remember { mutableStateOf<FavoriteDialogState>(FavoriteDialogState.Hidden) }
	
	val snackbarHostState = remember { SnackbarHostState() }
	val coroutineScope = rememberCoroutineScope()
	val clipboardManager = LocalClipboardManager.current
	

	fun showCreateDialog() {
		dialogState = FavoriteDialogState.Create()
	}
	
	fun showEditDialog(favorite: FavoriteAmountEntity) {
		dialogState = FavoriteDialogState.Edit(favorite)
	}
	
	fun hideDialog() {
		dialogState = FavoriteDialogState.Hidden
	}
	
	fun handleSuggestionClick(suggestion: FavoriteSuggestion) {
		dialogState = FavoriteDialogState.Create(
			initialName = suggestion.name,
			initialAmount = suggestion.amountUsd.toString()
		)
	}
	
	// Mostrar snackbar con mensajes o errores
	LaunchedEffect(uiState.message, uiState.error) {
		uiState.message?.let {
			coroutineScope.launch { snackbarHostState.showSnackbar(it) }
			favoritesViewModel.clearMessage()
		}
		uiState.error?.let {
			coroutineScope.launch { snackbarHostState.showSnackbar("⚠️ Error: $it") }
			favoritesViewModel.clearMessage()
		}
	}
	

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
					IconButton(onClick = { dollarViewModel.fetchRates() }) {
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
		Box(modifier = Modifier.fillMaxSize()) {
			FavoriteContent(
				modifier = Modifier
					.padding(padding)
					.fillMaxSize()
					.padding(16.dp),
				rates = rates.value,
				uiState = uiState,
				onRetryRates = { dollarViewModel.fetchRates() },
				onAddFavoriteClick = { showCreateDialog() },
				onEditFavorite = { showEditDialog(it) },
				isRatesLoading = isRatesLoading,
				onDeleteFavorite = { favoritesViewModel.deleteFavorite(it) },
				onCopyAmount = { amountBs ->
					clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(amountBs))
					coroutineScope.launch {
						snackbarHostState.showSnackbar("📋 Monto copiado al portapapeles")
					}
				},
				onSuggestionClick = { handleSuggestionClick(it) }
			)
			
			SnackbarHost(
				hostState = snackbarHostState,
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(WindowInsets.navigationBars.asPaddingValues())
			)
		}
	}
	
	// Diálogo de crear/editar favorito
	FavoriteDialog(
		state = dialogState,
		currentRate = rates.value?.bcv ?: 0.0,
		onDismiss = { hideDialog() },
		onConfirm = { name, amount ->
			when (val state = dialogState) {
				is FavoriteDialogState.Create -> {
					favoritesViewModel.addFavorite(name, amount)
				}
				is FavoriteDialogState.Edit -> {
					val updatedFavorite = state.favorite.copy(
						name = name,
						amountUsd = amount
					)
					favoritesViewModel.updateFavorite(updatedFavorite)
				}
				FavoriteDialogState.Hidden -> {}
			}
			hideDialog()
		}
	)
}

@Composable
private fun FavoriteContent(
	modifier: Modifier = Modifier,
	rates: DollarRates?,
	uiState: FavoritesUiState,
	isRatesLoading: Boolean, // ✅ nuevo parámetro
	onRetryRates: () -> Unit,
	onAddFavoriteClick: () -> Unit,
	onEditFavorite: (FavoriteAmountEntity) -> Unit,
	onDeleteFavorite: (FavoriteAmountEntity) -> Unit,
	onCopyAmount: (String) -> Unit,
	onSuggestionClick: (FavoriteSuggestion) -> Unit
) {
	Column(
		modifier = modifier.verticalScroll(rememberScrollState()),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		if (rates == null) {
			if (isRatesLoading) { // ✅ usamos el estado del DollarViewModel
				CircularProgressIndicator(
					modifier = Modifier.padding(32.dp)
				)
			} else {
				ErrorState(
					message = "No hay tasas disponibles",
					onRetry = onRetryRates
				)
			}
			return@Column
		}
		
	
		CurrentRateCard(rate = rates.bcv)
		
		Spacer(modifier = Modifier.height(24.dp))
		

		FavoriteSection(
			uiState = uiState,
			rate = rates.bcv,
			onAddClick = onAddFavoriteClick,
			onEdit = onEditFavorite,
			onDelete = onDeleteFavorite,
			onCopy = onCopyAmount
		)
		
		Spacer(modifier = Modifier.height(24.dp))
		
		
		if (uiState.favorites.isEmpty() && !uiState.isLoading) {
			SuggestionsSection(onSuggestionClick = onSuggestionClick)
		}
	}
}

@Composable
private fun CurrentRateCard(rate: Double) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.primaryContainer
		)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 16.dp, horizontal = 24.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			Text(
				text = "💱 Tasa Actual BCV",
				style = MaterialTheme.typography.titleLarge,
				fontWeight = FontWeight.Bold,
				fontSize = 20.sp
			)
			
			val formatter = DecimalFormat("#,##0.00")
			Text(
				text = "${formatter.format(rate)} Bs",
				style = MaterialTheme.typography.headlineMedium,
				fontWeight = FontWeight.Bold,
				fontSize = 28.sp,
				color = MaterialTheme.colorScheme.primary
			)
			
			Text(
				text = "Por cada $1 USD",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
				fontSize = 15.sp
			)
		}
	}
}

@Composable
private fun FavoriteSection(
	uiState: FavoritesUiState,
	rate: Double,
	onAddClick: () -> Unit,
	onEdit: (FavoriteAmountEntity) -> Unit,
	onDelete: (FavoriteAmountEntity) -> Unit,
	onCopy: (String) -> Unit
) {

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
			onClick = onAddClick,
			modifier = Modifier.background(
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
	
	
	when {
		uiState.isLoading -> {
			LoadingState()
		}
		uiState.favorites.isEmpty() -> {
			EmptyFavoritesState()
		}
		else -> {
			FavoritesList(
				favorites = uiState.favorites,
				rate = rate,
				onEdit = onEdit,
				onDelete = onDelete,
				onCopy = onCopy
			)
		}
	}
}

@Composable
private fun LoadingState() {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(200.dp),
		contentAlignment = Alignment.Center
	) {
		CircularProgressIndicator()
	}
}

@Composable
private fun EmptyFavoritesState() {
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
			modifier = Modifier.padding(32.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = "🎯",
				style = MaterialTheme.typography.displaySmall
			)
			Spacer(modifier = Modifier.height(12.dp))
			Text(
				text = "Aún no tienes favoritos",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Medium
			)
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				text = "Guarda tus montos más consultados para acceso rápido",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
			)
		}
	}
}

@Composable
private fun FavoritesList(
	favorites: List<FavoriteAmountEntity>,
	rate: Double,
	onEdit: (FavoriteAmountEntity) -> Unit,
	onDelete: (FavoriteAmountEntity) -> Unit,
	onCopy: (String) -> Unit
) {
	LazyColumn(
		modifier = Modifier
			.fillMaxWidth()
			.heightIn(max = 400.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		items(favorites, key = { it.id }) { favorite ->
			FavoriteCard(
				favorite = favorite,
				rate = rate,
				onEdit = { onEdit(favorite) },
				onDelete = { onDelete(favorite) },
				onCopy = onCopy
			)
		}
	}
}

@Composable
private fun SuggestionsSection(
	onSuggestionClick: (FavoriteSuggestion) -> Unit
) {
	Text(
		text = "💭 Sugerencias populares",
		style = MaterialTheme.typography.titleMedium,
		fontWeight = FontWeight.Bold,
		modifier = Modifier.fillMaxWidth()
	)
	
	Spacer(modifier = Modifier.height(12.dp))
	
	LazyRow(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		contentPadding = PaddingValues(horizontal = 4.dp)
	) {
		items(getSuggestedFavorites()) { suggestion ->
			SuggestionChip(
				onClick = { onSuggestionClick(suggestion) },
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



@Composable
fun FavoriteCard(
	favorite: FavoriteAmountEntity,
	rate: Double,
	onEdit: (FavoriteAmountEntity) -> Unit,
	onDelete: (FavoriteAmountEntity) -> Unit,
	onCopy: (String) -> Unit
) {
	val formatter = DecimalFormat("#,##0.00")
	
	// Formateo en USD y en Bs
	val usdFormatted = formatter.format(favorite.amountUsd)
	val bolivares = favorite.amountUsd * rate
	val bolivaresFormatted = formatter.format(bolivares)
	
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(12.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surface
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
		
			Column(
				modifier = Modifier
					.weight(1f)
					.padding(end = 8.dp)
			) {
				
				Text(
					text = "💡 ${favorite.name} - $$usdFormatted USD",
					style = MaterialTheme.typography.bodyLarge,
					fontWeight = FontWeight.SemiBold,
					color = MaterialTheme.colorScheme.onSurface,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				
				Spacer(modifier = Modifier.height(4.dp))
				

				Text(
					text = "$bolivaresFormatted Bs",
					style = MaterialTheme.typography.bodyMedium,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.primary,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
			}
			

			Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
				IconButton(
					onClick = { onCopy(bolivaresFormatted) },
					modifier = Modifier.size(40.dp)
				) {
					Icon(
						imageVector = Icons.Default.ContentCopy,
						contentDescription = "Copiar monto",
						tint = MaterialTheme.colorScheme.primary,
						modifier = Modifier.size(18.dp)
					)
				}
				
				IconButton(
					onClick = { onEdit(favorite) },
					modifier = Modifier.size(40.dp)
				) {
					Icon(
						imageVector = Icons.Default.Edit,
						contentDescription = "Editar favorito",
						tint = MaterialTheme.colorScheme.secondary,
						modifier = Modifier.size(18.dp)
					)
				}
				
				IconButton(
					onClick = { onDelete(favorite) },
					modifier = Modifier.size(40.dp)
				) {
					Icon(
						imageVector = Icons.Default.Delete,
						contentDescription = "Eliminar favorito",
						tint = MaterialTheme.colorScheme.error,
						modifier = Modifier.size(18.dp)
					)
				}
			}
		}
	}
}






sealed class FavoriteDialogState {
	object Hidden : FavoriteDialogState()
	data class Create(
		val initialName: String = "",
		val initialAmount: String = ""
	) : FavoriteDialogState()
	data class Edit(val favorite: FavoriteAmountEntity) : FavoriteDialogState()
}

@Composable
fun FavoriteDialog(
	state: FavoriteDialogState,
	currentRate: Double,
	onDismiss: () -> Unit,
	onConfirm: (name: String, amount: Double) -> Unit
) {
	if (state == FavoriteDialogState.Hidden) return
	
	var favoriteName by remember(state) {
		mutableStateOf(
			when (state) {
				is FavoriteDialogState.Create -> state.initialName
				is FavoriteDialogState.Edit -> state.favorite.name
				FavoriteDialogState.Hidden -> ""
			}
		)
	}
	
	var favoriteAmount by remember(state) {
		mutableStateOf(
			when (state) {
				is FavoriteDialogState.Create -> state.initialAmount
				is FavoriteDialogState.Edit -> state.favorite.amountUsd.toString()
				FavoriteDialogState.Hidden -> ""
			}
		)
	}
	
	val title = when (state) {
		is FavoriteDialogState.Create -> "💡 Nuevo Favorito"
		is FavoriteDialogState.Edit -> "✏️ Editar Favorito"
		FavoriteDialogState.Hidden -> ""
	}
	
	val confirmButtonText = when (state) {
		is FavoriteDialogState.Create -> "Guardar"
		is FavoriteDialogState.Edit -> "Actualizar"
		FavoriteDialogState.Hidden -> ""
	}
	
	val isValid = favoriteName.isNotBlank() &&
			favoriteAmount.toDoubleOrNull()?.let { it > 0 } == true
	
	AlertDialog(
		onDismissRequest = onDismiss,
		containerColor = MaterialTheme.colorScheme.surfaceVariant,
		title = {
			Text(
				text = title,
				style = MaterialTheme.typography.titleLarge,
				fontWeight = FontWeight.Bold
			)
		},
		text = {
			Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
					singleLine = true,
					isError = favoriteName.isBlank()
				)
				
				OutlinedTextField(
					value = favoriteAmount,
					onValueChange = { newValue ->
						if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
							favoriteAmount = newValue
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
					singleLine = true,
					isError = favoriteAmount.toDoubleOrNull()?.let { it <= 0 } != false
				)
				
				
				val previewAmount = favoriteAmount.toDoubleOrNull() ?: 0.0
				if (previewAmount > 0 && currentRate > 0) {
					val formatter = DecimalFormat("#,##0.00")
					
					val usdFormatted = formatter.format(previewAmount)
					val bolivares = previewAmount * currentRate
					val bolivaresFormatted = formatter.format(bolivares)
					
					Card(
						modifier = Modifier.fillMaxWidth(),
						colors = CardDefaults.cardColors(
							containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
						)
					) {
						Row(
							modifier = Modifier.padding(12.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							Icon(
								imageVector = Icons.Default.Visibility,
								contentDescription = null,
								tint = MaterialTheme.colorScheme.primary,
								modifier = Modifier.size(16.dp)
							)
							Spacer(modifier = Modifier.width(8.dp))
							Text(
								text = "Vista previa: $$usdFormatted = $bolivaresFormatted Bs",
								style = MaterialTheme.typography.bodyMedium,
								fontWeight = FontWeight.Medium,
								color = MaterialTheme.colorScheme.onSurface
							)
						}
					}
				}
			}
		},
		confirmButton = {
			Button(
				onClick = {
					val amount = favoriteAmount.toDoubleOrNull()
					if (favoriteName.isNotBlank() && amount != null && amount > 0) {
						onConfirm(favoriteName.trim(), amount)
					}
				},
				enabled = isValid
			) {
				Text(confirmButtonText)
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text("Cancelar")
			}
		}
	)
}




