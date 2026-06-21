package com.tecsup.stockmanager

import android.app.Application
import com.tecsup.stockmanager.data.firebase.AuthRepository
import com.tecsup.stockmanager.data.firebase.FirestoreRepository
import com.tecsup.stockmanager.data.local.StockManagerDatabase
import com.tecsup.stockmanager.data.remote.RetrofitInstance
import com.tecsup.stockmanager.data.repository.ProductRepository
import com.tecsup.stockmanager.util.NotificationHelper

class StockManagerApp : Application() {

    val database: StockManagerDatabase by lazy {
        StockManagerDatabase.getInstance(this)
    }

    val exchangeRateService by lazy {
        RetrofitInstance.service
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository()
    }

    val firestoreRepository: FirestoreRepository by lazy {
        FirestoreRepository()
    }

    val repository: ProductRepository by lazy {
        ProductRepository(
            dao = database.productDao(),
            exchangeRateService = exchangeRateService,
            authRepository = authRepository,
            firestoreRepository = firestoreRepository
        )
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.crearCanal(this)
    }
}