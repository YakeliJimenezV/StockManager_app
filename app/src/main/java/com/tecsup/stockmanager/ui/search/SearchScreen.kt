package com.tecsup.stockmanager.ui.search

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    val viewModel: SearchViewModel = viewModel(factory = ViewModelFactory(app.repository))

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var productoSeleccionado by remember { mutableStateOf<ProductEntity?>(null) }
    var modoOperacion by remember { mutableStateOf(ModoOperacion.VENTA) }

    LaunchedEffect(uiState.ventaExitosa) {
        if (uiState.ventaExitosa) {
            val msg = if (modoOperacion == ModoOperacion.VENTA)
                "🛒 Venta registrada correctamente"
            else
                "📦 Llegada de productos registrada"
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
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${uiState.filtrados.size} resultados",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f)
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.80f)
                        )
                    )
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
            Spacer(modifier = Modifier.height(10.dp))

            // ── Barra de búsqueda estilo card elevada ──────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                OutlinedTextField(
                    value = uiState.busqueda,
                    onValueChange = viewModel::onBusquedaChange,
                    placeholder = {
                        Text(
                            "Buscar por nombre o categoría...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (uiState.busqueda.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onBusquedaChange("") }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Limpiar",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Filtros de categoría ───────────────────────────────────────
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
                                    FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = if (uiState.categoriaSeleccionada == categoria) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Lista de resultados ────────────────────────────────────────
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                uiState.filtrados.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 56.sp)
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No se encontraron productos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Intenta con otro nombre o categoría",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
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
                        item { Spacer(Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }

    // ── Diálogo de operación ───────────────────────────────────────────────
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

// ─────────────────────────────────────────────────────────────────────────────
// Card de producto
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductoSearchCard(
    producto: ProductEntity,
    onRegistrarVenta: (ProductEntity) -> Unit,
    onRegistrarLlegada: (ProductEntity) -> Unit
) {
    val esStockCritico = producto.cantidad < producto.stockMinimo
    val stockColor = if (esStockCritico) StockCritico else StockOk

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Barra lateral con gradiente
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(82.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(stockColor, stockColor.copy(alpha = 0.35f))
                        )
                    )
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Info del producto
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 14.dp)
            ) {
                Text(
                    text = producto.nombre.replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = producto.categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Badge stock
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = stockColor.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            if (esStockCritico) {
                                Icon(
                                    Icons.Default.Warning, null,
                                    tint = stockColor,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                            Text(
                                text = "Stock: ${producto.cantidad}",
                                style = MaterialTheme.typography.labelSmall,
                                color = stockColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Badge precio
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                    ) {
                        Text(
                            text = "S/ ${String.format(java.util.Locale.US, "%.2f", producto.precio)}",
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Botones de acción
            Column(
                modifier = Modifier.padding(end = 12.dp, top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    onClick = { onRegistrarVenta(producto) },
                    modifier = Modifier.size(38.dp),
                    shape = CircleShape,
                    color = StockCritico.copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Registrar venta",
                            tint = StockCritico,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Surface(
                    onClick = { onRegistrarLlegada(producto) },
                    modifier = Modifier.size(38.dp),
                    shape = CircleShape,
                    color = StockOk.copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Inventory,
                            contentDescription = "Registrar llegada",
                            tint = StockOk,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Diálogo de operación rediseñado
// ─────────────────────────────────────────────────────────────────────────────

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
    val nuevoStock = if (esVenta) (producto.cantidad - cantidad).coerceAtLeast(0)
    else producto.cantidad + cantidad
    val quedaStockCritico = nuevoStock < producto.stockMinimo
    val colorModo   = if (esVenta) StockCritico else StockOk
    val iconoModo   = if (esVenta) Icons.Default.ShoppingCart else Icons.Default.Inventory
    val tituloModo  = if (esVenta) "Registrar venta"     else "Registrar llegada"
    val textoBoton  = if (esVenta) "Confirmar venta"     else "Confirmar llegada"
    val labelCampo  = if (esVenta) "¿Cuántas unidades se vendieron?" else "¿Cuántas unidades llegaron?"
    val puedeConfirmar = cantidad > 0 && (if (esVenta) cantidad <= producto.cantidad else true)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                // ── Header con gradiente + icono doble círculo ─────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(colorModo.copy(alpha = 0.18f), Color.Transparent)
                            ),
                            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                        )
                        .padding(top = 28.dp, bottom = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Doble círculo concéntrico
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(colorModo.copy(alpha = 0.13f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(colorModo.copy(alpha = 0.22f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    iconoModo,
                                    contentDescription = null,
                                    tint = colorModo,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = tituloModo,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // ── Contenido ─────────────────────────────────────────────
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // Nombre y categoría
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = producto.nombre.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorModo,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = producto.categoria,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Visualizador stock actual → nuevo
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 18.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${producto.cantidad}",
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Stock actual",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Flecha / operador dentro de círculo
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(colorModo.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (esVenta) "→" else "+",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = colorModo
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (cantidad > 0) "$nuevoStock" else "?",
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (cantidad > 0) colorModo
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                                )
                                Text(
                                    text = "Nuevo stock",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Campo de cantidad
                    OutlinedTextField(
                        value = cantidadTexto,
                        onValueChange = { cantidadTexto = it },
                        label = { Text(labelCampo) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorModo,
                            focusedLabelColor = colorModo,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Alerta stock bajo (solo en ventas)
                    if (cantidad > 0 && esVenta && quedaStockCritico) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StockCritico.copy(alpha = 0.08f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Warning, null,
                                    tint = StockCritico,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "⚠️ El stock quedará por debajo del mínimo (${producto.stockMinimo} unid.).",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StockCritico,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Confirmación llegada
                    if (cantidad > 0 && !esVenta) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StockOk.copy(alpha = 0.08f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Check, null,
                                    tint = StockOk,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "✅ El stock quedará en $nuevoStock unidades.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StockOk,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // ── Botones ───────────────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                            )
                        ) {
                            Text("Cancelar", fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = { if (puedeConfirmar) onConfirmar(cantidad) },
                            enabled = puedeConfirmar,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorModo)
                        ) {
                            Icon(
                                iconoModo,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                textoBoton,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}