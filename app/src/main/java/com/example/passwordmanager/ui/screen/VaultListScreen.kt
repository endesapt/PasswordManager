package com.example.passwordmanager.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.passwordmanager.app.AppContainer
import com.example.passwordmanager.domain.model.VaultItem
import com.example.passwordmanager.ui.components.StatusMessage
import com.example.passwordmanager.ui.viewmodel.VaultListUiState
import com.example.passwordmanager.ui.viewmodel.VaultListViewModel

@Composable
fun VaultListRoute(
    container: AppContainer,
    onOpenSettings: () -> Unit,
    onOpenItem: (Long) -> Unit,
    onCreateItem: () -> Unit,
) {
    val viewModel: VaultListViewModel = viewModel(
        factory = VaultListViewModel.Factory(container.vaultRepository),
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    VaultListScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onToggleFavorite = viewModel::toggleFavorite,
        onOpenSettings = onOpenSettings,
        onOpenItem = onOpenItem,
        onCreateItem = onCreateItem,
        onClearError = viewModel::clearError,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultListScreen(
    state: VaultListUiState,
    onQueryChange: (String) -> Unit,
    onToggleFavorite: (VaultItem) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenItem: (Long) -> Unit,
    onCreateItem: () -> Unit,
    onClearError: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vault") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateItem,
                modifier = Modifier.testTag("addEntryButton"),
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add entry")
            }
        },
    ) { innerPadding ->
        VaultListContent(
            state = state,
            innerPadding = innerPadding,
            onQueryChange = onQueryChange,
            onToggleFavorite = onToggleFavorite,
            onOpenItem = onOpenItem,
            onClearError = onClearError,
        )
    }
}

@Composable
private fun VaultListContent(
    state: VaultListUiState,
    innerPadding: PaddingValues,
    onQueryChange: (String) -> Unit,
    onToggleFavorite: (VaultItem) -> Unit,
    onOpenItem: (Long) -> Unit,
    onClearError: () -> Unit,
) {
    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) {
            onClearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("vaultSearchField"),
            label = { Text("Search by title, login, or website") },
            singleLine = true,
        )

        state.errorMessage?.let { message ->
            StatusMessage(message = message)
        }

        if (state.items.isEmpty()) {
            Text(
                text = "No entries yet. Tap the + button to add your first account.",
                style = MaterialTheme.typography.bodyLarge,
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = state.items,
                    key = { item -> item.id },
                ) { item ->
                    VaultItemCard(
                        item = item,
                        onOpen = { onOpenItem(item.id) },
                        onToggleFavorite = { onToggleFavorite(item) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VaultItemCard(
    item: VaultItem,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onOpen,
                onLongClick = onToggleFavorite,
            )
            .testTag("vaultItem_${item.id}"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = item.title, style = MaterialTheme.typography.titleLarge)
                if (item.username.isNotBlank()) {
                    Text(text = item.username, style = MaterialTheme.typography.bodyLarge)
                }
                if (item.website.isNotBlank()) {
                    Text(text = item.website, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Icon(
                imageVector = if (item.isFavorite) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                contentDescription = if (item.isFavorite) "Favorite" else "Not favorite",
            )
        }
    }
}
