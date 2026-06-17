package com.tecsup.stockmanager.ui.list

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.tecsup.stockmanager.StockManagerApp
import com.tecsup.stockmanager.data.local.ProductEntity
import com.tecsup.stockmanager.ui.theme.StockCritico
import com.tecsup.stockmanager.ui.theme.StockOk
import com.tecsup.stockmanager.viewmodel.ProductListViewModel
import com.tecsup.stockmanager.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToForm: (Int?) -> Unit,
    onNavigateToStats: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as StockManagerApp
    val viewModel: ProductListViewModel = viewModel(
        factory = ViewModelFactory(app.repository)
    )

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("StockManager") },
                actions = {
                    IconButton(onClick = onNavigateToStats) {
                        Icon(Icons.Default.BarChart, contentDescription = "Estadísticas")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToForm(null) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Agregar") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.productos.isEmpty() -> {
                    Text(
                        text = "No hay productos registrados.\nPresiona + para agregar uno.",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        )
                    ) {
                        items(uiState.productos) { producto ->
                            ProductoCard(
                                producto = producto,
                                onClick = { onNavigateToDetail(producto.id) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductoCard(
    producto: ProductEntity,
    onClick: () -> Unit
) {
    val esStockCritico = producto.cantidad < producto.stockMinimo

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // Indicador visual de color a la izquierda
            Surface(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp),
                color = if (esStockCritico) StockCritico else StockOk
            ) {}

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = producto.categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "Stock: ${producto.cantidad}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (esStockCritico) StockCritico else MaterialTheme.colorScheme.onSurface
                    )
                    if (esStockCritico) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Stock crítico",
                            tint = StockCritico,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}