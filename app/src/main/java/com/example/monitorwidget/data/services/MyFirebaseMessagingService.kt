package com.example.monitorwidget.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.monitorwidget.R
import com.example.monitorwidget.infraestructure.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
	
	override fun onMessageReceived(remoteMessage: RemoteMessage) {
		super.onMessageReceived(remoteMessage)
		
		// 🔥 Aquí manejas el mensaje entrante
		remoteMessage.notification?.let {
			showNotification(it.title, it.body)
		}
	}
	
	override fun onNewToken(token: String) {
		super.onNewToken(token)
		// 👉 Aquí puedes enviar el token a tu backend (para asociarlo al usuario)
	}
	
	private fun showNotification(title: String?, message: String?) {
		val channelId = "monitor_channel"
		val notificationManager =
			getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		
		// Crear canal de notificación (Android 8+)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				channelId,
				"MonitorWidget Notifications",
				NotificationManager.IMPORTANCE_HIGH
			)
			notificationManager.createNotificationChannel(channel)
		}
		
		// Intent al abrir la notificación
		val intent = Intent(this, MainActivity::class.java).apply {
			addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		}
		val pendingIntent = PendingIntent.getActivity(
			this, 0, intent,
			PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
		)
		
		val notificationBuilder = NotificationCompat.Builder(this, channelId)
			.setContentTitle(title ?: "MonitorWidget")
			.setContentText(message ?: "")
			.setSmallIcon(R.drawable.ic_launcher_foreground) // 👈 pon tu icono
			.setAutoCancel(true)
			.setContentIntent(pendingIntent)
		
		notificationManager.notify(0, notificationBuilder.build())
	}
}