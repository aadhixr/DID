# DiskWala Reborn (Personal Streaming & Download Client)

A personal, ad-free recreation of DiskWala built with modern Android development standards.

## Architecture & Features

- **Jetpack Compose UI**: Modern Material 3 interface with Dark Mode styling.
- **Navigation**:
  - `DramaScreen`: Catalog and episode list for video content.
  - `DownloadsScreen`: Track and play offline media saved on disk.
  - `YouScreen`: Local watch history and file retention.
  - `CustomPlayerScreen`: Clean media player without ads, overlays, or popups.
- **Media3 ExoPlayer + RefreshingPlayerEngine**:
  - Automatically intercepts HTTP 401/403 token expiration codes.
  - Re-fetches fresh stream URLs in background and resumes from the exact millisecond.
- **Room Database (3 Offline Tables)**:
  - `downloads`: Downloaded file entities with paths, sizes, and MIME types.
  - `recent_files`: User viewing history.
  - `recent_creators`: Watched creators.
- **Download Foreground Service**:
  - Multi-threaded buffered download worker with active Android notification channel progress reporting.
- **Networking**:
  - Ktor Client with OkHttp backend engine and JSON serialization.

## Building and Running

1. Open the project folder (`C:\Users\aadhi\Downloads\DISK\DiskWalaReborn`) in **Android Studio**.
2. Let Gradle sync dependencies.
3. Run on an Android device or emulator with API 26+ (Android 8.0+).
