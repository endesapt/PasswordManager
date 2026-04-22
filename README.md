# Project Name
VaultKeeper

## Description
VaultKeeper is a local Android password manager built with Kotlin and Jetpack Compose. The app stores data in a local Room database, encrypts passwords with AES/GCM, and protects access with a master password derived through PBKDF2. Users can create, edit, search, and organize credentials, then run breach checks through the Have I Been Pwned range API without sending raw passwords. The project also includes test coverage, CI workflows, and publishable documentation for repository wiki and GitHub Pages.

## Installation
1. Install Android Studio with Android SDK and bundled JDK.
2. Clone the repository.
3. Open the project in Android Studio.
4. Verify that `local.properties` points to a valid Android SDK path.
5. Sync Gradle and run module `app` on an emulator or physical device.

Optional command-line test run (Windows):

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat testDebugUnitTest
```

## Usage
1. Start the app and create a master password on first launch.
2. Unlock the vault with the same master password.
3. Add entries with title, username, password, website, and notes.
4. Open entry details to copy credentials, edit an item, delete it, or run a breach check.
5. Use search and favorite marks to organize the vault list.

Screenshots and extended project documentation are published in `docs/` and deployed to GitHub Pages.

## Contributing
- **Ilya Gorokhov**: architecture, data layer (Room), security (master password + AES/GCM), HIBP integration.
- **Team member 2 (fill before submission)**: Compose UI, navigation, UI tests, project presentation.
- Workflow rules: issue-based planning, feature branches, pull requests, code review, squash merge to `main`.
