package com.tecsup.stockmanager.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.stockmanager.StockManagerApp
import com.tecsup.stockmanager.data.local.ProductEntity
import com.tecsup.stockmanager.ui.theme.StockCritico
import com.tecsup.stockmanager.ui.theme.StockOk
import com.tecsup.stockmanager.viewmodel.SearchViewModel
import com.tecsup.stockmanager.viewmodel.ViewModelFactory

val CATEGORIAS_FILTRO = listOf("Todos", "Abarrotes", "Bebidas", "Limpieza", "Otros")

enum class ModoOperacion { VENTA, LLEGADA }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as StockManagerApp
    val viewModel: SearchViewModel = viewModel(
        factory = ViewModelFactory(app.repository)
    )

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var productoSeleccionado by remember { mutableStateOf<ProductEntity?>(null) }
    var modoOperacion by remember { mutableStateOf(ModoOperacion.VENTA) }

    LaunchedEffect(uiState.ventaExitosa) {
        if (uiState.ventaExitosa) {
            val msg = if (modoOperacion == ModoOperacion.VENTA)
                "🛒 Venta registrada correctamente"
            else "📦 Llegada de productos registrada"
            snackbarHostState.showSnackbar(msg)
            viewModel.resetVentaExitosa()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Buscar Productos",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${uiState.filtrados.size} resultados",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = uiState.busqueda,
                onValueChange = viewModel::onBusquedaChange,
                placeholder = { Text("Buscar producto...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, null,
                        tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    if (uiState.busqueda.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onBusquedaChange("") }) {
                            Icon(Icons.Default.Close, "Limpiar")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                singleLine = true
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CATEGORIAS_FILTRO) { categoria ->
                    FilterChip(
                        selected = uiState.categoriaSeleccionada == categoria,
                        onClick = { viewModel.onCategoriaChange(categoria) },
                        label = {
                            Text(
                                text = categoria,
                                fontWeight = if (uiState.categoriaSeleccionada == categoria)
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (uiState.categoriaSeleccionada == categoria) {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                uiState.filtrados.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "No se encontraron productos",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Intenta con otro nombre o categoría",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.filtrados) { producto ->
                            ProductoSearchCard(
                                producto = producto,
                                onRegistrarVenta = {
                                    productoSeleccionado = it
                                    modoOperacion = ModoOperacion.VENTA
                                },
                                onRegistrarLlegada = {
                                    productoSeleccionado = it
                                    modoOperacion = ModoOperacion.LLEGADA
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }

    productoSeleccionado?.let { producto ->
        DialogoOperacion(
            producto = producto,
            modo = modoOperacion,
            onConfirmar = { cantidad ->
                if (modoOperacion == ModoOperacion.VENTA)
                    viewModel.registrarVenta(context, producto, cantidad)
                else
                    viewModel.registrarLlegada(context, producto, cantidad)
                productoSeleccionado = null
            },
            onDismiss = { productoSeleccionado = null }
        )
    }
}

@Composable
private fun ProductoSearchCard(
    producto: ProductEntity,
    onRegistrarVenta: (ProductEntity) -> Unit,
    onRegistrarLlegada: (ProductEntity) -> Unit
) {
    val esStockCritico = producto.cantidad < producto.stockMinimo

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp).height(60.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (esStockCritico) StockCritico else StockOk)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = producto.categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Stock: ${producto.cantidad}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (esStockCritico) StockCritico
                        else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (esStockCritico) FontWeight.Bold
                        else FontWeight.Normal
                    )
                    if (esStockCritico) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Warning, null,
                            tint = StockCritico, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("·", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "S/ ${String.format(java.util.Locale.US, "%.2f", producto.precio)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                        .background(StockCritico.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { onRegistrarVenta(producto) },
                        modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.ShoppingCart, "Venta",
                            tint = StockCritico, modifier = Modifier.size(18.dp))
                    }
                }
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                        .background(StockOk.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { onRegistrarLlegada(producto) },
                        modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Inventory, "Llegada",
                            tint = StockOk, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogoOperacion(
    producto: ProductEntity,
    modo: ModoOperacion,
    onConfirmar: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var cantidadTexto by remember { mutableStateOf("") }
    val cantidad = cantidadTexto.toIntOrNull() ?: 0
    val esVenta = modo == ModoOperacion.VENTA
    val nuevoStock = if (esVenta)
        (producto.cantidad - cantidad).coerceAtLeast(0)
    else producto.cantidad + cantidad

    val quedaStockCritico = nuevoStock < producto.stockMinimo
    val colorModo = if (esVenta) StockCritico else StockOk
    val iconoModo = if (esVenta) Icons.Default.ShoppingCart else Icons.Default.Inventory
    val tituloModo = if (esVenta) "Registrar venta" else "Registrar llegada"
    val textoBoton = if (esVenta) "Confirmar venta" else "Confirmar llegada"
    val labelCampo = if (esVenta)
        "¿Cuántas unidades se vendieron?"
    else "¿Cuántas unidades llegaron?"

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape)
                    .background(colorModo.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconoModo, null, tint = colorModo, modifier = Modifier.size(30.dp))
            }
        },
        title = {
            Text(tituloModo, fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorModo.copy(alpha = 0.06f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            producto.nombre.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = colorModo
                        )
                        Text(producto.categoria,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${producto.cantidad}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold)
                                Text("Stock actual",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(if (esVenta) "→" else "+",
                                style = MaterialTheme.typography.headlineSmall,
                                color = colorModo, fontWeight = FontWeight.Bold)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (cantidad > 0) "$nuevoStock" else "?",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (cantidad > 0) colorModo
                                    else MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Nuevo stock",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = cantidadTexto,
                    onValueChange = { cantidadTexto = it },
                    label = { Text(labelCampo) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorModo,
                        focusedLabelColor = colorModo
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (cantidad > 0 && esVenta && quedaStockCritico) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = StockCritico.copy(alpha = 0.1f))
                    ) {
                        Row(modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null,
                                tint = StockCritico, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "⚠️ El stock quedará por debajo del mínimo (${producto.stockMinimo} unid.).",
                                style = MaterialTheme.typography.bodySmall,
                                color = StockCritico, fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                if (cantidad > 0 && !esVenta) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = StockOk.copy(alpha = 0.1f))
                    ) {
                        Row(modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, null,
                                tint = StockOk, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("✅ El stock quedará en $nuevoStock unidades.",
                                style = MaterialTheme.typography.bodySmall,
                                color = StockOk, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (cantidad > 0) onConfirmar(cantidad) },
                enabled = cantidad > 0 && (if (esVenta) cantidad <= producto.cantidad else true),
                colors = ButtonDefaults.buttonColors(containerColor = colorModo),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(iconoModo, null, modifier = Modifier.size(16.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(textoBoton, color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}