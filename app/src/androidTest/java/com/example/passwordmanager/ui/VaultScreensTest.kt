package com.example.passwordmanager.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.passwordmanager.domain.model.VaultItem
import com.example.passwordmanager.ui.screen.EntryEditorScreen
import com.example.passwordmanager.ui.screen.VaultAuthScreen
import com.example.passwordmanager.ui.screen.VaultListScreen
import com.example.passwordmanager.ui.theme.PasswordManagerTheme
import com.example.passwordmanager.ui.viewmodel.AppUiState
import com.example.passwordmanager.ui.viewmodel.EntryEditorUiState
import com.example.passwordmanager.ui.viewmodel.VaultListUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class VaultScreensTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun setupScreenShowsConfirmationField() {
        composeRule.setContent {
            PasswordManagerTheme {
                VaultAuthScreen(
                    isSetupMode = true,
                    appState = AppUiState(isLoading = false),
                    onSubmit = { _, _ -> },
                    onClearMessage = {},
                    onAuthenticated = {},
                )
            }
        }

        composeRule.onNodeWithTag("setupPasswordField").assertIsDisplayed()
        composeRule.onNodeWithTag("setupConfirmPasswordField").assertIsDisplayed()
        composeRule.onNodeWithTag("setupSubmitButton").assertIsDisplayed()
    }

    @Test
    fun vaultListShowsItemsAndFab() {
        composeRule.setContent {
            PasswordManagerTheme {
                VaultListScreen(
                    state = VaultListUiState(
                        isLoading = false,
                        items = listOf(VaultItem(id = 1, title = "GitHub", username = "octocat")),
                    ),
                    onQueryChange = {},
                    onToggleFavorite = {},
                    onOpenSettings = {},
                    onOpenItem = {},
                    onCreateItem = {},
                    onClearError = {},
                )
            }
        }

        composeRule.onNodeWithText("GitHub").assertIsDisplayed()
        composeRule.onNodeWithTag("addEntryButton").assertIsDisplayed()
    }

    @Test
    fun entryEditorInvokesSaveCallback() {
        var savePressed = false

        composeRule.setContent {
            var state by remember {
                mutableStateOf(EntryEditorUiState(isLoading = false))
            }

            PasswordManagerTheme {
                EntryEditorScreen(
                    state = state,
                    onBack = {},
                    onSaved = {},
                    onTitleChange = { state = state.copy(title = it) },
                    onUsernameChange = { state = state.copy(username = it) },
                    onPasswordChange = { state = state.copy(password = it) },
                    onWebsiteChange = { state = state.copy(website = it) },
                    onNotesChange = { state = state.copy(notes = it) },
                    onFavoriteChange = { state = state.copy(isFavorite = it) },
                    onSave = { savePressed = true },
                    onCheckPasswordBreach = {},
                    onClearTransientState = {},
                )
            }
        }

        composeRule.onNodeWithTag("entryTitleField").performTextInput("Mail")
        composeRule.onNodeWithTag("entryPasswordField").performTextInput("secret-password")
        composeRule.onNodeWithTag("saveEntryButton").performClick()

        assertTrue(savePressed)
    }
}
