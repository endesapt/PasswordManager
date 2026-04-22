# Functional Requirements

## Functional requirements list
- Create and confirm a master password on first launch.
- Unlock the vault using the master password.
- Create, view, edit, and delete password entries.
- Search entries by title, username, or website.
- Mark entries as favorites.
- Copy username and password from the details screen.
- Check password breach status through Have I Been Pwned (k-anonymity API).
- Lock the vault manually from settings.

## Use case diagram

![Use case diagram](assets/use-case-diagram.svg)

## Text scenarios

### UC-01: Create vault
1. User opens the app for the first time.
2. App navigates to setup screen.
3. User enters and confirms master password.
4. App derives a key, stores only verifier metadata, and unlocks the vault session.

### UC-02: Add entry
1. User opens vault list.
2. User taps add action.
3. User fills title, username, password, website, and notes.
4. App encrypts password and stores entry in Room.

### UC-03: Check breach status
1. User opens entry details or editor.
2. User starts breach check.
3. App computes SHA-1 hash and sends only the first 5 characters to HIBP range API.
4. App compares suffix list locally and returns breach result.

### UC-04: Edit or delete entry
1. User opens entry details.
2. User selects edit or delete.
3. App updates or removes item in local storage.
4. Vault list is refreshed.
