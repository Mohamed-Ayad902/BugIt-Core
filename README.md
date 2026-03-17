# 🛡️ Bug It: Core Project A

A high performance, **multi module** core library providing the Domain and Data layers for the Bug It ecosystem. Built with a focus on **Offline First** reliability and app security.

## 🏗️ Multi Module Architecture
This project is split into two distinct Maven published modules to enforce physical separation of concerns: 

### 1. `core-contracts`
* **Abstractions**: Defines all interfaces for Network Providers, Image Hosting, and Issue Trackers (Google Sheets/Notion).
* **Pure Domain**: Contains high level business models and custom `BugItExceptions`.
* **Zero-Dependency**: Strictly independent of implementation libraries to prevent leakage.

### 2. `core`
* **Feature Logic**: Defines the `domain` package containing feature specific UseCases, repositories and data classes.
* **Data Layer**: Implements the repositories using the **Strategy Pattern** for Google Sheets and ImgBB.
* **Base Helpers**: Includes modular network wrappers and image compression utilities.]]

## 🛠️ Key Functionality & Requirements
* **Dynamic Daily Grouping**: Automatically segregates bug reports into daily exclusive Google Sheet tabs (e.g., `17-03-26`).
* **Dual Target Support**: Infrastructure ready for both **Google Sheets** and **Notion** connectors.
* **Image Hosting Orchestration**: Handles 3rd-party image hosting (ImgBB) and maps remote URLs to the tracking sheet.

## 🔐 Enterprise-Grade Security
To prevent unauthorized access and reverse engineering:
* **NDK Encryption**: Google Service Credentials and API Keys are stored in the **Native Layer (C++)**.
* **XOR Masking**: Sensitive strings are masked with unique keys per value.
* **Signature Verification**: The native library is cryptographically tied to the application's signature to prevent tampering.

## 🚀 Setup
1. Open the Core Project in Android Studio.
2. Publish both the 2 artifacts to your local machine:
   `./gradlew publishToMavenLocal` 
3. After syncing The **Application Project** it will automatically consume these via `mavenLocal()`.
