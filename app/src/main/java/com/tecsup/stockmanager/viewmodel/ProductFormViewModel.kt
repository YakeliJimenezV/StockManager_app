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
import kotlinx.coroutines.launch

data class ProductFormUiState(
    val nombre: String = "",
    val categoria: String = "Abarrotes",
    val precio: String = "",
    val cantidad: String = "",
    val stockMinimo: String = "",
    val nombreError: String? = null,
    val precioError: String? = null,
    val cantidadError: String? = null,
    val stockMinimoError: String? = null,
    val isLoading: Boolean = false,
    val guardadoExitoso: Boolean = false
)

val CATEGORIAS = listOf("Abarrotes", "Bebidas", "Limpieza", "Otros")

class ProductFormViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductFormUiState())
    val uiState: StateFlow<ProductFormUiState> = _uiState.asStateFlow()

    fun cargarProducto(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val producto = repository.obtenerPorId(id)
            producto?.let {
                _uiState.value = _uiState.value.copy(
                    nombre = it.nombre,
                    categoria = it.categoria,
                    precio = it.precio.toString(),
                    cantidad = it.cantidad.toString(),
                    stockMinimo = it.stockMinimo.toString(),
                    isLoading = false
                )
            }
        }
    }

    fun onNombreChange(value: String) {
        _uiState.value = _uiState.value.copy(nombre = value, nombreError = null)
    }

    fun onCategoriaChange(value: String) {
        _uiState.value = _uiState.value.copy(categoria = value)
    }

    fun onPrecioChange(value: String) {
        _uiState.value = _uiState.value.copy(precio = value, precioError = null)
    }

    fun onCantidadChange(value: String) {
        _uiState.value = _uiState.value.copy(cantidad = value, cantidadError = null)
    }

    fun onStockMinimoChange(value: String) {
        _uiState.value = _uiState.value.copy(stockMinimo = value, stockMinimoError = null)
    }

    fun guardar(context: Context, productoId: Int? = null) {
        if (!validar()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val state = _uiState.value
            val cantidad = state.cantidad.toInt()
            val stockMinimo = state.stockMinimo.toInt()

            val producto = ProductEntity(
                id = productoId ?: 0,
                nombre = state.nombre.trim(),
                categoria = state.categoria,
                precio = state.precio.toDouble(),
                cantidad = cantidad,
                stockMinimo = stockMinimo,
                usuarioId = "local"
            )

            val idParaNotificacion: Int
            if (productoId == null) {
                val idGenerado = repository.insertarYObtenerID(producto)
                idParaNotificacion = idGenerado.toInt()
            } else {
                repository.actualizar(producto)
                idParaNotificacion = productoId
            }

            // Notificación si stock bajo
            if (cantidad < stockMinimo) {
                NotificationHelper.enviarAlertaStockBajo(
                    context = context,
                    productoId = idParaNotificacion,
                    nombreProducto = state.nombre.trim(),
                    cantidadActual = cantidad,
                    stockMinimo = stockMinimo
                )
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                guardadoExitoso = true
            )
        }
    }

    private fun validar(): Boolean {
        val state = _uiState.value
        var valido = true

        if (state.nombre.isBlank()) {
            _uiState.value = _uiState.value.copy(nombreError = "El nombre es obligatorio")
            valido = false
        }
        if (state.precio.isBlank() || state.precio.toDoubleOrNull() == null || state.precio.toDouble() <= 0) {
            _uiState.value = _uiState.value.copy(precioError = "Ingresa un precio válido")
            valido = false
        }
        if (state.cantidad.isBlank() || state.cantidad.toIntOrNull() == null || state.cantidad.toInt() < 0) {
            _uiState.value = _uiState.value.copy(cantidadError = "Ingresa una cantidad válida")
            valido = false
        }
        if (state.stockMinimo.isBlank() || state.stockMinimo.toIntOrNull() == null || state.stockMinimo.toInt() < 0) {
            _uiState.value = _uiState.value.copy(stockMinimoError = "Ingresa un stock mínimo válido")
            valido = false
        }
        return valido
    }
}