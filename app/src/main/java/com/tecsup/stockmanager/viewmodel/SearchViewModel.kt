package com.tecsup.stockmanager.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.stockmanager.data.local.ProductEntity
import com.tecsup.stockmanager.data.repository.ProductRepository
import com.tecsup.stockmanager.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SearchUiState(
    val todos: List<ProductEntity> = emptyList(),
    val filtrados: List<ProductEntity> = emptyList(),
    val busqueda: String = "",
    val categoriaSeleccionada: String = "Todos",
    val isLoading: Boolean = true,
    val ventaExitosa: Boolean = false
)

class SearchViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            repository.obtenerTodos().collect { lista ->
                _uiState.value = _uiState.value.copy(
                    todos = lista,
                    isLoading = false
                )
                aplicarFiltros()
            }
        }
    }

    fun onBusquedaChange(texto: String) {
        _uiState.value = _uiState.value.copy(busqueda = texto)
        aplicarFiltros()
    }

    fun onCategoriaChange(categoria: String) {
        _uiState.value = _uiState.value.copy(categoriaSeleccionada = categoria)
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val state = _uiState.value
        val filtrados = state.todos.filter { producto ->
            val coincideBusqueda = producto.nombre.contains(state.busqueda, ignoreCase = true)
            val coincideCategoria = state.categoriaSeleccionada == "Todos" ||
                    producto.categoria == state.categoriaSeleccionada
            coincideBusqueda && coincideCategoria
        }
        _uiState.value = _uiState.value.copy(filtrados = filtrados)
    }

    fun registrarVenta(
        context: Context,
        producto: ProductEntity,
        cantidadVendida: Int
    ) {
        if (cantidadVendida <= 0) return

        viewModelScope.launch {
            val nuevoStock = (producto.cantidad - cantidadVendida).coerceAtLeast(0)
            val productoActualizado = producto.copy(cantidad = nuevoStock)
            repository.actualizar(productoActualizado)

            // Notificación si stock bajo después de la venta
            if (nuevoStock < producto.stockMinimo) {
                NotificationHelper.enviarAlertaStockBajo(
                    context = context,
                    productoId = producto.id,
                    nombreProducto = producto.nombre,
                    cantidadActual = nuevoStock,
                    stockMinimo = producto.stockMinimo
                )
            }

            _uiState.value = _uiState.value.copy(ventaExitosa = true)
        }
    }

    fun resetVentaExitosa() {
        _uiState.value = _uiState.value.copy(ventaExitosa = false)
    }
    fun registrarLlegada(
        context: Context,
        producto: ProductEntity,
        cantidadLlegada: Int
    ) {
        if (cantidadLlegada <= 0) return

        viewModelScope.launch {
            val nuevoStock = producto.cantidad + cantidadLlegada
            val productoActualizado = producto.copy(cantidad = nuevoStock)
            repository.actualizar(productoActualizado)
            _uiState.value = _uiState.value.copy(ventaExitosa = true)
        }
    }


}