package com.example.passwordmanager.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLockVault: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = "VaultKeeper",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Local password storage with Room, AES/GCM encryption, and breach checking via Have I Been Pwned.",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "This screen is also a good place to link documentation, GitHub Pages, and presentation assets later in the project.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                onClick = onLockVault,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Outlined.Lock, contentDescription = null)
                Text(
                    text = "Lock vault",
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}
