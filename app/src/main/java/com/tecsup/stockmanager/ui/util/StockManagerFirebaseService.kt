package com.tecsup.stockmanager.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tecsup.stockmanager.R

class StockManagerFirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val titulo = message.notification?.title ?: message.data["title"] ?: "StockManager"
        val cuerpo = message.notification?.body ?: message.data["body"] ?: ""
        mostrarNotificacionPush(titulo, cuerpo)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun mostrarNotificacionPush(titulo: String, cuerpo: String) {
        val channelId = "fcm_channel"
        val manager = getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                channelId,
                "Notificaciones Push",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                lightColor = android.graphics.Color.rgb(232, 87, 10)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
            }
            manager.createNotificationChannel(canal)
        }

        val notificacion = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(cuerpo)
                    .setSummaryText("StockManager")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setColor(android.graphics.Color.rgb(232, 87, 10))
            .setColorized(true)
            .setVibrate(longArrayOf(0, 250, 150, 250))
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notificacion)
    }
}