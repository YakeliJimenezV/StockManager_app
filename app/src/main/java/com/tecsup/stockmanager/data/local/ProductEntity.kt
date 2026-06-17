package com.tecsup.stockmanager.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val categoria: String,       // Abarrotes, Bebidas, Limpieza, Otros
    val precio: Double,          // En soles (S/)
    val cantidad: Int,
    val stockMinimo: Int,
    val usuarioId: String = "local"  // Sprint 2: reemplazar con UID de Firebase
)