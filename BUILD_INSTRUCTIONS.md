# Building OmniMind Pro for Android

This project is set up with Apache Cordova for creating an Android APK.

## Prerequisites

1.  **Node.js**: Install from [nodejs.org](https://nodejs.org/).
2.  **Cordova**: Open a terminal and run `npm install -g cordova`.
3.  **Android Studio**: Download and install [Android Studio](https://developer.android.com/studio).
    *   Ensure the "Android SDK Platform-Tools" and "Android SDK Build-Tools" are installed via the SDK Manager.
    *   Set your `ANDROID_HOME` environment variable (e.g., `%LOCALAPPDATA%\Android\Sdk` on Windows or `~/Library/Android/sdk` on Mac).

## Build Instructions

1.  Open your terminal and navigate to the `android/` folder in this repository:
    ```bash
    cd android
    ```

2.  Add the Android platform (if not already added):
    ```bash
    cordova platform add android
    ```

3.  Build the APK:
    ```bash
    cordova build android
    ```

4.  **Locate the APK**:
    Once the build finishes, the APK will be located at:
    `android/platforms/android/app/build/outputs/apk/debug/app-debug.apk`

5.  **Install on Phone**:
    *   Connect your Android phone via USB.
    *   Enable "Developer Options" and "USB Debugging" on your phone.
    *   Run:
        ```bash
        cordova run android
        ```

## Customization

*   **Icon**: Replace the default icons in `android/www/` (or configure specific resources in `config.xml`).
*   **Splash Screen**: Add splash screen resources to `config.xml` if desired.

## Troubleshooting

*   **Gradle Errors**: If you see errors about Gradle versions, open `android/platforms/android` in Android Studio. It will often auto-fix Gradle wrapper version mismatches.
