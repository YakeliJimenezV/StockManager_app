package com.tecsup.stockmanager

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.tecsup.stockmanager.ui.navigation.NavGraph
import com.tecsup.stockmanager.ui.theme.StockManagerTheme

class MainActivity : ComponentActivity() {

    private val solicitarPermisoNotificacion = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Solicitar permiso de notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                solicitarPermisoNotificacion.launch(
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }

        // Obtener token FCM para pruebas push
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM_TOKEN", "Token: ${task.result}")
            }
        }

        val productoIdDesdeNotificacion = intent?.getIntExtra("productoId", -1)
            ?.takeIf { it != -1 }

        setContent {
            StockManagerTheme {
                NavGraph(productoIdDesdeNotificacion = productoIdDesdeNotificacion)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val productoId = intent.getIntExtra("productoId", -1).takeIf { it != -1 }
        if (productoId != null) {
            setIntent(intent)
            recreate()
        }
    }
}