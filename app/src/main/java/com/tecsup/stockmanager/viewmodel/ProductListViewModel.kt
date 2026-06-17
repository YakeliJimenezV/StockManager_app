package com.tecsup.stockmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.stockmanager.data.local.ProductEntity
import com.tecsup.stockmanager.data.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProductListUiState(
    val productos: List<ProductEntity> = emptyList(),
    val isLoading: Boolean = true
)

class ProductListViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    val uiState: StateFlow<ProductListUiState> =
        repository.obtenerTodos()
            .map { lista -> ProductListUiState(productos = lista, isLoading = false) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ProductListUiState()
            )

    fun eliminar(producto: ProductEntity) {
        viewModelScope.launch {
            repository.eliminar(producto)
        }
    }
}