package com.tecsup.stockmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tecsup.stockmanager.ui.navigation.NavGraph
import com.tecsup.stockmanager.ui.theme.StockManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StockManagerTheme {
                NavGraph()
            }
        }
    }
}