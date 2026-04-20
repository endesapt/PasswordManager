package com.example.passwordmanager.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.passwordmanager.ui.components.StatusMessage
import com.example.passwordmanager.ui.viewmodel.AppUiState

@Composable
fun VaultAuthScreen(
    isSetupMode: Boolean,
    appState: AppUiState,
    onSubmit: (String, String) -> Unit,
    onClearMessage: () -> Unit,
    onAuthenticated: () -> Unit,
) {
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }

    LaunchedEffect(appState.isUnlocked, appState.hasMasterPassword, isSetupMode) {
        val shouldContinue = if (isSetupMode) {
            appState.hasMasterPassword && appState.isUnlocked
        } else {
            appState.isUnlocked
        }

        if (shouldContinue) {
            onAuthenticated()
        }
    }

    Scaffold { innerPadding ->
        AuthContent(
            isSetupMode = isSetupMode,
            password = password,
            confirmation = confirmation,
            appState = appState,
            innerPadding = innerPadding,
            onPasswordChange = {
                onClearMessage()
                password = it
            },
            onConfirmationChange = {
                onClearMessage()
                confirmation = it
            },
            onSubmit = { onSubmit(password, confirmation) },
        )
    }
}

@Composable
private fun AuthContent(
    isSetupMode: Boolean,
    password: String,
    confirmation: String,
    appState: AppUiState,
    innerPadding: PaddingValues,
    onPasswordChange: (String) -> Unit,
    onConfirmationChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = if (isSetupMode) "Create your master password" else "Unlock VaultKeeper",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = if (isSetupMode) {
                "Your entries stay local and encrypted. Use a password you can remember."
            } else {
                "Enter the master password to decrypt and manage your local vault."
            },
            style = MaterialTheme.typography.bodyLarge,
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(if (isSetupMode) "setupPasswordField" else "unlockPasswordField"),
            label = { Text("Master password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
        )

        if (isSetupMode) {
            OutlinedTextField(
                value = confirmation,
                onValueChange = onConfirmationChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setupConfirmPasswordField"),
                label = { Text("Confirm master password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
            )
        }

        appState.errorMessage?.let { message ->
            StatusMessage(message = message)
        }

        Button(
            onClick = onSubmit,
            enabled = !appState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(if (isSetupMode) "setupSubmitButton" else "unlockSubmitButton"),
        ) {
            if (appState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(if (isSetupMode) "Create vault" else "Unlock")
            }
        }
    }
}
