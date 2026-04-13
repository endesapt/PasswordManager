package com.example.passwordmanager.data.network

import com.example.passwordmanager.domain.model.BreachCheckResult
import com.example.passwordmanager.domain.service.BreachChecker
import com.example.passwordmanager.security.CryptoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class HibpBreachChecker(
    private val cryptoManager: CryptoManager,
) : BreachChecker {
    override suspend fun checkPassword(password: String): Result<BreachCheckResult> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val sha1Hash = cryptoManager.sha1(password)
                val prefix = sha1Hash.take(5)
                val suffix = sha1Hash.drop(5)

                val connection = (URL("https://api.pwnedpasswords.com/range/$prefix").openConnection() as HttpURLConnection)
                connection.requestMethod = "GET"
                connection.setRequestProperty("Add-Padding", "true")
                connection.setRequestProperty("User-Agent", "VaultKeeper")
                connection.connectTimeout = 10_000
                connection.readTimeout = 10_000

                try {
                    check(connection.responseCode in 200..299) {
                        "HIBP request failed with code ${connection.responseCode}"
                    }

                    val body = connection.inputStream.bufferedReader().use { it.readText() }
                    val breachCount = body.lineSequence()
                        .mapNotNull { line ->
                            val parts = line.split(":")
                            if (parts.size != 2) return@mapNotNull null
                            if (parts[0].trim() != suffix) return@mapNotNull null
                            parts[1].trim().toIntOrNull()
                        }
                        .firstOrNull()
                        ?: 0

                    BreachCheckResult(
                        isCompromised = breachCount > 0,
                        breachCount = breachCount,
                    )
                } finally {
                    connection.disconnect()
                }
            }
        }
    }
}
