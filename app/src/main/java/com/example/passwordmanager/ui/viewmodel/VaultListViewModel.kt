package com.example.passwordmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.domain.model.VaultItem
import com.example.passwordmanager.domain.repository.VaultRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class VaultListUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    val items: List<VaultItem> = emptyList(),
    val errorMessage: String? = null,
)

class VaultListViewModel(
    private val repository: VaultRepository,
) : ViewModel() {
    private val queryFlow = MutableStateFlow("")
    private val errorFlow = MutableStateFlow<String?>(null)

    val state: StateFlow<VaultListUiState> = combine(
        repository.observeVaultItems(),
        queryFlow,
        errorFlow,
    ) { items, query, error ->
        val filtered = if (query.isBlank()) {
            items
        } else {
            val normalized = query.trim().lowercase()
            items.filter { item ->
                item.title.lowercase().contains(normalized) ||
                    item.username.lowercase().contains(normalized) ||
                    item.website.lowercase().contains(normalized)
            }
        }

        VaultListUiState(
            isLoading = false,
            query = query,
            items = filtered,
            errorMessage = error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = VaultListUiState(),
    )

    fun onQueryChange(query: String) {
        queryFlow.value = query
    }

    fun toggleFavorite(item: VaultItem) {
        viewModelScope.launch {
            runCatching {
                repository.upsertVaultItem(item.copy(isFavorite = !item.isFavorite))
            }.onFailure { throwable ->
                errorFlow.value = throwable.message
            }
        }
    }

    fun clearError() {
        errorFlow.value = null
    }

    class Factory(
        private val repository: VaultRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VaultListViewModel(repository) as T
        }
    }
}
