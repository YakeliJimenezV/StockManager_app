package com.tecsup.stockmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.stockmanager.data.local.ProductEntity
import com.tecsup.stockmanager.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val producto: ProductEntity? = null,
    val precioEnDolares: Double? = null,
    val isLoadingProducto: Boolean = true,
    val isLoadingDolar: Boolean = false,
    val errorDolar: String? = null
)

class ProductDetailViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun cargarProducto(id: Int) {
        viewModelScope.launch {
            val producto = repository.obtenerPorId(id)
            _uiState.value = _uiState.value.copy(
                producto = producto,
                isLoadingProducto = false
            )
            // Al cargar el producto, consulta el tipo de cambio automáticamente
            if (producto != null) {
                consultarTipoCambio(producto.precio)
            }
        }
    }

    private fun consultarTipoCambio(precioSoles: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingDolar = true,
                errorDolar = null
            )
            val resultado = repository.obtenerTipoCambioUSD()
            resultado.fold(
                onSuccess = { tipoCambio ->
                    val precioUSD = precioSoles * tipoCambio
                    _uiState.value = _uiState.value.copy(
                        precioEnDolares = precioUSD,
                        isLoadingDolar = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        errorDolar = "Sin conexión a Internet",
                        isLoadingDolar = false
                    )
                }
            )
        }
    }

    fun eliminar(onEliminado: () -> Unit) {
        viewModelScope.launch {
            _uiState.value.producto?.let {
                repository.eliminar(it)
                onEliminado()
            }
        }
    }
}