package com.example.monitorwidget.presentation.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap

fun shareBitmap(context: Context, bitmap: Bitmap) {
	val uri = saveBitmapToCache(context, bitmap)
	val intent = Intent(Intent.ACTION_SEND).apply {
		type = "image/png"
		putExtra(Intent.EXTRA_STREAM, uri)
		addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
	}
	context.startActivity(Intent.createChooser(intent, "Compartir captura"))
}
