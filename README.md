# Project Name
VaultKeeper

# Description
VaultKeeper is a local Android password manager built with Kotlin and Jetpack Compose. The app stores entries in a Room database, encrypts passwords with AES/GCM, protects access with a master password, and checks passwords against the Have I Been Pwned Pwned Passwords API. The project is designed for team development with GitHub Projects, pull requests, automated tests, GitHub Actions, and GitHub Pages documentation.

# Installation
1. Install Android Studio with Android SDK and the bundled JDK.
2. Clone the repository and open it in Android Studio.
3. Ensure `local.properties` points to a valid Android SDK.
4. Sync Gradle and run `app` on an emulator or Android device.
5. For command-line checks on Windows, run:

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat testDebugUnitTest
```

# Usage
1. Launch the app and create a master password on the first start.
2. Unlock the vault with the same master password.
3. Add entries with title, username, password, website, and notes.
4. Long-press an item in the vault to toggle favorite state.
5. Open an entry to copy credentials, edit it, delete it, or run a breach check.

Screenshots and extended guides are published in the `docs/` pages and can be deployed to GitHub Pages.

# Contributing
- Team member 1: data layer, Room, encryption, HIBP integration, unit tests, GitHub Actions.
- Team member 2: Compose UI, navigation, UI tests, GitHub Pages, documentation, presentation materials.
- Work should go through GitHub issues, feature branches, pull requests, and squash merges to `main`.
