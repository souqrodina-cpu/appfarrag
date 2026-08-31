# Souq Rodina - Mobile Accounting Native Android Project
Architecture: Kotlin 2.0+ • Jetpack Compose Material 3 • Room SQLite Database • DataStore Preferences • Google Drive API

## How to Build Standalone APK:
1. Extract this zip file.
2. Open Android Studio (Ladybug, Koala, Iguana, or newer).
3. Click "Open..." and select this extracted directory.
4. Let Gradle sync and resolve dependencies.
5. To build Debug APK:
   Run in Terminal: `./gradlew assembleDebug`
   The generated APK will be at: `app/build/outputs/apk/debug/app-debug.apk`
6. To build Signed Release APK:
   Build -> Generate Signed Bundle / APK -> APK.

## Cloud CI/CD Automated Build:
- Push to GitHub repository.
- GitHub Actions automatically runs `.github/workflows/build-apk.yml` and compiles `app-debug.apk` ready for download from the Actions tab.

## Standalone Web-APK Direct Install:
- You can also open the live web URL in Chrome on any Android smartphone, tap menu (⋮) and select "Install App" or "Add to Home Screen" to install it immediately as an offline-ready standalone application!
