package com.tecsup.stockmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.stockmanager.data.local.ProductEntity
import com.tecsup.stockmanager.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class StatsUiState(
    val valorTotalSoles: Double = 0.0,
    val valorTotalDolares: Double? = null,
    val productosStockCritico: List<ProductEntity> = emptyList(),
    val productoConMenosStock: ProductEntity? = null,
    val isLoading: Boolean = true,
    val isLoadingDolar: Boolean = false,
    val errorDolar: String? = null
)

class StatsViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        cargarEstadisticas()
    }

    private fun cargarEstadisticas() {
        viewModelScope.launch {
            combine(
                repository.obtenerValorTotal(),
                repository.obtenerStockCritico()
            ) { valorTotal, stockCritico ->
                Pair(valorTotal, stockCritico)
            }.collect { (valorTotal, stockCritico) ->
                val criticos = stockCritico.sortedBy { it.cantidad }
                _uiState.value = _uiState.value.copy(
                    valorTotalSoles = valorTotal ?: 0.0,
                    productosStockCritico = criticos,
                    productoConMenosStock = criticos.firstOrNull(),
                    isLoading = false
                )
            }
        }
        consultarDolar()
    }

    private fun consultarDolar() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingDolar = true)
            val resultado = repository.obtenerTipoCambioUSD()
            resultado.fold(
                onSuccess = { tipoCambio ->
                    _uiState.value = _uiState.value.copy(
                        valorTotalDolares = _uiState.value.valorTotalSoles * tipoCambio,
                        isLoadingDolar = false,
                        errorDolar = null
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isLoadingDolar = false,
                        errorDolar = "Sin conexión"
                    )
                }
            )
        }
    }
}