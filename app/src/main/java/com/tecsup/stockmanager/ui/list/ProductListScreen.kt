package com.tecsup.stockmanager.ui.list

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.stockmanager.StockManagerApp
import com.tecsup.stockmanager.data.local.ProductEntity
import com.tecsup.stockmanager.ui.theme.CoralLight
import com.tecsup.stockmanager.ui.theme.CoralPrimary
import com.tecsup.stockmanager.ui.theme.StockCritico
import com.tecsup.stockmanager.ui.theme.StockOk
import com.tecsup.stockmanager.ui.theme.TextDark
import com.tecsup.stockmanager.ui.theme.TextMutedWarm
import com.tecsup.stockmanager.ui.theme.WarmBg
import com.tecsup.stockmanager.ui.theme.WarmWhite
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
    val viewModel: ProductListViewModel = viewModel(factory = ViewModelFactory(app.repository))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(CoralPrimary, CoralLight)))
            ) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = WarmWhite,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("StockManager", color = WarmWhite, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                                Text("Control de inventario", color = WarmWhite.copy(alpha = 0.75f), fontSize = 11.sp)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToStats) {
                            Icon(Icons.Default.BarChart, contentDescription = "Estadísticas", tint = WarmWhite)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToForm(null) },
                containerColor = CoralPrimary,
                contentColor = WarmWhite,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(28.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Agregar", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                }
            }
        },
        containerColor = WarmBg
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = CoralPrimary
                )
                uiState.productos.isEmpty() -> Column(
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Inventory2, null, tint = CoralPrimary.copy(alpha = 0.3f), modifier = Modifier.size(72.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Sin productos aún", fontWeight = FontWeight.ExtraBold, color = TextDark, fontSize = 17.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Presiona Agregar para registrar\ntu primer producto.", textAlign = TextAlign.Center, color = TextMutedWarm, fontSize = 13.sp)
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    items(uiState.productos) { producto ->
                        ProductoCard(producto = producto, onClick = { onNavigateToDetail(producto.id) })
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductoCard(producto: ProductEntity, onClick: () -> Unit) {
    val esCritico = producto.cantidad < producto.stockMinimo

    Card(
        modifier = Modifier.fillMaxWidth().shadow(6.dp, RoundedCornerShape(18.dp)),
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WarmWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.width(4.dp).height(52.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (esCritico) StockCritico else StockOk)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.ExtraBold, color = TextDark, fontSize = 14.sp)
                Spacer(Modifier.height(2.dp))
                Text(producto.categoria, color = CoralPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (esCritico) StockCritico.copy(alpha = 0.10f) else StockOk.copy(alpha = 0.10f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (esCritico) {
                            Icon(Icons.Default.Warning, null, tint = StockCritico, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                        }
                        Text(
                            "Stock: ${producto.cantidad}",
                            fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = if (esCritico) StockCritico else StockOk
                        )
                    }
                }
            }
        }
    }
}