package com.tecsup.stockmanager.ui.detail

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
import com.tecsup.stockmanager.viewmodel.ProductDetailViewModel
import com.tecsup.stockmanager.viewmodel.ViewModelFactory
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productoId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as StockManagerApp
    val viewModel: ProductDetailViewModel = viewModel(factory = ViewModelFactory(app.repository))
    val uiState by viewModel.uiState.collectAsState()
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    LaunchedEffect(productoId) { viewModel.cargarProducto(productoId) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(CoralPrimary, CoralLight)))
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text("Detalle del Producto", color = WarmWhite, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                            Text("Información completa", color = WarmWhite.copy(alpha = 0.75f), fontSize = 11.sp)
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                uiState.isLoadingProducto -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = CoralPrimary)
                uiState.producto == null  -> Text("Producto no encontrado", modifier = Modifier.align(Alignment.Center), color = TextMutedWarm)
                else -> {
                    val producto = uiState.producto!!
                    val esCritico = producto.cantidad < producto.stockMinimo

                    Column(
                        modifier = Modifier.fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 20.dp)
                    ) {
                        // ── Hero ─────────────────────────────────────────────
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
                                    Surface(shape = RoundedCornerShape(20.dp), color = WarmWhite.copy(alpha = 0.20f)) {
                                        Text(
                                            producto.categoria,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                            color = WarmWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(Modifier.height(10.dp))
                                    Text(producto.nombre, color = WarmWhite, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                                    if (esCritico) {
                                        Spacer(Modifier.height(10.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Warning, null, tint = WarmWhite, modifier = Modifier.size(15.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Stock por debajo del mínimo", color = WarmWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // ── Stock ─────────────────────────────────────────────
                        Card(
                            modifier = Modifier.fillMaxWidth().shadow(5.dp, RoundedCornerShape(18.dp)),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = WarmWhite),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    Text("STOCK ACTUAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CoralPrimary, letterSpacing = 1.sp)
                                    Spacer(Modifier.weight(1f))
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (esCritico) StockCritico.copy(0.10f) else StockOk.copy(0.10f)
                                    ) {
                                        Text(
                                            "${producto.cantidad} unid.",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                            fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                                            color = if (esCritico) StockCritico else StockOk
                                        )
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(8.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (esCritico) StockCritico.copy(0.10f) else StockOk.copy(0.10f))
                                ) {
                                    LinearProgressIndicator(
                                        progress = {
                                            if (producto.stockMinimo == 0) 1f
                                            else (producto.cantidad.toFloat() / (producto.stockMinimo * 2).coerceAtLeast(1)).coerceIn(0f, 1f)
                                        },
                                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(6.dp)),
                                        color = if (esCritico) StockCritico else StockOk,
                                        trackColor = Color.Transparent,
                                        strokeCap = StrokeCap.Round
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text("Mínimo requerido: ${producto.stockMinimo} unid.", fontSize = 11.sp, color = TextMutedWarm)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // ── Precio ────────────────────────────────────────────
                        Card(
                            modifier = Modifier.fillMaxWidth().shadow(5.dp, RoundedCornerShape(18.dp)),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = WarmWhite),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text("PRECIO UNITARIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CoralPrimary, letterSpacing = 1.sp)
                                Spacer(Modifier.height(6.dp))
                                Text("S/ ${String.format(Locale.US, "%.2f", producto.precio)}", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                                Spacer(Modifier.height(10.dp))
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFFFE0D0)))
                                Spacer(Modifier.height(10.dp))
                                when {
                                    uiState.isLoadingDolar -> Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = CoralPrimary)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Consultando tipo de cambio...", fontSize = 12.sp, color = TextMutedWarm)
                                    }
                                    uiState.errorDolar != null -> Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ErrorOutline, null, tint = StockCritico, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(uiState.errorDolar ?: "", fontSize = 12.sp, color = StockCritico)
                                    }
                                    uiState.precioEnDolares != null -> Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(shape = RoundedCornerShape(6.dp), color = CoralPrimary.copy(alpha = 0.10f)) {
                                            Text("USD", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = CoralPrimary)
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Text("≈ \$ ${String.format(Locale.US, "%.2f", uiState.precioEnDolares)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CoralPrimary)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(28.dp))

                        // ── Botones ───────────────────────────────────────────
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { mostrarDialogoEliminar = true },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(28.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, StockCritico.copy(alpha = 0.6f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StockCritico)
                            ) {
                                Icon(Icons.Default.Delete, null, modifier = Modifier.size(17.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Eliminar", fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(12.dp))
                            Button(
                                onClick = { onNavigateToEdit(producto.id) },
                                modifier = Modifier.weight(1f).height(50.dp).shadow(6.dp, RoundedCornerShape(28.dp)),
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary, contentColor = WarmWhite)
                            ) {
                                Icon(Icons.Default.Edit, null, modifier = Modifier.size(17.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Editar", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = WarmWhite,
            title = { Text("¿Eliminar producto?", fontWeight = FontWeight.ExtraBold, color = TextDark) },
            text = { Text("Esta acción no se puede deshacer.", color = TextMutedWarm) },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false; viewModel.eliminar(onEliminado = onNavigateBack) }) {
                    Text("Eliminar", color = StockCritico, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar", color = CoralPrimary)
                }
            }
        )
    }
}