package com.example.monitorwidget.presentation.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun formatLiveInput(raw: String): String {
	if (raw.isEmpty()) return ""
	val hasDecimal = raw.contains('.')
	val parts = raw.split('.')
	val intPart = parts[0].toLongOrNull() ?: return raw
	val formattedInt = DecimalFormat("#,##0", DecimalFormatSymbols(Locale.US)).format(intPart)
	return if (hasDecimal) "$formattedInt.${parts.getOrElse(1) { "" }}" else formattedInt
}
