package com.example.passwordmanager.domain.model

data class BreachCheckResult(
    val isCompromised: Boolean,
    val breachCount: Int,
) {
    companion object {
        val Safe = BreachCheckResult(
            isCompromised = false,
            breachCount = 0,
        )
    }
}
