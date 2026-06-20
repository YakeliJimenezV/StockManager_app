package com.tecsup.stockmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.stockmanager.data.local.ProductEntity
import com.tecsup.stockmanager.data.repository.ProductRepository
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
//privado solo viewmodel modifica
    private val _uiState = MutableStateFlow(ProductFormUiState())
    //// Público: la UI solo puede leerlo, no modificarlo
    val uiState: StateFlow<ProductFormUiState> = _uiState.asStateFlow()

    // Cargar datos si estamos editando
    fun cargarProducto(id: Int) {
        viewModelScope.launch {
            // _uiState mutable solo viewmodel modifica
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

    // Actualizar campos del formulario
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

    // Validar y guardar
    fun guardar(productoId: Int? = null) {
        if (!validar()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val state = _uiState.value
            val producto = ProductEntity(
                id = productoId ?: 0,
                nombre = state.nombre.trim(),
                categoria = state.categoria,
                precio = state.precio.toDouble(),
                cantidad = state.cantidad.toInt(),
                stockMinimo = state.stockMinimo.toInt(),
                usuarioId = "local"
            )
            if (productoId == null) {
                repository.insertar(producto)
            } else {
                repository.actualizar(producto)
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