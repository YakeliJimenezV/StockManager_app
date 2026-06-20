package com.tecsup.stockmanager.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.stockmanager.StockManagerApp
import com.tecsup.stockmanager.ui.theme.CoralLight
import com.tecsup.stockmanager.ui.theme.CoralPrimary
import com.tecsup.stockmanager.ui.theme.StockCritico
import com.tecsup.stockmanager.ui.theme.StockOk
import com.tecsup.stockmanager.ui.theme.TextDark
import com.tecsup.stockmanager.ui.theme.TextMutedWarm
import com.tecsup.stockmanager.ui.theme.WarmBg
import com.tecsup.stockmanager.ui.theme.WarmWhite
import com.tecsup.stockmanager.viewmodel.StatsViewModel
import com.tecsup.stockmanager.viewmodel.ViewModelFactory
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as StockManagerApp
    val viewModel: StatsViewModel = viewModel(factory = ViewModelFactory(app.repository))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(CoralPrimary, CoralLight)))
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text("Estadísticas", color = WarmWhite, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                            Text("Resumen del inventario", color = WarmWhite.copy(alpha = 0.75f), fontSize = 11.sp)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = WarmWhite)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        containerColor = WarmBg
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CoralPrimary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Valor total ──────────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Brush.horizontalGradient(listOf(CoralPrimary, CoralLight)))
                            .padding(20.dp)
                    ) {
                        Column {
                            Text("VALOR DEL INVENTARIO", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = WarmWhite.copy(0.75f), letterSpacing = 1.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("S/ ${String.format(Locale.US, "%.2f", uiState.valorTotalSoles)}", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = WarmWhite)
                            Spacer(Modifier.height(12.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(WarmWhite.copy(0.2f)))
                            Spacer(Modifier.height(12.dp))
                            when {
                                uiState.isLoadingDolar -> Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = WarmWhite)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Consultando tipo de cambio...", fontSize = 12.sp, color = WarmWhite.copy(0.7f))
                                }
                                uiState.errorDolar != null -> Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ErrorOutline, null, tint = WarmWhite, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(uiState.errorDolar ?: "", fontSize = 12.sp, color = WarmWhite.copy(0.85f))
                                }
                                uiState.valorTotalDolares != null -> Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(shape = RoundedCornerShape(6.dp), color = WarmWhite.copy(0.20f)) {
                                        Text("USD", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = WarmWhite)
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text("≈ \$ ${String.format(Locale.US, "%.2f", uiState.valorTotalDolares)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WarmWhite.copy(0.9f))
                                }
                            }
                        }
                    }
                }
            }

            // ── Menor stock ──────────────────────────────────────────────────
            item {
                uiState.productoConMenosStock?.let { producto ->
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(5.dp, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmWhite),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(StockCritico.copy(0.10f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.TrendingDown, null, tint = StockCritico, modifier = Modifier.size(24.dp))
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text("MENOR STOCK", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = StockCritico.copy(0.7f), letterSpacing = 0.8.sp)
                                Spacer(Modifier.height(2.dp))
                                Text(producto.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = TextDark)
                                Text("${producto.cantidad} unidades disponibles", fontSize = 12.sp, color = StockCritico, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // ── Encabezado críticos ──────────────────────────────────────────
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("STOCK CRÍTICO", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = CoralPrimary, letterSpacing = 1.sp)
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (uiState.productosStockCritico.isEmpty()) StockOk.copy(0.12f) else StockCritico.copy(0.10f)
                    ) {
                        Text(
                            "${uiState.productosStockCritico.size}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                            color = if (uiState.productosStockCritico.isEmpty()) StockOk else StockCritico
                        )
                    }
                }
            }

            // ── Sin críticos ─────────────────────────────────────────────────
            if (uiState.productosStockCritico.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmWhite),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = StockOk, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("No hay productos en estado crítico", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = StockOk)
                        }
                    }
                }
            } else {
                items(uiState.productosStockCritico) { producto ->
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmWhite),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.width(4.dp).height(46.dp).clip(RoundedCornerShape(4.dp)).background(StockCritico))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(producto.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = TextDark)
                                Spacer(Modifier.height(2.dp))
                                Text("Stock: ${producto.cantidad}  ·  Mínimo: ${producto.stockMinimo}", fontSize = 11.sp, color = StockCritico, fontWeight = FontWeight.SemiBold)
                            }
                            Icon(Icons.Default.Warning, null, tint = StockCritico, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}