package com.tecsup.stockmanager.ui.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.stockmanager.StockManagerApp
import com.tecsup.stockmanager.ui.theme.CoralLight
import com.tecsup.stockmanager.ui.theme.CoralPrimary
import com.tecsup.stockmanager.ui.theme.StockCritico
import com.tecsup.stockmanager.ui.theme.TextDark
import com.tecsup.stockmanager.ui.theme.TextMutedWarm
import com.tecsup.stockmanager.ui.theme.WarmBg
import com.tecsup.stockmanager.ui.theme.WarmWhite
import com.tecsup.stockmanager.viewmodel.CATEGORIAS
import com.tecsup.stockmanager.viewmodel.ProductFormViewModel
import com.tecsup.stockmanager.viewmodel.ViewModelFactory

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = CoralPrimary,
    unfocusedBorderColor    = Color(0xFFFFD4C2),
    focusedLabelColor       = CoralPrimary,
    unfocusedLabelColor     = TextMutedWarm,
    focusedTextColor        = TextDark,
    unfocusedTextColor      = TextDark,
    cursorColor             = CoralPrimary,
    errorBorderColor        = StockCritico,
    errorLabelColor         = StockCritico,
    unfocusedContainerColor = WarmWhite,
    focusedContainerColor   = WarmWhite
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productoId: Int? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as StockManagerApp
    val viewModel: ProductFormViewModel = viewModel(factory = ViewModelFactory(app.repository))
    val uiState by viewModel.uiState.collectAsState()
    val esEdicion = productoId != null

    LaunchedEffect(productoId) { if (productoId != null) viewModel.cargarProducto(productoId) }
    LaunchedEffect(uiState.guardadoExitoso) { if (uiState.guardadoExitoso) onNavigateBack() }

    var expandedCategoria by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(CoralPrimary, CoralLight)))
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                if (esEdicion) "Editar Producto" else "Nuevo Producto",
                                color = WarmWhite, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp
                            )
                            Text(
                                if (esEdicion) "Modifica los datos" else "Completa los campos",
                                color = WarmWhite.copy(alpha = 0.75f), fontSize = 11.sp
                            )
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
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SectionLabel("Identificación")
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre del producto") },
                isError = uiState.nombreError != null,
                supportingText = { uiState.nombreError?.let { Text(it, color = StockCritico) } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors()
            )

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = expandedCategoria, onExpandedChange = { expandedCategoria = it }) {
                OutlinedTextField(
                    value = uiState.categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = CoralPrimary) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false },
                    containerColor = WarmWhite
                ) {
                    CATEGORIAS.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat, color = TextDark, fontWeight = FontWeight.Medium) },
                            onClick = { viewModel.onCategoriaChange(cat); expandedCategoria = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            SectionLabel("Precio y stock")
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.precio,
                onValueChange = viewModel::onPrecioChange,
                label = { Text("Precio (S/)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = uiState.precioError != null,
                supportingText = { uiState.precioError?.let { Text(it, color = StockCritico) } },
                prefix = { Text("S/ ", color = CoralPrimary, fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.cantidad,
                onValueChange = viewModel::onCantidadChange,
                label = { Text("Cantidad disponible") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = uiState.cantidadError != null,
                supportingText = { uiState.cantidadError?.let { Text(it, color = StockCritico) } },
                suffix = { Text(" unid.", color = TextMutedWarm) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.stockMinimo,
                onValueChange = viewModel::onStockMinimoChange,
                label = { Text("Stock mínimo (alerta)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = uiState.stockMinimoError != null,
                supportingText = { uiState.stockMinimoError?.let { Text(it, color = StockCritico) } },
                suffix = { Text(" unid.", color = TextMutedWarm) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors()
            )

            Spacer(Modifier.height(36.dp))

            Button(
                onClick = { viewModel.guardar(productoId) },
                modifier = Modifier.fillMaxWidth().height(54.dp).shadow(8.dp, RoundedCornerShape(28.dp)),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CoralPrimary,
                    contentColor = WarmWhite,
                    disabledContainerColor = CoralPrimary.copy(alpha = 0.4f)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp, color = WarmWhite)
                } else {
                    Text(
                        if (esEdicion) "Guardar Cambios" else "Crear Producto",
                        fontSize = 15.sp, fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = CoralPrimary, letterSpacing = 1.sp)
}