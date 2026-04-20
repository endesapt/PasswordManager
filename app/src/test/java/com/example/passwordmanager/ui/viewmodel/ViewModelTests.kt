package com.example.passwordmanager.ui.viewmodel

import com.example.passwordmanager.MainDispatcherRule
import com.example.passwordmanager.domain.model.BreachCheckResult
import com.example.passwordmanager.domain.model.VaultItem
import com.example.passwordmanager.domain.repository.VaultRepository
import com.example.passwordmanager.domain.service.BreachChecker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTests {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `vault list view model filters items by query`() = runTest {
        val repository = FakeVaultRepository(
            initialItems = listOf(
                VaultItem(id = 1, title = "GitHub", username = "octocat"),
                VaultItem(id = 2, title = "Mail", username = "user@example.com"),
            ),
        )
        val viewModel = VaultListViewModel(repository)

        viewModel.onQueryChange("git")
        advanceUntilIdle()

        val state = viewModel.state.first { !it.isLoading && it.query == "git" }
        assertEquals(1, state.items.size)
        assertEquals("GitHub", state.items.first().title)
    }

    @Test
    fun `entry editor saves valid item`() = runTest {
        val repository = FakeVaultRepository()
        val viewModel = EntryEditorViewModel(
            itemId = null,
            repository = repository,
            breachChecker = FakeBreachChecker(BreachCheckResult.Safe),
        )

        viewModel.onTitleChange("GitHub")
        viewModel.onPasswordChange("PlainPassword123")
        viewModel.save()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.saveCompleted)
        assertEquals("GitHub", repository.items.value.first().title)
    }

    @Test
    fun `entry editor reports compromised password`() = runTest {
        val viewModel = EntryEditorViewModel(
            itemId = null,
            repository = FakeVaultRepository(),
            breachChecker = FakeBreachChecker(BreachCheckResult(isCompromised = true, breachCount = 42)),
        )

        viewModel.onTitleChange("Mail")
        viewModel.onPasswordChange("123456")
        viewModel.checkPasswordBreach()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.breachStatusMessage!!.contains("42"))
    }

    @Test
    fun `entry details keeps breach result until cleared`() = runTest {
        val repository = FakeVaultRepository(
            initialItems = listOf(
                VaultItem(id = 7, title = "Mail", password = "123456"),
            ),
        )
        val viewModel = EntryDetailsViewModel(
            itemId = 7,
            repository = repository,
            breachChecker = FakeBreachChecker(BreachCheckResult(isCompromised = true, breachCount = 42)),
        )

        advanceUntilIdle()
        viewModel.checkPasswordBreach()
        advanceUntilIdle()

        assertTrue(viewModel.state.value.breachStatusMessage!!.contains("42"))
    }
}

private class FakeVaultRepository(
    initialItems: List<VaultItem> = emptyList(),
) : VaultRepository {
    val items = MutableStateFlow(initialItems)

    override fun observeVaultItems(): Flow<List<VaultItem>> = items

    override suspend fun getVaultItem(id: Long): VaultItem? = items.value.firstOrNull { it.id == id }

    override suspend fun upsertVaultItem(item: VaultItem): Long {
        val saved = if (item.id == 0L) {
            item.copy(id = (items.value.maxOfOrNull { it.id } ?: 0L) + 1)
        } else {
            item
        }
        items.value = items.value.filterNot { it.id == saved.id } + saved
        return saved.id
    }

    override suspend fun deleteVaultItem(id: Long) {
        items.value = items.value.filterNot { it.id == id }
    }
}

private class FakeBreachChecker(
    private val result: BreachCheckResult,
) : BreachChecker {
    override suspend fun checkPassword(password: String): Result<BreachCheckResult> {
        return Result.success(result)
    }
}
