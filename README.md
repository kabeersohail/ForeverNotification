# SmartAgent Android App

SmartAgent is an Android application designed to efficiently manage and download configuration data while providing continuous network monitoring. This README provides an overview of the app's architecture and key features.

## Features

- **Foreground Notification**
  - The app displays a permanent foreground notification to keep the service active and visible to the user.

- **Foreground Service**
  - A foreground service starts automatically when the app is launched and also upon device boot-up using the `BootCompleted` broadcast receiver.

- **Dependency Injection**
  - Hilt is used for dependency injection, ensuring efficient management and provisioning of dependencies.

- **MVVM Architecture**
  - SmartAgent follows the MVVM (Model-View-ViewModel) architectural pattern for clear separation of concerns and maintainability.

- **Repository**
  - The repository is responsible for fetching and storing the `ConfigData`.
  - `ConfigData` is stored in a Room Database for persistence.

- **Download Manager Module**
  - The download functionality is modularized into a separate `DownloadManager` module.
  - It downloads `ConfigData` and saves it to the app's external storage directory.

- **Download Handling**
  - The download module:
    - Fetches a list of `ConfigData` from the API response.
    - Downloads various file types, including images, videos, and HTML files.
    - Checks if files exist locally and compares their sizes before deciding to redownload.

- **Network Monitoring**
  - The app monitors the device's network status through Network Monitor, which converts callback functions into callback flows for a reactive approach.

- **Device Boot Handling**
  - The app ensures continuous operation by starting the service automatically when the device reboots.

- **Retrofit for API Calls**
  - Retrofit is used for making network requests and fetching remote configuration data.

## Minimum Requirements

- Android SDK: 28 (Android 9.0 and above)

## Getting Started

To build and run the SmartAgent app:

1. Clone this repository.

```bash
git clone https://github.com/kabeersohail/ForeverNotification.git
```

2. Open the project in Android Studio.

3. Build and run the app on an Android device or emulator.
