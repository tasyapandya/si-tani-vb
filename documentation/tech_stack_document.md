# SiTani - Tech Stack Document

This document explains, in plain language, the technology choices for the SiTani Android application (Smart Farming Assistant). Each section outlines the tools and libraries we use, and why they were chosen to meet the project’s goals.

## 1. Frontend Technologies

These are the tools that shape how the app looks and feels on a farmer’s device.

- **Java**  
  The main programming language for writing the app’s screens and logic. Java is stable, well-known, and fully supported by Android.

- **Android Studio**  
  The official development environment (IDE) for Android. It offers code completion, layout previews, and built-in emulators to speed up development.

- **AndroidX & Jetpack Components**  
  A set of modern libraries that simplify building robust Android apps:
  • **ViewBinding**: Lets us reference UI elements (buttons, text fields) safely, without extra code to find them.  
  • **Lifecycle** (inferred): Helps the app respond correctly when a user switches screens or the device sleeps.  
  • **WorkManager** (see later): Schedules background tasks reliably.

- **XML Layouts & Material Design**  
  We define screen layouts in XML files, using modern UI components (e.g., ConstraintLayout) that adapt to different screen sizes. Material styling gives a clean, familiar look and feel.

- **RecyclerView**  
  Displays lists of items—like a farmer’s daily tasks—in a way that recycles on-screen components for smooth scrolling and low memory use.

## 2. Backend Technologies

This section covers how data is stored, retrieved, and synchronized behind the scenes.

- **Room Database**  
  A local database layer built on SQLite. It stores the to-do list items securely on the device. Room ensures:
  • Safe, compile-time checks of SQL queries  
  • Simple mapping between database rows and Java objects  

- **SharedPreferences**  
  Lightweight key-value storage for simple data, such as keeping a user’s login session token between app launches.

- **Retrofit (with OkHttp & Gson)**  
  A type-safe network library for calling web services. In SiTani, Retrofit:
  • Fetches current weather data from the OpenWeather API  
  • Works with **OkHttp** for efficient HTTP connections  
  • Uses **Gson** to convert JSON responses into Java objects

- **WorkManager & AlarmManager**  
  Built-in Android components for scheduling tasks when the app isn’t running in front:
  • **WorkManager** handles flexible, guaranteed background work (e.g., syncing weather every few hours).  
  • **AlarmManager** can trigger precise, time-based reminders (e.g., “Water crops at 8 AM”).  
  Both feed into the Android **NotificationManager** to show alerts on the farmer’s device.

## 3. Infrastructure and Deployment

How the project is built, stored, and delivered to developers and (eventually) to end users.

- **Git & GitHub**  
  Version control system (Git) hosted on GitHub. Keeps track of every change, enables collaboration, and makes it easy to roll back if needed.

- **Gradle Build System**  
  Automates compiling code, managing library versions, running tests, and packaging the app into an installable APK.

- **Continuous Integration (CI)** (typical setup)  
  While not explicitly in the README, a common practice is to use GitHub Actions or another CI tool to automatically:
  • Build the app on every code change  
  • Run unit tests  
  • Alert the team if something breaks

- **Android Studio Emulators & Physical Devices**  
  For testing across different Android versions and screen sizes before release.

## 4. Third-Party Integrations

Services and APIs that extend SiTani’s functionality beyond what we build in-house.

- **OpenWeather API**  
  Provides real-time weather data (temperature, humidity, wind speed) that helps farmers plan tasks around changing conditions.

- **Android Notification System**  
  Leverages built-in Android notifications so farmers receive reminders even if the app is closed.

## 5. Security and Performance Considerations

Ensuring the app is safe to use and runs smoothly on a variety of devices.

- **Secure Data Storage**  
  • **Room** keeps task data on the user’s device rather than sending it over the network.  
  • **SharedPreferences** uses private mode so other apps can’t read session tokens.

- **Protected API Keys**  
  Store the OpenWeather key in Gradle’s `BuildConfig` (or another secure location) rather than hard-coding it in source files.

- **HTTPS Everywhere**  
  Retrofit and OkHttp communicate over HTTPS to encrypt weather requests and responses.

- **Efficient List Rendering**  
  RecyclerView recycles item views to reduce memory use and ensure smooth scrolling, even with many tasks.

- **Reliable Scheduling**  
  WorkManager automatically handles device restarts and network availability, making sure reminders and data updates happen as expected.

## 6. Conclusion and Overall Tech Stack Summary

SiTani combines proven Android tools and libraries to deliver a reliable, user-friendly farming assistant:

- Frontend: Java, Android Studio, XML layouts, RecyclerView, ViewBinding
- Backend: Room database, SharedPreferences, Retrofit + OkHttp + Gson, WorkManager/AlarmManager
- Infrastructure: Git/GitHub, Gradle, CI best practices
- Integrations: OpenWeather API, Android Notifications
- Security & Performance: Encrypted network calls, safe local storage, efficient UI components

Together, these choices ensure that SiTani is easy to use, secure, and responsive—helping farmers manage tasks and weather information without technical hurdles.