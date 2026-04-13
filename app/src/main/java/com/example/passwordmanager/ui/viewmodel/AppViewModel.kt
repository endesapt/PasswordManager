package com.example.passwordmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.app.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppUiState(
    val isLoading: Boolean = true,
    val hasMasterPassword: Boolean = false,
    val isUnlocked: Boolean = false,
    val errorMessage: String? = null,
)

class AppViewModel(
    private val container: AppContainer,
) : ViewModel() {
    private val _state = MutableStateFlow(AppUiState())
    val state: StateFlow<AppUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val hasMasterPassword = container.masterPasswordManager.hasMasterPassword()
            _state.update {
                it.copy(
                    isLoading = false,
                    hasMasterPassword = hasMasterPassword,
                    isUnlocked = container.masterPasswordManager.isUnlocked(),
                )
            }
        }
    }

    fun setupMasterPassword(password: String, confirmation: String) {
        viewModelScope.launch {
            when {
                password != confirmation -> {
                    _state.update { it.copy(errorMessage = "Passwords do not match.") }
                }

                password.length < 8 -> {
                    _state.update { it.copy(errorMessage = "Use at least 8 characters.") }
                }

                else -> {
                    val result = container.masterPasswordManager.setupPassword(password)
                    _state.update {
                        it.copy(errorMessage = result.exceptionOrNull()?.message)
                    }
                    refresh()
                }
            }
        }
    }

    fun unlock(password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val success = container.masterPasswordManager.unlock(password)
            _state.update {
                it.copy(
                    isLoading = false,
                    isUnlocked = success,
                    hasMasterPassword = true,
                    errorMessage = if (success) null else "Incorrect master password.",
                )
            }
        }
    }

    fun lock() {
        container.masterPasswordManager.lock()
        _state.update { it.copy(isUnlocked = false) }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    class Factory(
        private val container: AppContainer,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppViewModel(container) as T
        }
    }
}
