package com.tecsup.stockmanager.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.tecsup.stockmanager.MainActivity
import com.tecsup.stockmanager.R

object NotificationHelper {

    private const val CHANNEL_ID = "stock_critico_channel"
    private const val CHANNEL_NAME = "Alertas de Stock"
    private const val CHANNEL_DESC = "Notificaciones cuando un producto tiene stock bajo"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableLights(true)
                lightColor = Color.rgb(232, 87, 10) // naranja
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    fun enviarAlertaStockBajo(
        context: Context,
        productoId: Int,
        nombreProducto: String,
        cantidadActual: Int,
        stockMinimo: Int
    ) {
        val manager = context.getSystemService(NotificationManager::class.java)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("productoId", productoId)
            putExtra("navegarADetalle", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            productoId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Texto expandido con más detalle
        val textoExpandido = """
            📦 Producto: $nombreProducto
            📉 Stock actual: $cantidadActual unidades
            ⚠️ Stock mínimo: $stockMinimo unidades
            
            Toca para ver el detalle y reabastecer.
        """.trimIndent()

        val notificacion = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⚠️ Stock crítico: $nombreProducto")
            .setContentText("Solo quedan $cantidadActual/${stockMinimo} unidades mínimas")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(textoExpandido)
                    .setBigContentTitle("⚠️ Stock crítico: $nombreProducto")
                    .setSummaryText("StockManager · Alerta de inventario")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(android.graphics.Color.rgb(232, 87, 10))
            .setColorized(true)
            .setVibrate(longArrayOf(0, 250, 150, 250))
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Ver producto",
                pendingIntent
            )
            .build()

        manager.notify(productoId, notificacion)
    }
}