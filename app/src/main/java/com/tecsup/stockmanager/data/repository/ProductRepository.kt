package com.tecsup.stockmanager.data.repository

import com.tecsup.stockmanager.data.local.ProductDao
import com.tecsup.stockmanager.data.local.ProductEntity
import com.tecsup.stockmanager.data.remote.ExchangeRateService
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val dao: ProductDao,
    private val exchangeRateService: ExchangeRateService
) {

    // ─── CRUD LOCAL (Room) ───────────────────────────────────────────

    fun obtenerTodos(usuarioId: String = "local"): Flow<List<ProductEntity>> =
        dao.obtenerTodos(usuarioId)

    suspend fun obtenerPorId(id: Int): ProductEntity? =
        dao.obtenerPorId(id)

    suspend fun insertar(producto: ProductEntity) =
        dao.insertar(producto)

    suspend fun actualizar(producto: ProductEntity) =
        dao.actualizar(producto)

    suspend fun eliminar(producto: ProductEntity) =
        dao.eliminar(producto)

    // ─── ESTADÍSTICAS ────────────────────────────────────────────────

    fun obtenerStockCritico(usuarioId: String = "local"): Flow<List<ProductEntity>> =
        dao.obtenerStockCritico(usuarioId)

    fun obtenerValorTotal(usuarioId: String = "local"): Flow<Double?> =
        dao.obtenerValorTotal(usuarioId)

    // ─── REMOTO (Retrofit + ExchangeRate API) ────────────────────────

    suspend fun obtenerTipoCambioUSD(): Result<Double> {
        return try {
            val response = exchangeRateService.obtenerTipoCambio()
            // La API retorna cuántos USD equivale 1 PEN
            val usd = response.rates["USD"]
                ?: return Result.failure(Exception("Tipo de cambio USD no disponible"))
            Result.success(usd)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}