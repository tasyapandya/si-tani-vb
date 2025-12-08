# Backend Structure Document for SiTani

This document describes how the SiTani Android app handles its “backend” needs—namely local data storage and communication with external services—so that anyone can understand how data is managed, where it lives, and how the pieces fit together.

## 1. Backend Architecture

SiTani’s backend lives entirely inside the mobile app. It consists of three main layers:

- Data Layer
  - Room (SQLite) for storing tasks and related metadata on the device.
  - SharedPreferences for session tokens and simple user settings.
- Network Layer
  - Retrofit + OkHttp for calling the OpenWeather API over HTTPS.
- Scheduling Layer
  - WorkManager (preferred) or AlarmManager for scheduling reminders and notifications.

Design Patterns and Frameworks

- Repository Pattern: A `TaskRepository` mediates between the Room database and the UI, keeping data access code in one place.
- DAO (Data Access Object): Room generates DAOs to read and write the `tasks` table.
- MVVM (Model–View–ViewModel) (inferred): ViewModels hold UI data, call into repositories, and expose LiveData streams to Activities/Fragments.

How This Supports Scalability, Maintainability, and Performance

- Scalability: By using Room, the app can handle large numbers of tasks without slowing down the UI.
- Maintainability: Clear separation (Repository → DAO → Database) makes it easy to update or replace one piece without touching the others.
- Performance: SQLite’s on-device queries are fast, and AlarmManager/WorkManager runs reminders off the main thread.

## 2. Database Management

Technologies Used

- SQL Database: SQLite via the Android Room library.
- Key Benefits of Room:
  - Compile-time query validation.
  - Automatic mapping between database rows and Java objects.
  - Built-in support for database migrations.

Data Storage and Access

- Entities define table structure (e.g., `TaskEntity`).
- DAOs expose methods like `insertTask()`, `getAllTasks()`, `updateTask()`, `deleteTask()`.
- The `AppDatabase` class bundles entities and DAOs into a single database instance.
- SharedPreferences store small pieces of data (e.g., authentication tokens, user locale).

Data Management Practices

- Single Instance: The Room database is a singleton to prevent multiple connections.
- Migrations: Versioned migration scripts ensure data is preserved when the schema changes.
- Asynchronous Access: All DAO methods run off the main thread, using Kotlin Coroutines or RxJava (if available) to avoid UI freezes.

## 3. Database Schema

Human-Readable Format

- **Task**
  - **ID**: Unique identifier (auto-generated integer).
  - **Title**: Short description of the farming task.
  - **Description**: Longer notes about the task (optional).
  - **Due Date**: Timestamp for when the task should be completed.
  - **Reminder Time**: Timestamp for when to trigger a notification.
  - **Is Completed**: Boolean flag.

SQL (Room/SQLite) Schema Definition

```sql
CREATE TABLE tasks (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  title TEXT NOT NULL,
  description TEXT,
  due_date INTEGER NOT NULL,
  reminder_time INTEGER,
  is_completed INTEGER NOT NULL DEFAULT 0
);
```

## 4. API Design and Endpoints

SiTani uses a RESTful approach to fetch weather data. There is no custom server API; instead it relies on the OpenWeather service.

Key Endpoint(s)

- **Get Current Weather**
  - URL: `https://api.openweathermap.org/data/2.5/weather`
  - Method: `GET`
  - Query Parameters:
    - `lat` (latitude of the farm location)
    - `lon` (longitude of the farm location)
    - `appid` (API key)
    - `units` (e.g., `metric`)
  - Response: JSON object containing temperature, humidity, wind speed, weather description.

How It Works

- Retrofit interface defines the endpoint with a `@GET` annotation.
- The network call runs asynchronously and updates a LiveData or callback with the parsed `WeatherResponse` object.
- The Home Dashboard observes that data and refreshes the UI accordingly.

## 5. Hosting Solutions

There is no custom backend server to host—everything runs on the user’s device. For weather data, the app depends on the OpenWeather cloud service.

Benefits of This Approach

- **Reliability**: Local data is available offline; weather calls will fail gracefully if there is no internet.
- **Scalability**: No server maintenance or scaling needed; each installation is self-contained.
- **Cost-Effectiveness**: No hosting fees for custom servers; only the cost of the OpenWeather API (free tier or paid plans).

## 6. Infrastructure Components

Even without a traditional server, SiTani uses several key components to provide a smooth experience:

- **WorkManager / AlarmManager**: Ensures reminders fire at the correct time, even if the app is killed or the device is rebooted.
- **OkHttp Caching**: Retrofit’s OkHttp client can cache responses to reduce network usage and speed up repeated calls.
- **Android Notification Manager**: Delivers local notifications for scheduled tasks.

How They Work Together

1. User schedules a reminder in the to-do list UI.  
2. The app’s scheduling layer registers a WorkManager task or AlarmManager alarm.  
3. At the reminder time, the alarm triggers and the Notification Manager displays an alert.  
4. Meanwhile, the network layer can refresh weather data on app launch or on a schedule.

## 7. Security Measures

- **HTTPS Everywhere**: All network requests to OpenWeather use HTTPS, ensuring data in transit is encrypted.
- **API Key Management**: The OpenWeather API key is stored in `BuildConfig` or encrypted at build time, not hard-coded in source files.
- **Local Data Protection**: Sensitive user settings and tokens in SharedPreferences can be wrapped with Android’s EncryptedSharedPreferences.
- **Least Privilege**: The app requests only necessary permissions (e.g., Internet, device wake lock for alarms).

## 8. Monitoring and Maintenance

- **Crash Reporting**: Integrate a service like Firebase Crashlytics to capture uncaught exceptions and track stability issues.
- **Logging**: Use Android’s Logcat for debug-level logs, and consider a structured logger (Timber) for production.
- **Analytics**: Optional integration with Google Analytics or Firebase Analytics to track user engagement (e.g., how often reminders fire).
- **Database Migrations**: Write and test migration scripts whenever the Room schema changes.
- **Dependency Updates**: Periodically update Retrofit, Room, and other libraries to benefit from security patches and performance improvements.

## 9. Conclusion and Overall Backend Summary

SiTani’s backend is a light, on-device solution complemented by a reliable third-party weather API. By using Room for local storage and Retrofit for network calls, the app remains fast, responsive, and functional even in areas with limited connectivity. Scheduled reminders run off the main thread thanks to WorkManager or AlarmManager, ensuring farmers never miss a critical task.

Key Takeaways

- There is no separate server to manage—everything is bundled into the Android app.
- Local SQL storage (Room) and local notifications form the core of data persistence and user alerts.
- The only external dependency is the OpenWeather cloud service, accessed securely over HTTPS.

This setup keeps costs low, simplifies maintenance, and delivers a robust experience tailored to farmers’ needs.