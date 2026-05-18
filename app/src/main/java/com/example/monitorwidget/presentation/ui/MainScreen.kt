package com.example.monitorwidget.presentation.ui


import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.monitorwidget.R
import com.example.monitorwidget.domain.model.enums.NavigationRoute
import com.example.monitorwidget.presentation.ui.commons.DrawerScaffold
import com.example.monitorwidget.presentation.utils.captureView
import com.example.monitorwidget.presentation.utils.formatLiveInput
import com.example.monitorwidget.presentation.utils.shareBitmap
import com.example.monitorwidget.ui.theme.domain.model.viewmodel.DollarViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DollarCalculatorScreen(
	navController: NavController,
	viewModel: DollarViewModel = hiltViewModel(),
) {
	val rates by viewModel.rates.collectAsState()
	val loading by viewModel.isLoading.collectAsState()
	val error by viewModel.error.collectAsState()
	val snackbarHostState = remember { SnackbarHostState() }
	val coroutineScope = rememberCoroutineScope()
	val clipboardManager = LocalClipboardManager.current
	val formatter = remember { DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US)) }
	var isUsd by rememberSaveable { mutableStateOf(true) }
	var foreignInput by rememberSaveable { mutableStateOf("") }
	var bsInput by rememberSaveable { mutableStateOf("") }
	var lastEdited by rememberSaveable { mutableStateOf("foreign") }
	var initialValueShown by rememberSaveable { mutableStateOf(true) }
	val activeRate = if (isUsd) rates?.bcv ?: 0.0 else rates?.eur ?: 0.0
	
	val onCurrencyChange = { newIsUsd: Boolean ->
		isUsd = newIsUsd
		foreignInput = "1"
		bsInput = ""
		lastEdited = "foreign"
		initialValueShown = true
	}
	
	val foreignDisplay = when {
		lastEdited == "bs" && bsInput.isNotEmpty() && activeRate > 0 -> {
			val bs = bsInput.toDoubleOrNull() ?: 0.0
			formatter.format(bs / activeRate)
		}
		lastEdited == "foreign" -> formatLiveInput(foreignInput)
		else -> foreignInput
	}
	
	val bsDisplay = when {
		lastEdited == "foreign" && foreignInput.isNotEmpty() && activeRate > 0 -> {
			val foreign = foreignInput.toDoubleOrNull() ?: 0.0
			formatter.format(foreign * activeRate)
		}
		lastEdited == "bs" -> formatLiveInput(bsInput)
		else -> bsInput
	}
	
	val context = LocalContext.current
	val activity = context as Activity
	val cardBounds = remember { mutableStateOf<Rect?>(null) }
	
	var showPreview by remember { mutableStateOf(false) }
	var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
	var hasAskedPermission by rememberSaveable { mutableStateOf(false) }
	
	val launcher = rememberLauncherForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { _ -> }
	
	LaunchedEffect(Unit) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasAskedPermission) {
			if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
				!= PackageManager.PERMISSION_GRANTED
			) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
			hasAskedPermission = true
		}
	}
	
	LaunchedEffect(rates, isUsd) {
		if (rates != null && foreignInput.isEmpty() && bsInput.isEmpty()) {
			foreignInput = "1"
			lastEdited = "foreign"
			initialValueShown = true
		}
	}
	
	if (showPreview) {
		SharePreviewDialog(
			bitmap = previewBitmap,
			onDismiss = { showPreview = false; previewBitmap = null },
			onShare = { previewBitmap?.let { shareBitmap(context, it) } },
			onSave = {
				previewBitmap?.let { bmp ->
					val saved = saveBitmapToGallery(context, bmp)
					coroutineScope.launch {
						snackbarHostState.showSnackbar(
							if (saved) "Imagen guardada" else "Error al guardar"
						)
					}
				}
			}
		)
	}
	
	DrawerScaffold(
		currentRoute = NavigationRoute.CALCULATOR,
		navController = navController,
		topBar = { drawerState, scope ->
			CenterAlignedTopAppBar(
				title = {
					Text(
						"Calculadora BCV",
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.onPrimary
					)
				},
				navigationIcon = {
					IconButton(onClick = { scope.launch { drawerState.open() } }) {
						Icon(
							Icons.Default.Menu, "Menú",
							tint = MaterialTheme.colorScheme.onPrimary
						)
					}
				},
				actions = {
					IconButton(onClick = { viewModel.fetchRates() }) {
						Icon(
							Icons.Default.Refresh, "Refrescar",
							tint = MaterialTheme.colorScheme.onPrimary
						)
					}
				},
				colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
					containerColor = MaterialTheme.colorScheme.primary
				),
				windowInsets = TopAppBarDefaults.windowInsets
			)
		},
		floatingActionButton = {
			FloatingActionButton(
				onClick = {
					cardBounds.value?.let { rect ->
						val root = activity.window.decorView.rootView
						previewBitmap = Bitmap.createBitmap(
							captureView(root),
							rect.left, rect.top,
							rect.width(), rect.height()
						)
						showPreview = true
					}
				},
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = MaterialTheme.colorScheme.onPrimary,
				shape = CircleShape
			) {
				Icon(Icons.Default.CameraAlt, "Capturar convertidor")
			}
		},
		snackbarHost = { SnackbarHost(snackbarHostState) }
	) { padding ->
		
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
				.background(MaterialTheme.colorScheme.background),
			contentAlignment = Alignment.Center
		) {
			when {
				loading -> FullScreenLoader()
				error != null -> ErrorState(error ?: "Error") { viewModel.fetchRates() }
				rates == null -> ErrorState("Sin tasas disponibles") { viewModel.fetchRates() }
				
				else -> {
					Column(
						modifier = Modifier
							.fillMaxSize()
							.imePadding()
							.verticalScroll(rememberScrollState())
							.padding(horizontal = 24.dp, vertical = 16.dp),
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Image(
							painter = painterResource(id = R.drawable.ic_logo_splash),
							contentDescription = "Logo",
							modifier = Modifier.size(90.dp),
							contentScale = ContentScale.Fit
						)
						
						Spacer(modifier = Modifier.height(20.dp))
						
						Card(
							modifier = Modifier
								.fillMaxWidth()
								.onGloballyPositioned { coords ->
									val r = coords.boundsInWindow()
									cardBounds.value = Rect(
										r.left.toInt(), r.top.toInt(),
										r.right.toInt(), r.bottom.toInt()
									)
								},
							shape = RoundedCornerShape(28.dp),
							elevation = CardDefaults.cardElevation(12.dp),
							colors = CardDefaults.cardColors(
								containerColor = MaterialTheme.colorScheme.surface
							)
						) {
							Column(
								modifier = Modifier
									.fillMaxWidth()
									.padding(horizontal = 24.dp, vertical = 24.dp),
								horizontalAlignment = Alignment.CenterHorizontally
							) {
								CurrencySegmentedSelector(
									isUsd = isUsd,
									onSelect = { newIsUsd ->
										if (newIsUsd != isUsd) onCurrencyChange(newIsUsd)
									}
								)
								
								Spacer(modifier = Modifier.height(24.dp))
								
								ConversionField(
									value = foreignDisplay,
									isEditing = lastEdited == "foreign",
									currencyLabel = if (isUsd) "USD" else "EUR",
									currencySymbol = if (isUsd) "$" else "€",
									placeholder = "0.00",
									isInitialValue = initialValueShown,
									onValueChange = { input ->
										val clean = input.replace(",", "").filter { it.isDigit() || it == '.' }
										if (clean.count { it == '.' } <= 1) {
											foreignInput = if (initialValueShown && clean.length > 1 && clean.startsWith("1")) {
												initialValueShown = false
												clean.removePrefix("1")
											} else {
												initialValueShown = false
												clean
											}
											lastEdited = "foreign"
											if (foreignInput.isEmpty()) bsInput = ""
										}
									},
									onCopy = {
										val txt = "$foreignDisplay ${if (isUsd) "USD" else "EUR"}"
										clipboardManager.setText(AnnotatedString(txt))
										coroutineScope.launch {
											snackbarHostState.showSnackbar("✅ Copiado: $txt")
										}
									}
								)
								
								Box(
									modifier = Modifier
										.fillMaxWidth()
										.padding(vertical = 4.dp),
									contentAlignment = Alignment.Center
								) {
									HorizontalDivider(
										color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
										thickness = 1.dp
									)
									Box(
										modifier = Modifier
											.size(32.dp)
											.background(
												MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
												CircleShape
											),
										contentAlignment = Alignment.Center
									) {
										Icon(
											Icons.Default.SwapVert, null,
											tint = MaterialTheme.colorScheme.primary,
											modifier = Modifier.size(16.dp)
										)
									}
								}
								
								ConversionField(
									value = bsDisplay,
									isEditing = lastEdited == "bs",
									currencyLabel = "VES",
									currencySymbol = "Bs",
									placeholder = "0.00",
									isInitialValue = false,
									onValueChange = { input ->
										val clean = input.replace(",", "").filter { it.isDigit() || it == '.' }
										if (clean.count { it == '.' } <= 1) {
											bsInput = clean
											lastEdited = "bs"
											initialValueShown = false
											if (clean.isEmpty()) foreignInput = ""
										}
									},
									onCopy = {
										clipboardManager.setText(AnnotatedString("$bsDisplay Bs"))
										coroutineScope.launch {
											snackbarHostState.showSnackbar("✅ Copiado: $bsDisplay Bs")
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

@Composable
fun CurrencySegmentedSelector(
	isUsd: Boolean,
	onSelect: (Boolean) -> Unit,
) {
	Surface(
		shape = RoundedCornerShape(14.dp),
		color = MaterialTheme.colorScheme.surfaceVariant,
		tonalElevation = 0.dp,
		modifier = Modifier.fillMaxWidth()
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(4.dp),
			horizontalArrangement = Arrangement.spacedBy(4.dp)
		) {
			listOf(true to "💵  Dólar  BCV", false to "💶  Euro  BCV").forEach { (optionIsUsd, label) ->
				val selected = isUsd == optionIsUsd
				val bgColor by animateColorAsState(
					targetValue = if (selected) MaterialTheme.colorScheme.primary
					else Color.Transparent,
					animationSpec = tween(200),
					label = "segBg"
				)
				val textColor by animateColorAsState(
					targetValue = if (selected) MaterialTheme.colorScheme.onPrimary
					else MaterialTheme.colorScheme.onSurfaceVariant,
					animationSpec = tween(200),
					label = "segText"
				)
				Surface(
					onClick = { onSelect(optionIsUsd) },
					shape = RoundedCornerShape(10.dp),
					color = bgColor,
					shadowElevation = if (selected) 2.dp else 0.dp,
					modifier = Modifier.weight(1f)
				) {
					Text(
						text = label,
						modifier = Modifier.padding(vertical = 10.dp),
						style = MaterialTheme.typography.labelLarge,
						fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
						color = textColor,
						textAlign = TextAlign.Center
					)
				}
			}
		}
	}
}

@Composable
fun ConversionField(
	value: String,
	isEditing: Boolean,
	currencyLabel: String,
	currencySymbol: String,
	placeholder: String,
	isInitialValue: Boolean = false,
	onValueChange: (String) -> Unit,
	onCopy: () -> Unit,
) {
	val focusManager = LocalFocusManager.current
	val interactionSource = remember { MutableInteractionSource() }
	val isFocused by interactionSource.collectIsFocusedAsState()
	
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Column(modifier = Modifier.width(48.dp)) {
			Text(
				text = currencySymbol,
				style = MaterialTheme.typography.titleLarge,
				fontWeight = FontWeight.Bold,
				color = if (isFocused) MaterialTheme.colorScheme.primary
				else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
			)
			Text(
				text = currencyLabel,
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
			)
		}
		
		BasicTextField(
			value = value,
			onValueChange = onValueChange,
			interactionSource = interactionSource,
			cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
			textStyle = MaterialTheme.typography.titleLarge.copy(
				fontWeight = FontWeight.Bold,
				color = if (isInitialValue)
					MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
				else
					MaterialTheme.colorScheme.onSurface,
				textAlign = TextAlign.End
			),
			keyboardOptions = KeyboardOptions(
				keyboardType = KeyboardType.Decimal,
				imeAction = ImeAction.Done
			),
			keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
			singleLine = true,
			modifier = Modifier.weight(1f),
			decorationBox = { innerTextField ->
				Box(
					modifier = Modifier.fillMaxWidth(),
					contentAlignment = Alignment.CenterEnd
				) {
					if (value.isEmpty()) {
						Text(
							text = placeholder,
							style = MaterialTheme.typography.titleLarge.copy(
								fontWeight = FontWeight.Bold,
								textAlign = TextAlign.End
							),
							color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
							modifier = Modifier.fillMaxWidth()
						)
					}
					innerTextField()
				}
			}
		)
		
		Spacer(modifier = Modifier.width(8.dp))
		
		IconButton(
			onClick = onCopy,
			modifier = Modifier.size(28.dp)
		) {
			Icon(
				Icons.Default.ContentCopy, "Copiar $currencyLabel",
				tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
				modifier = Modifier.size(16.dp)
			)
		}
	}
}

@Composable
private fun FullScreenLoader() {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			CircularProgressIndicator(
				modifier = Modifier.size(48.dp),
				strokeWidth = 3.dp,
				color = MaterialTheme.colorScheme.primary
			)
			Spacer(modifier = Modifier.height(16.dp))
			Text(
				text = "Obteniendo tasas",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
			)
		}
	}
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(24.dp),
			shape = RoundedCornerShape(20.dp),
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.errorContainer
			),
			elevation = CardDefaults.cardElevation(4.dp)
		) {
			Column(
				modifier = Modifier.padding(32.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text("⚠️", style = MaterialTheme.typography.displaySmall)
				
				Spacer(modifier = Modifier.height(16.dp))
				
				Text(
					text = message,
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold,
					color = MaterialTheme.colorScheme.onErrorContainer,
					textAlign = TextAlign.Center
				)
				
				Spacer(modifier = Modifier.height(24.dp))
				
				Button(
					onClick = onRetry,
					shape = RoundedCornerShape(12.dp),
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.error
					)
				) {
					Icon(
						Icons.Default.Refresh, null,
						modifier = Modifier.size(16.dp)
					)
					Spacer(modifier = Modifier.width(6.dp))
					Text("Reintentar", fontWeight = FontWeight.SemiBold)
				}
			}
		}
	}
}


@Composable
fun SharePreviewDialog(
	bitmap: Bitmap?,
	onDismiss: () -> Unit,
	onShare: () -> Unit,
	onSave: (() -> Unit)? = null,
) {
	if (bitmap == null) return
	
	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(usePlatformDefaultWidth = false)
	) {
		Card(
			modifier = Modifier
				.fillMaxWidth(0.95f)
				.wrapContentHeight(),
			shape = RoundedCornerShape(20.dp),
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surface
			)
		) {
			Column(
				modifier = Modifier.padding(20.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = "Vista previa",
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.onSurface
				)
				
				Spacer(modifier = Modifier.height(16.dp))
				
				Card(
					modifier = Modifier
						.fillMaxWidth()
						.wrapContentHeight(),
					shape = RoundedCornerShape(12.dp),
					elevation = CardDefaults.cardElevation(4.dp)
				) {
					Image(
						bitmap = bitmap.asImageBitmap(),
						contentDescription = "Preview de captura",
						modifier = Modifier
							.fillMaxWidth()
							.wrapContentHeight()
					)
				}
				
				Spacer(modifier = Modifier.height(20.dp))
				
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(10.dp)
				) {
					if (onSave != null) {
						OutlinedButton(
							onClick = { onSave(); onDismiss() },
							modifier = Modifier.weight(1f),
							shape = RoundedCornerShape(12.dp)
						) {
							Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
							Spacer(modifier = Modifier.width(6.dp))
							Text("Guardar", fontWeight = FontWeight.SemiBold)
						}
					}
					
					Button(
						onClick = { onShare(); onDismiss() },
						modifier = Modifier.weight(1f),
						shape = RoundedCornerShape(12.dp),
						colors = ButtonDefaults.buttonColors(
							containerColor = MaterialTheme.colorScheme.primary
						)
					) {
						Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
						Spacer(modifier = Modifier.width(6.dp))
						Text("Compartir", fontWeight = FontWeight.SemiBold)
					}
				}
			}
		}
	}
}



fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Boolean {
	return try {
		val contentValues = ContentValues().apply {
			put(MediaStore.MediaColumns.DISPLAY_NAME, "Calculator_${System.currentTimeMillis()}.png")
			put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				put(
					MediaStore.MediaColumns.RELATIVE_PATH,
					Environment.DIRECTORY_PICTURES + "/DollarCalculator"
				)
				put(MediaStore.MediaColumns.IS_PENDING, 1)
			}
		}
		
		val uri = context.contentResolver.insert(
			MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			contentValues
		)
		
		uri?.let {
			context.contentResolver.openOutputStream(it)?.use { outputStream ->
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				contentValues.clear()
				contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
				context.contentResolver.update(uri, contentValues, null, null)
			}
		}
		true
	} catch (e: Exception) {
		e.printStackTrace()
		false
	}
}








