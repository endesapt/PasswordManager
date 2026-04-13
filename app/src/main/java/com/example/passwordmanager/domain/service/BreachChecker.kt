package com.example.passwordmanager.domain.service

import com.example.passwordmanager.domain.model.BreachCheckResult

interface BreachChecker {
    suspend fun checkPassword(password: String): Result<BreachCheckResult>
}
