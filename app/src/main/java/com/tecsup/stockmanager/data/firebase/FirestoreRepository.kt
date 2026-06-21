package com.tecsup.stockmanager.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.stockmanager.data.local.ProductEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    // Referencia a la colección de productos de un usuario específico
    private fun coleccion(usuarioId: String) =
        db.collection("usuarios").document(usuarioId).collection("productos")

    // Escucha en tiempo real los productos del usuario — retorna Flow
    fun obtenerTodos(usuarioId: String): Flow<List<ProductEntity>> = callbackFlow {
        val listener = coleccion(usuarioId)
            .orderBy("nombre")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toProductEntity()
                } ?: emptyList()
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    // Guardar o actualizar un producto en Firestore
    suspend fun guardar(producto: ProductEntity, usuarioId: String) {
        val datos = producto.toMap()
        if (producto.id == 0) {
            // Producto nuevo — Firestore genera el ID
            coleccion(usuarioId).add(datos).await()
        } else {
            // Producto existente — actualiza por ID
            coleccion(usuarioId).document(producto.id.toString()).set(datos).await()
        }
    }

    // Eliminar un producto de Firestore
    suspend fun eliminar(productoId: Int, usuarioId: String) {
        coleccion(usuarioId).document(productoId.toString()).delete().await()
    }
}

// Extensión: convierte un documento de Firestore a ProductEntity
private fun com.google.firebase.firestore.DocumentSnapshot.toProductEntity(): ProductEntity? {
    return try {
        ProductEntity(
            id = getString("id")?.toIntOrNull() ?: 0,
            nombre = getString("nombre") ?: return null,
            categoria = getString("categoria") ?: return null,
            precio = getDouble("precio") ?: return null,
            cantidad = getLong("cantidad")?.toInt() ?: return null,
            stockMinimo = getLong("stockMinimo")?.toInt() ?: return null,
            usuarioId = getString("usuarioId") ?: return null
        )
    } catch (e: Exception) {
        null
    }
}

// Extensión: convierte un ProductEntity a Map para guardar en Firestore
private fun ProductEntity.toMap(): Map<String, Any> = mapOf(
    "id" to id.toString(),
    "nombre" to nombre,
    "categoria" to categoria,
    "precio" to precio,
    "cantidad" to cantidad,
    "stockMinimo" to stockMinimo,
    "usuarioId" to usuarioId
)