package com.tecsup.stockmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.stockmanager.data.repository.ProductRepository

class ViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProductListViewModel::class.java) ->
                ProductListViewModel(repository) as T

            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) ->
                ProductDetailViewModel(repository) as T

            modelClass.isAssignableFrom(ProductFormViewModel::class.java) ->
                ProductFormViewModel(repository) as T

            modelClass.isAssignableFrom(StatsViewModel::class.java) ->
                StatsViewModel(repository) as T

            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}