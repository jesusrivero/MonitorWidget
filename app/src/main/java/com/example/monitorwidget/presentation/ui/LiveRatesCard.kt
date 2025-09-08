package com.example.monitorwidget.presentation.ui


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.monitorwidget.domain.model.DollarRates
import java.text.DecimalFormat
import com.example.monitorwidget.presentation.ui.commons.DrawerScaffold
import com.example.monitorwidget.presentation.ui.commons.NavigationRoute
import com.example.monitorwidget.ui.theme.domain.model.viewmodel.DollarViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveRatesScreen(
	viewModel: DollarViewModel = hiltViewModel(),
	rates: DollarRates?,
	navController: NavController,
) {
	DrawerScaffold(
		currentRoute = NavigationRoute.LIVE_RATES,
		navController = navController
	) { padding ->
		Column(
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
				.padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			if (rates == null) {
				ErrorState(
					message = "No hay tasas disponibles",
					onRetry = { viewModel.fetchRates() }
				)
				return@Column
			}
			// Header
			Card(
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(16.dp),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer
				)
			) {
				Column(
					modifier = Modifier
						.padding(24.dp)
						.fillMaxWidth(),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = "📊",
						style = MaterialTheme.typography.displayMedium
					)
					Text(
						text = "Tasas en Tiempo Real",
						style = MaterialTheme.typography.headlineSmall,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.onPrimaryContainer
					)
					Text(
						text = "Última actualización: Ahora",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
					)
				}
			}
			
			Spacer(modifier = Modifier.height(24.dp))
			
			// Tasas actuales expandidas
			LiveRateCard(
				title = "Banco Central de Venezuela",
				subtitle = "BCV - Tasa Oficial",
				rate = rates.bcv,
				icon = "🏦",
				color = MaterialTheme.colorScheme.secondary
			)
		}
	}
}

@Composable
fun LiveRateCard(
	title: String,
	subtitle: String,
	rate: Double,
	icon: String,
	color: Color,
) {
	val formatter = DecimalFormat("#,##0.00")
	
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(16.dp),
		colors = CardDefaults.cardColors(
			containerColor = color.copy(alpha = 0.08f)
		),
		border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
		elevation = CardDefaults.cardElevation(4.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(20.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Box(
				modifier = Modifier
					.size(56.dp)
					.background(
						color.copy(alpha = 0.15f),
						CircleShape
					),
				contentAlignment = Alignment.Center
			) {
				Text(
					text = icon,
					style = MaterialTheme.typography.headlineMedium
				)
			}
			
			Spacer(modifier = Modifier.width(16.dp))
			
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = title,
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold,
					color = color
				)
				Text(
					text = subtitle,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
				)
			}
			
			Column(
				horizontalAlignment = Alignment.End
			) {
				Text(
					text = "${formatter.format(rate)}",
					style = MaterialTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.onSurface
				)
				Text(
					text = "Bs.",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
				)
			}
		}
	}
}