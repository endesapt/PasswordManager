# Additional Specification

## Constraints
- Offline-first app; no cloud sync.
- UI does not access database/crypto directly.

## Security requirements
- Minimum master password length: 8.
- PBKDF2 key derivation and AES/GCM encryption.
- Store verifier metadata only, never raw master password.
- Use HIBP range API to preserve password privacy.

## Reliability requirements
- Run DB and encryption operations off main thread.
- Keep vault available offline except breach checks.

## Quality requirements
- Unit tests for core business logic.
- Pull-request based workflow with CI checks.
