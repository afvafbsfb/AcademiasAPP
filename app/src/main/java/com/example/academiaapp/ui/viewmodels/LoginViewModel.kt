package com.example.academiaapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.academiaapp.domain.LoginRepository
import com.example.academiaapp.domain.ChatRepository
import com.example.academiaapp.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val mustChangePassword: Boolean = false
)

class LoginViewModel(private val repo: LoginRepository, private val chatRepo: ChatRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onUsernameChange(newUsername: String) {
        _uiState.update { it.copy(username = newUsername) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
    }

    fun onLoginClick() {
        val email = _uiState.value.username.trim()
        val pass = _uiState.value.password
        if (email.isEmpty() || pass.isEmpty()) {
            _uiState.update { it.copy(error = "Usuario y contraseña obligatorios") }
            return
        }
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val res = repo.login(email, pass)) {
                is Result.Success -> {
                    val mustChangePassword = res.data
                    _uiState.update { it.copy(loading = false, success = true, mustChangePassword = mustChangePassword) }
                    // Solo lanzar welcome si NO requiere cambio de contraseña
                    if (!mustChangePassword) {
                        launch {
                            try {
                                chatRepo.welcome()
                            } catch (_: Throwable) {
                                // Ignorar errores aquí; ChatViewModel gestionará reintentos si es necesario
                            }
                        }
                    }
                }
                is Result.Error -> _uiState.update { it.copy(loading = false, error = res.message) }
            }
        }
    }
}

class LoginViewModelFactory(private val repo: LoginRepository, private val chatRepo: ChatRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repo, chatRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
