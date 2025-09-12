package com.example.monitorwidget.presentation.ui

import androidx.glance.action.actionStartActivity
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.domain.model.DollarRates
import com.example.monitorwidget.domain.RefreshAction
import com.example.monitorwidget.infraestructure.MainActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

import androidx.glance.appwidget.action.actionStartActivity

import androidx.glance.text.Text

import androidx.glance.LocalContext
import com.example.monitorwidget.presentation.navegacion.AppRoutes


class MonitorGlanceWidget : GlanceAppWidget() {
	override suspend fun provideGlance(context: Context, id: GlanceId) {
		val dataStore = DollarDataStore(context)
		val rates: DollarRates? = dataStore.getRates()
		
		provideContent {
			GlanceTheme {
				MonitorWidgetContent(rates)
			}
		}
	}
}


@SuppressLint("RestrictedApi")
@Composable
fun MonitorWidgetContent(rates: DollarRates?) {
	val formatter = DecimalFormat("#,##0.00")
	// ✅ Cambio aquí: formato de 12 horas con AM/PM
	val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
	
	Column(
		modifier = GlanceModifier
			.padding(8.dp)
			.fillMaxWidth()
			.background(ColorProvider(Color(0xFFEFEFEF)))
			.cornerRadius(12.dp)
			.padding(10.dp)
			.clickable(actionStartActivity<MainActivity>()), // 👈 Tocar fuera de botones abre la app normal
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		// Header
		Text(
			text = "💵 Dólar BCV",
			style = TextStyle(
				fontWeight = FontWeight.Bold,
				fontSize = 14.sp,
				color = ColorProvider(Color(0xFF333333))
			)
		)
		
		Spacer(GlanceModifier.height(12.dp))
		
		if (rates != null) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = "Bs. ${formatter.format(rates.bcv)}",
					style = TextStyle(
						fontSize = 18.sp,
						fontWeight = FontWeight.Bold,
						color = ColorProvider(Color(0xFF2563EB))
					)
				)
			}
			
			Spacer(GlanceModifier.height(10.dp))
			
			Text(
				text = "🕒 Actualizado: ${timeFormatter.format(Date(rates.timestamp * 1000))}",
				style = TextStyle(
					fontSize = 10.sp,
					color = ColorProvider(Color(0xFF888888))
				)
			)
		} else {
			Text(
				text = "⚠️ Sin conexión",
				style = TextStyle(
					fontWeight = FontWeight.Medium,
					fontSize = 12.sp,
					color = ColorProvider(Color(0xFFDC2626))
				)
			)
		}
		
		Spacer(GlanceModifier.height(10.dp))
		
		Row(
			modifier = GlanceModifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			// 🔄 Botón de actualizar
			Box(
				modifier = GlanceModifier
					.width(80.dp)
					.height(36.dp)
					.background(ColorProvider(Color(0xDD10B981))) // Verde
					.cornerRadius(18.dp)
					.clickable(actionRunCallback<RefreshAction>()),
				contentAlignment = Alignment.Center
			) {
				Row(
					modifier = GlanceModifier.padding(horizontal = 8.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						"🔄",
						style = TextStyle(fontSize = 14.sp)
					)
					Spacer(GlanceModifier.width(4.dp))
					Text(
						"Act",
						style = TextStyle(
							fontSize = 11.sp,
							color = ColorProvider(Color.White),
							fontWeight = FontWeight.Medium
						)
					)
				}
			}
			
			Spacer(GlanceModifier.width(8.dp))
			
			// ⭐ Botón de favoritos
			Box(
				modifier = GlanceModifier
					.width(80.dp)
					.height(36.dp)
					.background(ColorProvider(Color(0xCEEAB319))) // Amarillo/dorado
					.cornerRadius(18.dp)
					.clickable(
						actionStartActivity(
							Intent(LocalContext.current, MainActivity::class.java).apply {
								putExtra("navigateTo", "favorites")
								flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
							}
						)
					),
				contentAlignment = Alignment.Center
			) {
				Row(
					modifier = GlanceModifier.padding(horizontal = 8.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						"💡",
						style = TextStyle(fontSize = 14.sp)
					)
					Spacer(GlanceModifier.width(4.dp))
					Text(
						"Rec",
						style = TextStyle(
							fontSize = 11.sp,
							color = ColorProvider(Color.White),
							fontWeight = FontWeight.Medium
						)
					)
				}
			}
		}
	}
}
