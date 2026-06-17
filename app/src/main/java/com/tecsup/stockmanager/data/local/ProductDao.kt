package com.tecsup.stockmanager.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // Insertar producto nuevo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: ProductEntity)

    // Obtener todos los productos del usuario — retorna Flow para reactividad
    @Query("SELECT * FROM productos WHERE usuarioId = :usuarioId ORDER BY nombre ASC")
    fun obtenerTodos(usuarioId: String = "local"): Flow<List<ProductEntity>>

    // Obtener un producto por ID
    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): ProductEntity?

    // Actualizar producto existente
    @Update
    suspend fun actualizar(producto: ProductEntity)

    // Eliminar producto
    @Delete
    suspend fun eliminar(producto: ProductEntity)

    // Productos en stock crítico (cantidad < stockMinimo)
    @Query("SELECT * FROM productos WHERE cantidad < stockMinimo AND usuarioId = :usuarioId")
    fun obtenerStockCritico(usuarioId: String = "local"): Flow<List<ProductEntity>>

    // Valor total del inventario
    @Query("SELECT SUM(precio * cantidad) FROM productos WHERE usuarioId = :usuarioId")
    fun obtenerValorTotal(usuarioId: String = "local"): Flow<Double?>
}