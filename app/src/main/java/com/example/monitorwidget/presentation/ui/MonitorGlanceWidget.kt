package com.example.monitorwidget.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.domain.RefreshAction
import com.example.monitorwidget.domain.model.DollarRates
import com.example.monitorwidget.infraestructure.MainActivity
import com.example.monitorwidget.presentation.theme.MonitorWidgetTheme
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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
	val formatter      = DecimalFormat("#,##0.00")
	val timeFormatter  = SimpleDateFormat("h:mm a", Locale.getDefault())
	
	Column(
		modifier = GlanceModifier
			.padding(8.dp)
			.fillMaxWidth()
			.background(ColorProvider(Color(0xFFEFEFEF)))
			.cornerRadius(12.dp)
			.padding(10.dp)
			.clickable(actionStartActivity<MainActivity>()),
		horizontalAlignment = Alignment.CenterHorizontally
	) {

		Text(
			text  = "Monitor BCV",
			style = TextStyle(
				fontWeight = FontWeight.Bold,
				fontSize   = 14.sp,
				color      = ColorProvider(Color(0xFF333333))
			)
		)
		
		Spacer(GlanceModifier.height(10.dp))
		
		if (rates != null) {
			Row(
				modifier = GlanceModifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Column(
					modifier            = GlanceModifier.defaultWeight(),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text  = "💵 USD",
						style = TextStyle(
							fontSize = 10.sp,
							color    = ColorProvider(Color(0xFF888888))
						)
					)
					Spacer(GlanceModifier.height(2.dp))
					Text(
						text  = "Bs. ${formatter.format(rates.bcv)}",
						style = TextStyle(
							fontSize   = 15.sp,
							fontWeight = FontWeight.Bold,
							color      = ColorProvider(Color(0xFF2563EB))
						)
					)
				}
				
				Spacer(
					modifier = GlanceModifier
						.width(1.dp)
						.height(36.dp)
						.background(ColorProvider(Color(0xFFCCCCCC)))
				)
				
				Column(
					modifier            = GlanceModifier.defaultWeight(),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text  = "💶 EUR",
						style = TextStyle(
							fontSize = 10.sp,
							color    = ColorProvider(Color(0xFF888888))
						)
					)
					Spacer(GlanceModifier.height(2.dp))
					Text(
						text  = if (rates.eur > 0.0) "Bs. ${formatter.format(rates.eur)}"
						else     "N/D",
						style = TextStyle(
							fontSize   = 15.sp,
							fontWeight = FontWeight.Bold,
							color      = ColorProvider(Color(0xFF2563EB))
						)
					)
				}
			}
			
			Spacer(GlanceModifier.height(8.dp))
			
			Text(
				text  = "🕒 ${timeFormatter.format(Date(rates.timestamp * 1000))}",
				style = TextStyle(
					fontSize = 10.sp,
					color    = ColorProvider(Color(0xFF888888))
				)
			)
			
		} else {
			Text(
				text  = "⚠️ Sin conexión",
				style = TextStyle(
					fontWeight = FontWeight.Medium,
					fontSize   = 12.sp,
					color      = ColorProvider(Color(0xFFDC2626))
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
			Box(
				modifier = GlanceModifier
					.width(100.dp)
					.height(36.dp)
					.background(ColorProvider(Color(0xDD10B981)))
					.cornerRadius(18.dp)
					.clickable(actionRunCallback<RefreshAction>()),
				contentAlignment = Alignment.Center
			) {
				Row(
					modifier = GlanceModifier.padding(horizontal = 8.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalAlignment   = Alignment.CenterVertically
				) {
					Text("🔄", style = TextStyle(fontSize = 14.sp))
					Spacer(GlanceModifier.width(4.dp))
					Text(
						"Actualizar",
						style = TextStyle(
							fontSize   = 11.sp,
							color      = ColorProvider(Color.White),
							fontWeight = FontWeight.Medium
						)
					)
				}
			}
			
			Spacer(GlanceModifier.width(8.dp))
			
			Box(
				modifier = GlanceModifier
					.width(100.dp)
					.height(36.dp)
					.background(ColorProvider(Color(0xCEEAB319)))
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
					verticalAlignment   = Alignment.CenterVertically
				) {
					Text("💡", style = TextStyle(fontSize = 14.sp))
					Spacer(GlanceModifier.width(4.dp))
					Text(
						"Recurrentes",
						style = TextStyle(
							fontSize   = 11.sp,
							color      = ColorProvider(Color.White),
							fontWeight = FontWeight.Medium
						)
					)
				}
			}
		}
	}
}

