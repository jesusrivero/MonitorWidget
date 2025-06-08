package com.example.monitorwidget.presentation.monitor_widget


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
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.ui.theme.domain.model.DollarRates
import com.example.monitorwidget.ui.theme.domain.model.RefreshAction
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
            .padding(12.dp)
            .fillMaxWidth()
            .background(ColorProvider(Color(0x6BFFFFFF))),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Monitor $",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )

        Spacer(GlanceModifier.height(8.dp))

        if (rates != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Paralelo: ${formatter.format(rates.promedio)} Bs")
                Text("BCV: ${formatter.format(rates.bcv)} Bs")
                Text("USDT: ${formatter.format(rates.usdt)} Bs")

                Text(
                    text = "Act: ${timeFormatter.format(Date(rates.timestamp * 100))}",
                    style = TextStyle(fontSize = 12.sp)
                )
            }
        } else {
            Text("Sin conexión", style = TextStyle(fontWeight = FontWeight.Medium))
        }

        Spacer(GlanceModifier.height(12.dp))

        Button(
            text = "Recargar",
            onClick = actionRunCallback<RefreshAction>()
        )
    }
}