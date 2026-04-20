package com.example.passwordmanager.app

object AppRoute {
    const val Splash = "splash"
    const val Setup = "setup"
    const val Unlock = "unlock"
    const val Vault = "vault"
    const val Settings = "settings"
    const val EditPattern = "edit/{itemId}"
    const val DetailPattern = "detail/{itemId}"

    fun edit(itemId: Long? = null): String = "edit/${itemId ?: "new"}"

    fun detail(itemId: Long): String = "detail/$itemId"
}
