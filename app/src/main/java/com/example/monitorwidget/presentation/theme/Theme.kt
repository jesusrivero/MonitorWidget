package com.example.monitorwidget.presentation.theme


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Colores personalizados para app financiera
private val FinancialGreen = Color(0xFF10B981)      // Verde principal (éxito financiero)
private val FinancialGreenDark = Color(0xFF059669)  // Verde oscuro
private val FinancialBlue = Color(0xFF3B82F6)       // Azul BCV
private val FinancialBlueDark = Color(0xFF1E40AF)   // Azul oscuro
private val FinancialOrange = Color(0xFFF59E0B)     // Naranja (alertas)
private val FinancialRed = Color(0xFFEF4444)        // Rojo (errores)

// Grises modernos
private val NeutralGray50 = Color(0xFFFAFAFA)
private val NeutralGray100 = Color(0xFFF5F5F5)
private val NeutralGray200 = Color(0xFFE5E5E5)
private val NeutralGray800 = Color(0xFF262626)
private val NeutralGray900 = Color(0xFF171717)

private val DarkColorScheme = darkColorScheme(
	primary = FinancialGreen,
	onPrimary = Color.Black,
	primaryContainer = FinancialGreenDark,
	onPrimaryContainer = Color.White,
	
	secondary = FinancialBlue,
	onSecondary = Color.White,
	secondaryContainer = FinancialBlueDark,
	onSecondaryContainer = Color.White,
	
	tertiary = FinancialOrange,
	onTertiary = Color.Black,
	tertiaryContainer = Color(0xFFD97706),
	onTertiaryContainer = Color.White,
	
	error = FinancialRed,
	onError = Color.White,
	errorContainer = Color(0xFF7F1D1D),
	onErrorContainer = Color.White,
	
	background = NeutralGray900,
	onBackground = Color.White,
	surface = NeutralGray800,
	onSurface = Color.White,
	surfaceVariant = Color(0xFF374151),
	onSurfaceVariant = Color(0xFFE5E7EB),
	
	outline = Color(0xFF6B7280),
	outlineVariant = Color(0xFF4B5563)
)

private val LightColorScheme = lightColorScheme(
	primary = FinancialGreen,
	onPrimary = Color.White,
	primaryContainer = Color(0xFFDCFCE7),
	onPrimaryContainer = FinancialGreenDark,
	
	secondary = FinancialBlue,
	onSecondary = Color.White,
	secondaryContainer = Color(0xFFDBEAFE),
	onSecondaryContainer = FinancialBlueDark,
	
	tertiary = FinancialOrange,
	onTertiary = Color.White,
	tertiaryContainer = Color(0xFFFEF3C7),
	onTertiaryContainer = Color(0xFF92400E),
	
	error = FinancialRed,
	onError = Color.White,
	errorContainer = Color(0xFFFEE2E2),
	onErrorContainer = Color(0xFF7F1D1D),
	
	background = Color.White,
	onBackground = NeutralGray900,
	surface = NeutralGray50,
	onSurface = NeutralGray900,
	surfaceVariant = NeutralGray100,
	onSurfaceVariant = Color(0xFF6B7280),
	
	outline = NeutralGray200,
	outlineVariant = Color(0xFFD1D5DB)
)

@Composable
fun MonitorWidgetTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	dynamicColor: Boolean = false, // Deshabilitado para mantener consistencia
	content: @Composable () -> Unit
) {
	val colorScheme = when {
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			val context = LocalContext.current
			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
		}
		darkTheme -> DarkColorScheme
		else -> LightColorScheme
	}
	
	MaterialTheme(
		colorScheme = colorScheme,
		typography = FinancialTypography,
		content = content
	)
}

// Tipografía personalizada para app financiera
private val FinancialTypography = Typography(
	displayLarge = Typography().displayLarge.copy(
		fontWeight = FontWeight.Bold
	),
	headlineLarge = Typography().headlineLarge.copy(
		fontWeight = FontWeight.Bold
	),
	headlineMedium = Typography().headlineMedium.copy(
		fontWeight = FontWeight.SemiBold
	),
	titleLarge = Typography().titleLarge.copy(
		fontWeight = FontWeight.Bold
	),
	titleMedium = Typography().titleMedium.copy(
		fontWeight = FontWeight.SemiBold
	),
	bodyLarge = Typography().bodyLarge.copy(
		lineHeight = 24.sp
	)
)

// Extensiones de utilidad para colores específicos de la app
object AppColors {
	val SuccessGreen = FinancialGreen
	val BcvBlue = FinancialBlue
	val ParaleloGreen = FinancialGreenDark
	val WarningOrange = FinancialOrange
	val ErrorRed = FinancialRed
	
	@Composable
	fun surface(elevation: Int = 0): Color {
		return when (elevation) {
			0 -> MaterialTheme.colorScheme.surface
			1 -> MaterialTheme.colorScheme.surfaceVariant
			else -> MaterialTheme.colorScheme.surfaceVariant
		}
	}
}
