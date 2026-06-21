package com.tecsup.stockmanager.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    // Usuario actualmente autenticado (null si no hay sesión)
    val usuarioActual: FirebaseUser?
        get() = auth.currentUser

    // UID del usuario actual (usado para filtrar productos en Room y Firestore)
    val uidActual: String
        get() = auth.currentUser?.uid ?: "local"

    // ¿Hay sesión activa?
    val haySession: Boolean
        get() = auth.currentUser != null

    // Registro con correo y contraseña
    suspend fun registrar(correo: String, contrasena: String): Result<FirebaseUser> {
        return try {
            val resultado = auth.createUserWithEmailAndPassword(correo, contrasena).await()
            val usuario = resultado.user
                ?: return Result.failure(Exception("No se pudo crear el usuario"))
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(traducirError(e))
        }
    }

    // Inicio de sesión con correo y contraseña
    suspend fun iniciarSesion(correo: String, contrasena: String): Result<FirebaseUser> {
        return try {
            val resultado = auth.signInWithEmailAndPassword(correo, contrasena).await()
            val usuario = resultado.user
                ?: return Result.failure(Exception("No se pudo iniciar sesión"))
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(traducirError(e))
        }
    }

    // Cierre de sesión
    fun cerrarSesion() {
        auth.signOut()
    }

    // Traduce errores de Firebase a mensajes en español
    private fun traducirError(e: Exception): Exception {
        val mensaje = when {
            e.message?.contains("credential is incorrect", ignoreCase = true) == true ||
                    e.message?.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) == true ||
                    e.message?.contains("password is invalid", ignoreCase = true) == true ||
                    e.message?.contains("malformed", ignoreCase = true) == true ->
                "Correo o contraseña incorrectos"

            e.message?.contains("email address is already in use", ignoreCase = true) == true ->
                "Este correo ya está registrado"

            e.message?.contains("no user record", ignoreCase = true) == true ||
                    e.message?.contains("USER_NOT_FOUND", ignoreCase = true) == true ->
                "No existe una cuenta con ese correo"

            e.message?.contains("badly formatted", ignoreCase = true) == true ->
                "El formato del correo no es válido"

            e.message?.contains("password should be at least", ignoreCase = true) == true ->
                "La contraseña debe tener al menos 6 caracteres"

            e.message?.contains("network error", ignoreCase = true) == true ||
                    e.message?.contains("NETWORK_ERROR", ignoreCase = true) == true ->
                "Sin conexión a Internet"

            e.message?.contains("expired", ignoreCase = true) == true ->
                "Correo o contraseña incorrectos"

            else -> "Error: ${e.message}"
        }
        return Exception(mensaje)
    }
}