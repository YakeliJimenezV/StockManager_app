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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarYObtenerID(producto: ProductEntity): Long

    @Query("SELECT * FROM productos WHERE usuarioId = :usuarioId ORDER BY nombre ASC")
    fun obtenerTodos(usuarioId: String = "local"): Flow<List<ProductEntity>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): ProductEntity?

    @Update
    suspend fun actualizar(producto: ProductEntity)

    @Delete
    suspend fun eliminar(producto: ProductEntity)

    @Query("SELECT * FROM productos WHERE cantidad < stockMinimo AND usuarioId = :usuarioId")
    fun obtenerStockCritico(usuarioId: String = "local"): Flow<List<ProductEntity>>

    @Query("SELECT SUM(precio * cantidad) FROM productos WHERE usuarioId = :usuarioId")
    fun obtenerValorTotal(usuarioId: String = "local"): Flow<Double?>
}