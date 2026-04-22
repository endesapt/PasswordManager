# Database Schema

## Schema image

![Database schema](assets/database-schema.svg)

## SQL schema file
- [schema.sql](database/schema.sql)

## Schema description
- `security_meta`: master-password verification metadata (salt, verifier, iterations, timestamps).
- `vault_items`: vault entry metadata and encrypted password payload.

## Notes
- `encryptedPassword` stores `nonce + ciphertext` in Base64.
- Master password is never stored in plaintext.
- Room export snapshot is available in `app/schemas/com.example.passwordmanager.data.local.PasswordManagerDatabase/1.json`.
