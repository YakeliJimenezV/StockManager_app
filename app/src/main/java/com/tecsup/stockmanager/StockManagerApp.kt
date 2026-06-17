package com.tecsup.stockmanager

import android.app.Application
import com.tecsup.stockmanager.data.local.StockManagerDatabase
import com.tecsup.stockmanager.data.remote.RetrofitInstance
import com.tecsup.stockmanager.data.repository.ProductRepository

class StockManagerApp : Application() {

    // Base de datos Room — instancia única en toda la app
    val database: StockManagerDatabase by lazy {
        StockManagerDatabase.getInstance(this)
    }

    // Retrofit — instancia única
    val exchangeRateService by lazy {
        RetrofitInstance.service
    }

    // Repository — instancia única accesible desde ViewModels
    val repository: ProductRepository by lazy {
        ProductRepository(
            dao = database.productDao(),
            exchangeRateService = exchangeRateService
        )
    }
}