package com.tecsup.stockmanager.data.remote

import com.google.gson.annotations.SerializedName

// Respuesta completa de la API
data class ExchangeRateResponse(
    @SerializedName("base") val base: String, // PEN
    @SerializedName("rates") val rates: Map<String, Double> //USD
)
