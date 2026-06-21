package com.tecsup.stockmanager.data.repository

import com.tecsup.stockmanager.data.firebase.AuthRepository
import com.tecsup.stockmanager.data.firebase.FirestoreRepository
import com.tecsup.stockmanager.data.local.ProductDao
import com.tecsup.stockmanager.data.local.ProductEntity
import com.tecsup.stockmanager.data.remote.ExchangeRateService
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val dao: ProductDao,
    private val exchangeRateService: ExchangeRateService,
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) {

    private val uid: String
        get() = authRepository.uidActual

    fun obtenerTodos(): Flow<List<ProductEntity>> =
        dao.obtenerTodos(uid)

    suspend fun obtenerPorId(id: Int): ProductEntity? =
        dao.obtenerPorId(id)

    suspend fun insertar(producto: ProductEntity) {
        val productoConUid = producto.copy(usuarioId = uid)
        dao.insertar(productoConUid)
        try {
            firestoreRepository.guardar(productoConUid, uid)
        } catch (_: Exception) { }
    }

    suspend fun insertarYObtenerID(producto: ProductEntity): Long {
        val productoConUid = producto.copy(usuarioId = uid)
        val id = dao.insertarYObtenerID(productoConUid)
        try {
            firestoreRepository.guardar(productoConUid.copy(id = id.toInt()), uid)
        } catch (_: Exception) { }
        return id
    }

    suspend fun actualizar(producto: ProductEntity) {
        val productoConUid = producto.copy(usuarioId = uid)
        dao.actualizar(productoConUid)
        try {
            firestoreRepository.guardar(productoConUid, uid)
        } catch (_: Exception) { }
    }

    suspend fun eliminar(producto: ProductEntity) {
        dao.eliminar(producto)
        try {
            firestoreRepository.eliminar(producto.id, uid)
        } catch (_: Exception) { }
    }

    fun obtenerStockCritico(): Flow<List<ProductEntity>> =
        dao.obtenerStockCritico(uid)

    fun obtenerValorTotal(): Flow<Double?> =
        dao.obtenerValorTotal(uid)

    suspend fun obtenerTipoCambioUSD(): Result<Double> {
        return try {
            val response = exchangeRateService.obtenerTipoCambio()
            val usd = response.rates["USD"]
                ?: return Result.failure(Exception("Tipo de cambio USD no disponible"))
            Result.success(usd)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}