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
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.passwordmanager.app.AppContainer
import com.example.passwordmanager.ui.components.StatusMessage
import com.example.passwordmanager.ui.viewmodel.EntryEditorUiState
import com.example.passwordmanager.ui.viewmodel.EntryEditorViewModel

@Composable
fun EntryEditorRoute(
    container: AppContainer,
    itemId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
) {
    val viewModel: EntryEditorViewModel = viewModel(
        key = "editor-$itemId",
        factory = EntryEditorViewModel.Factory(
            itemId = itemId,
            repository = container.vaultRepository,
            breachChecker = container.breachChecker,
        ),
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    EntryEditorScreen(
        state = state,
        onBack = onBack,
        onSaved = onSaved,
        onTitleChange = viewModel::onTitleChange,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onWebsiteChange = viewModel::onWebsiteChange,
        onNotesChange = viewModel::onNotesChange,
        onFavoriteChange = viewModel::onFavoriteChange,
        onSave = viewModel::save,
        onCheckPasswordBreach = viewModel::checkPasswordBreach,
        onClearTransientState = viewModel::clearTransientState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryEditorScreen(
    state: EntryEditorUiState,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onTitleChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onWebsiteChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onFavoriteChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onCheckPasswordBreach: () -> Unit,
    onClearTransientState: () -> Unit,
) {
    LaunchedEffect(state.saveCompleted) {
        if (state.saveCompleted) {
            onClearTransientState()
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit entry" else "New entry") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        EntryEditorContent(
            state = state,
            innerPadding = innerPadding,
            onTitleChange = onTitleChange,
            onUsernameChange = onUsernameChange,
            onPasswordChange = onPasswordChange,
            onWebsiteChange = onWebsiteChange,
            onNotesChange = onNotesChange,
            onFavoriteChange = onFavoriteChange,
            onSave = onSave,
            onCheckPasswordBreach = onCheckPasswordBreach,
        )
    }
}

@Composable
private fun EntryEditorContent(
    state: EntryEditorUiState,
    innerPadding: PaddingValues,
    onTitleChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onWebsiteChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onFavoriteChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onCheckPasswordBreach: () -> Unit,
) {
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

        OutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("entryTitleField"),
            label = { Text("Title") },
            singleLine = true,
        )
        OutlinedTextField(
            value = state.username,
            onValueChange = onUsernameChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("entryUsernameField"),
            label = { Text("Username or email") },
            singleLine = true,
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("entryPasswordField"),
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    )
                }
            },
            singleLine = true,
        )
        OutlinedTextField(
            value = state.website,
            onValueChange = onWebsiteChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("entryWebsiteField"),
            label = { Text("Website") },
            singleLine = true,
        )
        OutlinedTextField(
            value = state.notes,
            onValueChange = onNotesChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("entryNotesField"),
            label = { Text("Notes") },
            minLines = 4,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Switch(
                checked = state.isFavorite,
                onCheckedChange = onFavoriteChange,
            )
            Text("Mark as favorite")
        }

        state.errorMessage?.let { message ->
            StatusMessage(message = message)
        }
        state.breachStatusMessage?.let { message ->
            StatusMessage(message = message)
        }

        Button(
            onClick = onCheckPasswordBreach,
            enabled = !state.isCheckingPassword,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (state.isCheckingPassword) {
                CircularProgressIndicator()
            } else {
                Text("Check breach status")
            }
        }

        Button(
            onClick = onSave,
            enabled = !state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("saveEntryButton"),
        ) {
            if (state.isSaving) {
                CircularProgressIndicator()
            } else {
                Text(if (state.isEditing) "Save changes" else "Save entry")
            }
        }
    }
}
