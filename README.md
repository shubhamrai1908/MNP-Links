# MNP Links

A lightweight Android app for opening and managing mobile-number-portability websites.

## Configure links

Open the app and tap **Manage**. Add, edit, or remove entries by changing their display name and HTTPS URL. Changes are saved locally on the device; they do not require a server or an account. **Restore default links** brings back the original three destinations.

Only HTTPS links are accepted. The in-app browser uses Android's normal TLS certificate validation, blocks insecure HTTP navigation, and does not allow web pages to access local files.

The browser also blocks requests to a small built-in list of common third-party advertising domains. This is best-effort filtering: it does not block all ads, including ads served by the site itself, and may not catch newly introduced ad networks.

## Sign-in and saved passwords

The app does not collect or store website passwords. On Android 8 (API 26) and newer, its WebView is exposed to Android Autofill so the device's selected autofill service or password manager can offer to save and fill login details. Enable a preferred service in Android Settings under Passwords, passkeys, and accounts (the exact menu name varies by device). Autofill availability depends on the Android version, WebView version, selected service, and whether the website exposes recognizable login fields.

## Build

Open this folder in Android Studio and run the `app` configuration, or build the debug APK with:

```powershell
.\gradlew.bat assembleDebug
```

The debug APK is written to `app\build\outputs\apk\debug\app-debug.apk`.

## Choosing where link settings live

- **Per-device settings (implemented):** simplest and safest for a small app; users manage links directly, and no server is needed.
- **A bundled configuration file:** useful when one maintainer changes links and publishes an app update; users cannot change the list without updating.
- **A hosted configuration service:** useful when all users should receive link updates without an app release. Add an authenticated/admin-controlled HTTPS endpoint, validate its response, and define an offline/cache policy. Avoid trusting arbitrary remote URLs or allowing remote configuration to disable HTTPS checks.

The three defaults are inherited from the supplied APK. Verify that each destination is still official and appropriate before distributing the app.
