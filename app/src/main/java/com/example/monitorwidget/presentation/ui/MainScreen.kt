package com.example.monitorwidget.presentation.ui


import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.monitorwidget.R
import com.example.monitorwidget.domain.enums.CalculationMode
import com.example.monitorwidget.domain.enums.NavigationRoute
import com.example.monitorwidget.presentation.ui.commons.DrawerScaffold
import com.example.monitorwidget.presentation.utils.captureView
import com.example.monitorwidget.presentation.utils.shareBitmap
import com.example.monitorwidget.ui.theme.domain.model.viewmodel.DollarViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DollarCalculatorScreen(
	navController: NavController,
	viewModel: DollarViewModel = hiltViewModel()
) {
	val rates by viewModel.rates.collectAsState()
	val loading by viewModel.isLoading.collectAsState()
	val error by viewModel.error.collectAsState()
	
	val snackbarHostState = remember { SnackbarHostState() }
	val coroutineScope = rememberCoroutineScope()
	val clipboardManager = LocalClipboardManager.current
	val formatter = DecimalFormat("#,##0.00")
	
	var dollarInput by rememberSaveable { mutableStateOf("") }
	var calculationMode by rememberSaveable { mutableStateOf(CalculationMode.USD_TO_BS) }
	
	val inputAmount = dollarInput.toDoubleOrNull() ?: 0.0
	val isValidInput = inputAmount > 0
	
	val bcvResult = when (calculationMode) {
		CalculationMode.USD_TO_BS -> inputAmount * (rates?.bcv ?: 0.0)
		CalculationMode.BS_TO_USD -> inputAmount / (rates?.bcv ?: 1.0)
	}
	
	val context = LocalContext.current
	val activity = context as Activity
	

	DrawerScaffold(
		currentRoute = NavigationRoute.CALCULATOR,
		navController = navController,
		topBar = { drawerState, scope ->
			CenterAlignedTopAppBar(
				title = {
					Text(
						text = "💱 Calculadora BCV",
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
					IconButton(onClick = { viewModel.fetchRates() }) {
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
		floatingActionButton = {
			FloatingActionButton(
				onClick = {
					val rootView = activity.window.decorView.rootView
					val bitmap = captureView(rootView)
					shareBitmap(context, bitmap)
				},
				containerColor = MaterialTheme.colorScheme.primary
			) {
				Icon(
					imageVector = Icons.Default.Share,
					contentDescription = "Compartir captura",
					tint = MaterialTheme.colorScheme.onPrimary
				)
			}
		},
		snackbarHost = { SnackbarHost(snackbarHostState) }
	) { padding ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
		) {
			when {
				loading -> {
					Box(
						modifier = Modifier.fillMaxSize(),
						contentAlignment = Alignment.Center
					) {
						CircularProgressIndicator()
					}
				}
				
				error != null -> {
					ErrorState(message = error ?: "Error desconocido") {
						viewModel.fetchRates()
					}
				}
				
				rates == null -> {
					ErrorState(message = "No hay tasas disponibles") {
						viewModel.fetchRates()
					}
				}
				
				else -> {
					Box(modifier = Modifier.fillMaxSize()) {
						Image(
							painter = painterResource(id = R.drawable.ic_logo_splash),
							contentDescription = null,
							modifier = Modifier
								.fillMaxSize()
								.align(Alignment.TopCenter),
							contentScale = ContentScale.Fit
						)
						
						Column(
							modifier = Modifier
								.fillMaxSize()
								.verticalScroll(rememberScrollState())
								.padding(horizontal = 8.dp, vertical = 12.dp),
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							Card(
								modifier = Modifier
									.fillMaxWidth()
									.padding(horizontal = 8.dp, vertical = 12.dp),
								shape = RoundedCornerShape(20.dp),
								elevation = CardDefaults.cardElevation(12.dp),
								colors = CardDefaults.cardColors(
									containerColor = MaterialTheme.colorScheme.surface
								)
							) {
								Column(
									modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
									horizontalAlignment = Alignment.CenterHorizontally
								) {
									CalculatorHeader(
										currentMode = calculationMode,
										onModeChange = {
											calculationMode = it
											dollarInput = ""
										}
									)
									
									Spacer(modifier = Modifier.height(24.dp))
									
									EnhancedInputField(
										value = dollarInput,
										onValueChange = { dollarInput = it },
										mode = calculationMode,
										bcvRate = rates?.bcv,
										modifier = Modifier.fillMaxWidth()
									)
									
									Spacer(modifier = Modifier.height(32.dp))
									
									AnimatedVisibility(
										visible = isValidInput,
										enter = fadeIn() + slideInVertically(),
										exit = fadeOut() + slideOutVertically()
									) {
										ResultsSection(
											bcvResult = bcvResult,
											formatter = formatter,
											targetCurrency = if (calculationMode == CalculationMode.USD_TO_BS) "Bs" else "USD",
											onCopy = { text ->
												clipboardManager.setText(AnnotatedString(text))
												coroutineScope.launch {
													snackbarHostState.showSnackbar("✅ Copiado: $text")
												}
											}
										)
									}
								}
							}
						}
					}
				}
			}
		}
	}
}




@Composable
fun CalculatorHeader(
	currentMode: CalculationMode,
	onModeChange: (CalculationMode) -> Unit
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "🏛️ Calculadora BCV",
			style = MaterialTheme.typography.headlineSmall,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.primary
		)
		
		Spacer(modifier = Modifier.height(8.dp))
		
		Text(
			text = "Tasa Oficial del Banco Central",
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
		)
		
		Spacer(modifier = Modifier.height(16.dp))
		
		Card(
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
			),
			shape = RoundedCornerShape(16.dp)
		) {
			Row(
				modifier = Modifier.padding(4.dp),
				horizontalArrangement = Arrangement.spacedBy(4.dp)
			) {
				ModeButton(
					text = "USD → Bs",
					isSelected = currentMode == CalculationMode.USD_TO_BS,
					onClick = { onModeChange(CalculationMode.USD_TO_BS) }
				)
				ModeButton(
					text = "Bs → USD",
					isSelected = currentMode == CalculationMode.BS_TO_USD,
					onClick = { onModeChange(CalculationMode.BS_TO_USD) }
				)
			}
		}
	}
}




@Composable
fun ErrorState(
	message: String,
	onRetry: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.errorContainer
		)
	) {
		Column(
			modifier = Modifier.padding(24.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = "⚠️",
				style = MaterialTheme.typography.displayMedium
			)
			
			Spacer(modifier = Modifier.height(16.dp))
			
			Text(
				text = message,
				style = MaterialTheme.typography.titleLarge,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onErrorContainer
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			Button(onClick = onRetry) {
				Text("Reintentar")
			}
		}
	}
}



@Composable
fun ModeButton(
	text: String,
	isSelected: Boolean,
	onClick: () -> Unit
) {
	Button(
		onClick = onClick,
		colors = ButtonDefaults.buttonColors(
			containerColor = if (isSelected)
				MaterialTheme.colorScheme.primary
			else
				Color.Transparent,
			contentColor = if (isSelected)
				MaterialTheme.colorScheme.onPrimary
			else
				MaterialTheme.colorScheme.onSurface
		),
		shape = RoundedCornerShape(12.dp),
		elevation = if (isSelected)
			ButtonDefaults.buttonElevation(4.dp)
		else
			ButtonDefaults.buttonElevation(0.dp),
		modifier = Modifier.width(130.dp)
	) {
		Text(
			text = text,
			style = MaterialTheme.typography.labelLarge,
			fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
		)
	}
}


@Composable
fun EnhancedInputField(
	value: String,
	onValueChange: (String) -> Unit,
	mode: CalculationMode,
	bcvRate: Double?,
	modifier: Modifier = Modifier
) {
	val focusManager = LocalFocusManager.current
	
	OutlinedTextField(
		value = value,
		onValueChange = { input ->
			val filteredInput = input.filter { it.isDigit() || it == '.' }
			val finalInput = if (filteredInput.count { it == '.' } <= 1) filteredInput else value
			onValueChange(finalInput)
		},
		placeholder = {
			if (bcvRate != null) {
				Text(
					text = "BCV: ${DecimalFormat("#,##0.00").format(bcvRate)} Bs/USD",
					style = MaterialTheme.typography.labelSmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		},
		leadingIcon = {
			Icon(
				imageVector = if (mode == CalculationMode.USD_TO_BS)
					Icons.Default.AttachMoney else Icons.Default.CurrencyExchange,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary
			)
		},
		trailingIcon = {
			if (value.isNotEmpty()) {
				IconButton(
					onClick = {
						onValueChange("")
						focusManager.clearFocus()
					}
				) {
					Icon(
						imageVector = Icons.Default.Clear,
						contentDescription = "Limpiar",
						tint = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}
		},
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			keyboardType = KeyboardType.Decimal,
			imeAction = ImeAction.Done
		),
		keyboardActions = KeyboardActions(
			onDone = { focusManager.clearFocus() }
		),
		modifier = modifier.fillMaxWidth(),
		shape = RoundedCornerShape(16.dp),
		colors = OutlinedTextFieldDefaults.colors(
			focusedBorderColor = MaterialTheme.colorScheme.primary,
			focusedLabelColor = MaterialTheme.colorScheme.primary
		)
	)
}



@Composable
fun ResultsSection(
	bcvResult: Double,
	formatter: DecimalFormat,
	targetCurrency: String,
	onCopy: (String) -> Unit
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "💰 Resultado de Conversión",
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.SemiBold,
			color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
		)
		
		Spacer(modifier = Modifier.height(16.dp))
		
		BcvResultCard(
			value = formatter.format(bcvResult),
			currency = targetCurrency,
			onCopy = onCopy
		)
	}
}

@Composable
fun BcvResultCard(
	value: String,
	currency: String,
	onCopy: (String) -> Unit
) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surface
		),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
		elevation = CardDefaults.cardElevation(2.dp)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp), // un poco más compacto
			horizontalAlignment = Alignment.CenterHorizontally
		) {

			Text(
				text = "BCV",
				style = MaterialTheme.typography.labelMedium, // más pequeño
				fontWeight = FontWeight.Medium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				letterSpacing = 0.5.sp
			)
			
			Spacer(modifier = Modifier.height(2.dp))
			

			Text(
				text = "$value $currency",
				style = MaterialTheme.typography.headlineSmall,
				fontWeight = FontWeight.SemiBold,
				color = MaterialTheme.colorScheme.onSurface,
				textAlign = TextAlign.Center,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
			
			Spacer(modifier = Modifier.height(12.dp))
			
			
			TextButton(
				onClick = { onCopy("$value $currency") },
				colors = ButtonDefaults.textButtonColors(
					contentColor = MaterialTheme.colorScheme.primary
				)
			) {
				Icon(
					imageVector = Icons.Default.ContentCopy,
					contentDescription = null,
					modifier = Modifier.size(16.dp)
				)
				Spacer(modifier = Modifier.width(6.dp))
				Text(
					text = "Copiar",
					style = MaterialTheme.typography.labelMedium,
					fontWeight = FontWeight.Medium
				)
			}
		}
	}
}
















