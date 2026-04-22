# File Diagram

## Application file diagram

```text
app/src/main/java/com/example/passwordmanager
|- app/
|  |- AppContainer.kt
|  |- AppRoute.kt
|  `- PasswordManagerApp.kt
|- data/
|  |- local/
|  |  |- dao/
|  |  |- entity/
|  |  `- PasswordManagerDatabase.kt
|  |- network/
|  |  `- HibpBreachChecker.kt
|  `- repository/
|     `- DefaultVaultRepository.kt
|- domain/
|  |- model/
|  |- repository/
|  `- service/
|- security/
|  |- CryptoManager.kt
|  |- DefaultMasterPasswordManager.kt
|  `- MasterPasswordManager.kt
`- ui/
   |- components/
   |- screen/
   |- theme/
   `- viewmodel/
```

## Diagram description
- `app`: composition root and navigation graph.
- `data/local`: Room database, entities, DAO.
- `data/network`: external API integration for breach checking.
- `data/repository`: data orchestration with encryption/decryption.
- `domain`: shared models and contracts.
- `security`: cryptography and master password lifecycle.
- `ui/viewmodel`: state and user actions.
- `ui/screen`: Compose presentation layer.
