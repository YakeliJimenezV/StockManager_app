package com.tecsup.stockmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.stockmanager.data.firebase.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val correo: String = "",
    val contrasena: String = "",
    val correoError: String? = null,
    val contrasenaError: String? = null,
    val isLoading: Boolean = false,
    val errorGeneral: String? = null,
    val loginExitoso: Boolean = false,
    val modoRegistro: Boolean = false  // false = login, true = registro
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ¿Hay sesión activa? Lo chequeamos al arrancar la app
    val haySession: Boolean
        get() = authRepository.haySession

    // Actualizar campos
    fun onCorreoChange(value: String) {
        _uiState.value = _uiState.value.copy(
            correo = value,
            correoError = null,
            errorGeneral = null
        )
    }

    fun onContrasenaChange(value: String) {
        _uiState.value = _uiState.value.copy(
            contrasena = value,
            contrasenaError = null,
            errorGeneral = null
        )
    }

    // Alternar entre modo login y modo registro
    fun toggleModo() {
        _uiState.value = _uiState.value.copy(
            modoRegistro = !_uiState.value.modoRegistro,
            correoError = null,
            contrasenaError = null,
            errorGeneral = null
        )
    }

    // Ejecutar login o registro según el modo actual
    fun ejecutarAccion() {
        if (!validar()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorGeneral = null)
            val state = _uiState.value

            val resultado = if (state.modoRegistro) {
                authRepository.registrar(state.correo.trim(), state.contrasena)
            } else {
                authRepository.iniciarSesion(state.correo.trim(), state.contrasena)
            }

            resultado.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginExitoso = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorGeneral = error.message
                    )
                }
            )
        }
    }

    fun cerrarSesion() {
        authRepository.cerrarSesion()
        _uiState.value = AuthUiState()
    }

    private fun validar(): Boolean {
        val state = _uiState.value
        var valido = true

        if (state.correo.isBlank()) {
            _uiState.value = _uiState.value.copy(correoError = "El correo es obligatorio")
            valido = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.correo).matches()) {
            _uiState.value = _uiState.value.copy(correoError = "El formato del correo no es válido")
            valido = false
        }

        if (state.contrasena.isBlank()) {
            _uiState.value = _uiState.value.copy(contrasenaError = "La contraseña es obligatoria")
            valido = false
        } else if (state.contrasena.length < 6) {
            _uiState.value = _uiState.value.copy(contrasenaError = "Mínimo 6 caracteres")
            valido = false
        }

        return valido
    }
}