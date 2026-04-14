package com.example.passwordmanager.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.passwordmanager.ui.screen.EntryDetailsRoute
import com.example.passwordmanager.ui.screen.EntryEditorRoute
import com.example.passwordmanager.ui.screen.SettingsScreen
import com.example.passwordmanager.ui.screen.SplashScreen
import com.example.passwordmanager.ui.screen.VaultAuthScreen
import com.example.passwordmanager.ui.screen.VaultListRoute
import com.example.passwordmanager.ui.viewmodel.AppViewModel

@Composable
fun PasswordManagerApp(container: AppContainer) {
    val navController = rememberNavController()
    val appViewModel: AppViewModel = viewModel(factory = AppViewModel.Factory(container))
    val appState by appViewModel.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash,
    ) {
        composable(AppRoute.Splash) {
            SplashScreen()

            LaunchedEffect(appState.isLoading, appState.hasMasterPassword, appState.isUnlocked) {
                if (appState.isLoading) return@LaunchedEffect

                val destination = when {
                    !appState.hasMasterPassword -> AppRoute.Setup
                    !appState.isUnlocked -> AppRoute.Unlock
                    else -> AppRoute.Vault
                }

                navController.navigate(destination) {
                    popUpTo(AppRoute.Splash) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable(AppRoute.Setup) {
            VaultAuthScreen(
                isSetupMode = true,
                appState = appState,
                onSubmit = appViewModel::setupMasterPassword,
                onClearMessage = appViewModel::clearError,
            ) {
                navController.navigate(AppRoute.Vault) {
                    popUpTo(AppRoute.Setup) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable(AppRoute.Unlock) {
            VaultAuthScreen(
                isSetupMode = false,
                appState = appState,
                onSubmit = { password, _ -> appViewModel.unlock(password) },
                onClearMessage = appViewModel::clearError,
            ) {
                navController.navigate(AppRoute.Vault) {
                    popUpTo(AppRoute.Unlock) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable(AppRoute.Vault) {
            VaultListRoute(
                container = container,
                onOpenSettings = { navController.navigate(AppRoute.Settings) },
                onOpenItem = { itemId -> navController.navigate(AppRoute.detail(itemId)) },
                onCreateItem = { navController.navigate(AppRoute.edit()) },
            )
        }

        composable(
            route = AppRoute.DetailPattern,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType }),
        ) { backStackEntry ->
            EntryDetailsRoute(
                container = container,
                itemId = backStackEntry.arguments?.getLong("itemId") ?: 0L,
                onBack = navController::popBackStack,
                onEdit = { itemId -> navController.navigate(AppRoute.edit(itemId)) },
                onDeleted = {
                    navController.navigate(AppRoute.Vault) {
                        popUpTo(AppRoute.Vault) { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(
            route = AppRoute.EditPattern,
            arguments = listOf(navArgument("itemId") {
                type = NavType.StringType
                defaultValue = "new"
            }),
        ) { backStackEntry ->
            val rawItemId = backStackEntry.arguments?.getString("itemId")
            EntryEditorRoute(
                container = container,
                itemId = rawItemId?.toLongOrNull(),
                onBack = navController::popBackStack,
                onSaved = {
                    navController.navigate(AppRoute.Vault) {
                        popUpTo(AppRoute.Vault) { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(AppRoute.Settings) {
            SettingsScreen(
                onBack = navController::popBackStack,
                onLockVault = {
                    appViewModel.lock()
                    navController.navigate(AppRoute.Unlock) {
                        popUpTo(AppRoute.Vault) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
