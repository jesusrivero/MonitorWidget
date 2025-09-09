package com.example.monitorwidget.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
	val cachePath = File(context.cacheDir, "images")
	cachePath.mkdirs()
	val file = File(cachePath, "screenshot.png")
	FileOutputStream(file).use { out ->
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
	}
	return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}
