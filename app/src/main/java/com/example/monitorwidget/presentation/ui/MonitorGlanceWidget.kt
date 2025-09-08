package com.example.monitorwidget.presentation.ui


import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
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
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

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
	val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
	
	Column(
		modifier = GlanceModifier
			.padding(8.dp)
			.fillMaxWidth()
			.background(ColorProvider(Color(0xFFEFEFEF)))
			.cornerRadius(12.dp)
			.padding(10.dp),
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
		
	
		Button(
			text = "🔄 Actualizar",
			onClick = actionRunCallback<RefreshAction>(),
			modifier = GlanceModifier.cornerRadius(6.dp)
		)
	}
}