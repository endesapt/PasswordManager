# Additional Specification

## Constraints
- The application is local-first and does not provide cloud synchronization.
- UI layer must not access Room entities or cryptography primitives directly.
- The project should remain buildable with current stable Android Studio and Gradle versions.

## Security requirements
- Minimum master password length: 8 characters.
- Key derivation: `PBKDF2WithHmacSHA256` with stored salt and iteration count.
- Password encryption: `AES/GCM/NoPadding`.
- Only verifier metadata is stored for master password validation.
- HIBP checks must use k-anonymity range API without sending raw password.

## Reliability requirements
- Database, encryption, and network operations run off the main thread.
- Vault functions remain available offline (except breach checking).
- App must correctly handle empty state, invalid master password, and network failures.

## Quality requirements
- Business logic should be testable outside UI.
- CI pipeline should run unit tests on pull requests.
- Code changes should be delivered through reviewed pull requests.
