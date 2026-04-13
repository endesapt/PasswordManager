package com.example.passwordmanager.app

import android.content.Context
import androidx.room.Room
import com.example.passwordmanager.data.local.PasswordManagerDatabase
import com.example.passwordmanager.data.network.HibpBreachChecker
import com.example.passwordmanager.data.repository.DefaultVaultRepository
import com.example.passwordmanager.domain.repository.VaultRepository
import com.example.passwordmanager.domain.service.BreachChecker
import com.example.passwordmanager.security.CryptoManager
import com.example.passwordmanager.security.DefaultMasterPasswordManager
import com.example.passwordmanager.security.MasterPasswordManager

class AppContainer(context: Context) {
    private val applicationContext = context.applicationContext

    private val database: PasswordManagerDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            PasswordManagerDatabase::class.java,
            "password-manager.db",
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    val cryptoManager: CryptoManager by lazy { CryptoManager() }

    val masterPasswordManager: MasterPasswordManager by lazy {
        DefaultMasterPasswordManager(
            securityMetaDao = database.securityMetaDao(),
            cryptoManager = cryptoManager,
        )
    }

    val breachChecker: BreachChecker by lazy {
        HibpBreachChecker(
            cryptoManager = cryptoManager,
        )
    }

    val vaultRepository: VaultRepository by lazy {
        DefaultVaultRepository(
            vaultItemDao = database.vaultItemDao(),
            masterPasswordManager = masterPasswordManager,
            cryptoManager = cryptoManager,
        )
    }
}
