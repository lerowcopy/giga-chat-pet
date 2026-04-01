package com.example.giga_chat_pet.ui.login

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onRetry() {
        if (isNetworkAvailable()) {
            _uiState.value = _uiState.value.copy(error = null, isNoNetwork = false)
        } else {
            _uiState.value = _uiState.value.copy(isNoNetwork = true)
        }
    }

    fun register() {
        if (!isNetworkAvailable()) {
            _uiState.value = _uiState.value.copy(isNoNetwork = true)
            return
        }

        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        val validationError = validateEmail(email)
        if (validationError != null) {
            _uiState.value = _uiState.value.copy(error = validationError)
            return
        }

        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(error = "Пароль должен содержать минимум 6 символов")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _uiState.value = _uiState.value.copy(isLoading = false)
                if (task.isSuccessful) {
                    _uiState.value = _uiState.value.copy(navigationToMain = true)
                } else {
                    val errorMessage = task.exception?.let { e ->
                        when (e) {
                            is FirebaseAuthException -> when (e.errorCode) {
                                "ERROR_EMAIL_ALREADY_IN_USE" -> "Этот email уже зарегистрирован"
                                "ERROR_INVALID_EMAIL" -> "Неверный формат email"
                                "ERROR_WEAK_PASSWORD" -> "Пароль слишком слабый"
                                else -> e.message ?: "Ошибка регистрации"
                            }
                            else -> e.message ?: "Ошибка регистрации"
                        }
                    } ?: "Ошибка регистрации"
                    _uiState.value = _uiState.value.copy(error = errorMessage)
                }
            }
    }

    fun signIn() {
        if (!isNetworkAvailable()) {
            _uiState.value = _uiState.value.copy(isNoNetwork = true)
            return
        }

        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        val validationError = validateEmail(email)
        if (validationError != null) {
            _uiState.value = _uiState.value.copy(error = validationError)
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _uiState.value = _uiState.value.copy(isLoading = false)
                if (task.isSuccessful) {
                    _uiState.value = _uiState.value.copy(navigationToMain = true)
                } else {
                    val errorMessage = task.exception?.let { e ->
                        when (e) {
                            is FirebaseAuthException -> when (e.errorCode) {
                                "ERROR_USER_NOT_FOUND" -> "Пользователь не найден"
                                "ERROR_WRONG_PASSWORD" -> "Неверный пароль"
                                "ERROR_INVALID_EMAIL" -> "Неверный формат email"
                                else -> e.message ?: "Ошибка входа"
                            }
                            else -> e.message ?: "Ошибка входа"
                        }
                    } ?: "Ошибка входа"
                    _uiState.value = _uiState.value.copy(error = errorMessage)
                }
            }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetNavigation() {
        _uiState.value = _uiState.value.copy(navigationToMain = false)
    }

    private fun validateEmail(email: String): String? {
        if (email.isEmpty()) {
            return "Введите email"
        }
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!email.matches(emailPattern.toRegex())) {
            return "Неверный формат email"
        }
        return null
    }

    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isNoNetwork: Boolean = false,
    val navigationToMain: Boolean = false
)
