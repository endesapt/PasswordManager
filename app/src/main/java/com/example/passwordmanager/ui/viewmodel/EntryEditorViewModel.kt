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

data class EntryEditorUiState(
    val itemId: Long? = null,
    val title: String = "",
    val username: String = "",
    val password: String = "",
    val website: String = "",
    val notes: String = "",
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isCheckingPassword: Boolean = false,
    val saveCompleted: Boolean = false,
    val errorMessage: String? = null,
    val breachStatusMessage: String? = null,
) {
    val isEditing: Boolean get() = itemId != null
}

class EntryEditorViewModel(
    private val itemId: Long?,
    private val repository: VaultRepository,
    private val breachChecker: BreachChecker,
) : ViewModel() {
    private val _state = MutableStateFlow(EntryEditorUiState(itemId = itemId, isLoading = itemId != null))
    val state: StateFlow<EntryEditorUiState> = _state.asStateFlow()

    init {
        if (itemId != null) {
            loadItem(itemId)
        }
    }

    fun onTitleChange(value: String) = _state.update { it.copy(title = value) }
    fun onUsernameChange(value: String) = _state.update { it.copy(username = value) }
    fun onPasswordChange(value: String) = _state.update { it.copy(password = value, breachStatusMessage = null) }
    fun onWebsiteChange(value: String) = _state.update { it.copy(website = value) }
    fun onNotesChange(value: String) = _state.update { it.copy(notes = value) }
    fun onFavoriteChange(value: Boolean) = _state.update { it.copy(isFavorite = value) }

    fun save() {
        val snapshot = state.value
        when {
            snapshot.title.isBlank() -> {
                _state.update { it.copy(errorMessage = "Title is required.") }
            }

            snapshot.password.isBlank() -> {
                _state.update { it.copy(errorMessage = "Password is required.") }
            }

            else -> {
                viewModelScope.launch {
                    _state.update { it.copy(isSaving = true, errorMessage = null) }
                    runCatching {
                        repository.upsertVaultItem(
                            VaultItem(
                                id = snapshot.itemId ?: 0L,
                                title = snapshot.title,
                                username = snapshot.username,
                                password = snapshot.password,
                                website = snapshot.website,
                                notes = snapshot.notes,
                                isFavorite = snapshot.isFavorite,
                            ),
                        )
                    }.onSuccess {
                        _state.update { state ->
                            state.copy(
                                isSaving = false,
                                saveCompleted = true,
                            )
                        }
                    }.onFailure { throwable ->
                        _state.update { state ->
                            state.copy(
                                isSaving = false,
                                errorMessage = throwable.message,
                            )
                        }
                    }
                }
            }
        }
    }

    fun checkPasswordBreach() {
        val password = state.value.password
        if (password.isBlank()) {
            _state.update { it.copy(errorMessage = "Enter a password before running the check.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isCheckingPassword = true, errorMessage = null) }
            breachChecker.checkPassword(password)
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            isCheckingPassword = false,
                            breachStatusMessage = result.toUiMessage(),
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isCheckingPassword = false,
                            errorMessage = throwable.message ?: "Password check failed.",
                        )
                    }
                }
        }
    }

    fun clearTransientState() {
        _state.update {
            it.copy(
                errorMessage = null,
                saveCompleted = false,
            )
        }
    }

    private fun loadItem(id: Long) {
        viewModelScope.launch {
            val item = repository.getVaultItem(id)
            _state.update {
                if (item == null) {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Entry not found.",
                    )
                } else {
                    it.copy(
                        itemId = item.id,
                        title = item.title,
                        username = item.username,
                        password = item.password,
                        website = item.website,
                        notes = item.notes,
                        isFavorite = item.isFavorite,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun BreachCheckResult.toUiMessage(): String {
        return if (isCompromised) {
            "This password appeared in breaches $breachCount times."
        } else {
            "Good news: no breach found for this password."
        }
    }

    class Factory(
        private val itemId: Long?,
        private val repository: VaultRepository,
        private val breachChecker: BreachChecker,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EntryEditorViewModel(itemId, repository, breachChecker) as T
        }
    }
}
