# File Diagram

```text
app/src/main/java/com/example/passwordmanager
|- app/            composition root and navigation
|- data/           Room, network client, repository
|- domain/         models and interfaces
|- security/       crypto and master password logic
`- ui/             Compose screens and ViewModels
```

## Description
- `app`: wires dependencies and routes.
- `data`: stores and transforms encrypted records.
- `domain`: shared contracts and entities.
- `security`: key derivation and AES/GCM operations.
- `ui`: user-facing screens and screen state management.
