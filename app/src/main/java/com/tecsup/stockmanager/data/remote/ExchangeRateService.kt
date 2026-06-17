package com.tecsup.stockmanager.data.remote

import retrofit2.http.GET

interface ExchangeRateService {

    // Consulta el tipo de cambio con base en PEN (soles)
    // Endpoint: https://api.exchangerate-api.com/v4/latest/PEN
    @GET("v4/latest/PEN")
    suspend fun obtenerTipoCambio(): ExchangeRateResponse
}