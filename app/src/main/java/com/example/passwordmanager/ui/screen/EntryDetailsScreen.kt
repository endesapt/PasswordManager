package com.example.passwordmanager.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.passwordmanager.app.AppContainer
import com.example.passwordmanager.ui.components.StatusMessage
import com.example.passwordmanager.ui.viewmodel.EntryDetailsUiState
import com.example.passwordmanager.ui.viewmodel.EntryDetailsViewModel

@Composable
fun EntryDetailsRoute(
    container: AppContainer,
    itemId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
) {
    val viewModel: EntryDetailsViewModel = viewModel(
        key = "detail-$itemId",
        factory = EntryDetailsViewModel.Factory(
            itemId = itemId,
            repository = container.vaultRepository,
            breachChecker = container.breachChecker,
        ),
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    EntryDetailsScreen(
        state = state,
        onBack = onBack,
        onEdit = { onEdit(itemId) },
        onDelete = viewModel::delete,
        onCheckPasswordBreach = viewModel::checkPasswordBreach,
        onDeleted = onDeleted,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryDetailsScreen(
    state: EntryDetailsUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCheckPasswordBreach: () -> Unit,
    onDeleted: () -> Unit,
) {
    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            onDeleted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entry details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit")
                    }
                },
            )
        },
    ) { innerPadding ->
        EntryDetailsContent(
            state = state,
            innerPadding = innerPadding,
            onDelete = onDelete,
            onCheckPasswordBreach = onCheckPasswordBreach,
        )
    }
}

@Composable
private fun EntryDetailsContent(
    state: EntryDetailsUiState,
    innerPadding: PaddingValues,
    onDelete: () -> Unit,
    onCheckPasswordBreach: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
            return@Column
        }

        val item = state.item
        if (item == null) {
            StatusMessage(message = state.errorMessage ?: "Entry not found.")
            return@Column
        }

        Text(text = item.title, style = MaterialTheme.typography.headlineMedium)

        DetailLine(
            label = "Username",
            value = item.username.ifBlank { "No username saved" },
            onCopy = if (item.username.isBlank()) null else {
                { clipboardManager.setText(AnnotatedString(item.username)) }
            },
        )

        DetailLine(
            label = "Website",
            value = item.website.ifBlank { "No website saved" },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(text = "Password", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = if (passwordVisible) item.password else "•".repeat(item.password.length.coerceAtLeast(8)),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Row {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    )
                }
                IconButton(onClick = { clipboardManager.setText(AnnotatedString(item.password)) }) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy password")
                }
            }
        }

        if (item.notes.isNotBlank()) {
            DetailLine(label = "Notes", value = item.notes)
        }

        state.errorMessage?.let { message ->
            StatusMessage(message = message)
        }
        state.breachStatusMessage?.let { message ->
            StatusMessage(message = message)
        }

        Button(
            onClick = onCheckPasswordBreach,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Run breach check")
        }

        Button(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Outlined.Delete, contentDescription = null)
            Text(
                text = "Delete entry",
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Composable
private fun DetailLine(
    label: String,
    value: String,
    onCopy: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = label, style = MaterialTheme.typography.titleLarge)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
        if (onCopy != null) {
            IconButton(onClick = onCopy) {
                Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy $label")
            }
        }
    }
}
