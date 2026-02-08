# MyReceipt

An eco-responsible, privacy-focused Android receipt scanner app built with Kotlin and Jetpack Compose.

![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat&logo=jetpackcompose&logoColor=white)

## Features

- **OCR Scanning** - Scan receipts using ML Kit (100% on-device processing)
- **Dashboard** - View spending analytics and insights
- **Privacy-First** - No cloud sync, no analytics, no tracking
- **Dark/Light Mode**
- **Offline Mode** - Works completely without internet

## Requirements

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK 17** (Eclipse Temurin recommended)
- **Android SDK** API 34 (Android 14)
- **Min SDK**: API 24 (Android 7.0)

## Setup & Build

### 1. Clone the Repository

```bash
git clone https://github.com/soufiane-elbarji/MyReceipt.git
cd MyReceipt
```

### 2. Configure Android SDK

Create or update `local.properties` in the project root:

```properties
sdk.dir=C:\\Android\\Sdk
```

> Replace with your actual Android SDK path.

### 3. Build the App

**Using Command Line:**

```bash
# Windows
.\gradlew.bat assembleDebug
```

**Using Android Studio:**
1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Click **Build > Build Bundle(s) / APK(s) > Build APK(s)**

### 4. Locate the APK

After a successful build, find the APK at:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Installation

The easiest way to get MyReceipt on your device is to download the latest stable APK from our GitHub Releases.

[![Download MyReceipt v1.2.8](https://img.shields.io/badge/Download-APK_v1.2.8-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/soufiane-elbarji/MyReceipt/releases/latest)

### How to Install
1. **Download:** Click the button above or visit the [Releases](https://github.com/soufiane-elbarji/MyReceipt/releases) page.
2. **Open APK:** Locate the downloaded `MyReceipt-v1.2.8.apk` in your downloads folder.
3. **Allow Installation:** If prompted, allow your browser or file manager to "Install unknown apps."
4. **Launch:** Open MyReceipt and start scanning!

---

### For Developers
If you'd rather build it yourself or use ADB:

**Via ADB (USB Debugging):**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Manual Installation:**

1. Transfer `app-debug.apk` to your Android device
2. Open the file on your device
3. Enable "Install from unknown sources" if prompted
4. Tap **Install**

## Project Structure

```
app/src/main/java/com/myreceipt/
├── data/
│   ├── local/          # Room database entities & DAOs
│   ├── preferences/    # DataStore theme preferences
│   └── repository/     # Data repository layer
├── domain/
│   └── analyzer/       # OCR text parsing logic
└── presentation/
    ├── navigation/     # Compose Navigation
    ├── ui/             # Screens & Components
    ├── theme/          # Material 3 theming
    └── viewmodel/      # ViewModels
```

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin 1.9.20 |
| UI | Jetpack Compose + Material 3 |
| Database | Room (SQLite) |
| OCR | ML Kit Text Recognition V2 |
| Camera | CameraX |
| DI | Manual (ViewModelFactory) |
| Async | Coroutines + Flow |

## Privacy Compliance

This app is designed with **Privacy by Design** principles (GDPR/Law 09-08 compliant):

- All OCR processing happens **on-device**
- Receipt images are **never** transmitted
- No Firebase, analytics, or crash reporting
- Data stored only in local SQLite database

## License

MIT License - See [LICENSE](LICENSE) for details.

