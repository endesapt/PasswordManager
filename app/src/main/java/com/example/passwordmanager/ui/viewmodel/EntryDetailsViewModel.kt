package com.example.passwordmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.domain.model.BreachCheckResult
import com.example.passwordmanager.domain.model.VaultItem
import com.example.passwordmanager.domain.repository.VaultRepository
import com.example.passwordmanager.domain.service.BreachChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EntryDetailsUiState(
    val isLoading: Boolean = true,
    val item: VaultItem? = null,
    val isDeleted: Boolean = false,
    val errorMessage: String? = null,
    val breachStatusMessage: String? = null,
)

class EntryDetailsViewModel(
    private val itemId: Long,
    private val repository: VaultRepository,
    private val breachChecker: BreachChecker,
) : ViewModel() {
    private val _state = MutableStateFlow(EntryDetailsUiState())
    val state: StateFlow<EntryDetailsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val item = repository.getVaultItem(itemId)
            _state.update {
                it.copy(
                    isLoading = false,
                    item = item,
                    errorMessage = if (item == null) "Entry not found." else null,
                )
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            runCatching {
                repository.deleteVaultItem(itemId)
            }.onSuccess {
                _state.update { it.copy(isDeleted = true) }
            }.onFailure { throwable ->
                _state.update { it.copy(errorMessage = throwable.message) }
            }
        }
    }

    fun checkPasswordBreach() {
        val password = state.value.item?.password.orEmpty()
        if (password.isBlank()) {
            _state.update {
                it.copy(
                    errorMessage = "No password available for the breach check.",
                    breachStatusMessage = null,
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    errorMessage = null,
                    breachStatusMessage = null,
                )
            }
            breachChecker.checkPassword(password)
                .onSuccess { result ->
                    _state.update { state ->
                        state.copy(
                            errorMessage = null,
                            breachStatusMessage = result.toUiMessage(),
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update { state ->
                        state.copy(
                            errorMessage = throwable.message ?: "Password check failed.",
                            breachStatusMessage = null,
                        )
                    }
                }
        }
    }

    fun clearMessage() {
        _state.update { it.copy(errorMessage = null, breachStatusMessage = null) }
    }

    private fun BreachCheckResult.toUiMessage(): String {
        return if (isCompromised) {
            "Compromised password detected: found $breachCount times."
        } else {
            "Password was not found in the breach corpus."
        }
    }

    class Factory(
        private val itemId: Long,
        private val repository: VaultRepository,
        private val breachChecker: BreachChecker,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EntryDetailsViewModel(itemId, repository, breachChecker) as T
        }
    }
}
