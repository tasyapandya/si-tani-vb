# Project Requirements Document for SiTani - Smart Farming Assistant

## 1. Project Overview
SiTani is an Android mobile application designed to help small-scale and commercial farmers manage daily agricultural activities with ease. It combines a task-oriented to-do list, real-time weather data, and configurable reminders into a single, intuitive dashboard. By centralizing these core functions, SiTani aims to reduce manual record-keeping, prevent missed tasks, and improve overall farm productivity.

This app is being built to address the common pain points farmers face in staying organized and making weather-informed decisions. The key objectives for the first version are:

- Enable fast and secure sign-up/login and profile updates.
- Provide reliable local storage and management of daily tasks.
- Fetch and display current weather conditions via OpenWeather API.
- Schedule and deliver timely notifications to remind users of critical activities.
- Offer an at-a-glance home dashboard that brings all this information together.

Success will be measured by stable authentication flows, quick data access (to-do list and weather), on-time reminders, and positive user feedback on usability.

---

## 2. In-Scope vs. Out-of-Scope

**In-Scope (Version 1)**
- User authentication (registration, login, logout) and profile management (name, location).
- CRUD (Create, Read, Update, Delete) operations on a daily to-do list using Room (SQLite) local database.
- Integration with OpenWeather API via Retrofit for current weather data (temperature, humidity, wind speed).
- Configurable task reminders using Android WorkManager (preferred) or AlarmManager, with push notifications.
- Home dashboard screen combining upcoming tasks and current weather.
- XML-based layouts and RecyclerView for dynamic list display.

**Out-of-Scope (Later Phases)**
- Remote backend synchronization or cloud storage of tasks and profile.
- Advanced analytics or insights (e.g., planting recommendations, crop yield forecasts).
- Multi-language or extensive accessibility support beyond basic Android guidelines.
- Pagination or infinite scrolling (tasks assumed to remain manageable in number).
- Dependency Injection frameworks (Hilt/Koin) or formal architectural patterns (MVVM/MVI).
- Comprehensive automated testing suite (unit/UI) and CI/CD pipelines.

---

## 3. User Flow

When a new user opens SiTani for the first time, they land on a welcome screen prompting them to register or log in. After successful authentication, they are directed to the Home Dashboard. Here, they see today’s to-do list on the lower half and the current weather on top. A floating action button lets them add a new farming task.

To manage tasks, the user taps the to-do list section to view all tasks in a RecyclerView. They can tap any item to edit its details (title, description, date/time for reminder) or swipe to delete. Adding or editing a task schedules or updates a reminder in the background. Notifications appear at the specified times, even if the app is closed. The profile section is accessible via a menu icon, where users can update their name or location.

---

## 4. Core Features

- **Authentication & Profile Module**: Secure email/password sign-up, login, logout, and simple profile editing.
- **To-Do List (CRUD)**: Create, read, update, and delete daily farming tasks stored locally with Room.
- **Weather Integration**: Fetch and display real-time weather (temperature, humidity, wind speed) via Retrofit from OpenWeather API.
- **Reminders & Notifications**: Schedule background tasks with WorkManager or AlarmManager to trigger Android notifications for each to-do item.
- **Home Dashboard**: Single screen combining upcoming tasks list and live weather data for quick at-a-glance planning.
- **List Display**: Use RecyclerView for efficient rendering of the task list.

---

## 5. Tech Stack & Tools

- **Frontend / Platform**: Android (API level 23+), written in Java.
- **IDE**: Android Studio.
- **Local Database**: Room (abstraction layer over SQLite) for task persistence.
- **HTTP Client**: Retrofit for OpenWeather API calls.
- **Background Processing**: WorkManager (preferred) or AlarmManager for scheduling reminders.
- **UI Components**: XML layouts, RecyclerView, ViewBinding (for safer code-to-view mapping).
- **Notification**: Android Notification API.
- **Optional Future Tools**: Hilt/Koin (DI), Espresso (UI tests), JUnit/Mockito (unit tests).

---

## 6. Non-Functional Requirements

- **Performance**: 
  - App launch under 3 seconds.
  - To-do list load time ≤ 200 ms.
  - Weather fetch completes within 2 seconds under normal network.
- **Reliability**: 
  - Reminders fire within ±15 seconds of the scheduled time.
  - Handle network failures with user-friendly error messages and retry logic.
- **Security**: 
  - Store credentials securely (Android Keystore or encrypted SharedPreferences).
  - Use HTTPS for all API calls.
- **Usability**: 
  - Simple, farmer-friendly interface with clear labels and large touch targets.
  - Follow Android Material design guidelines.
- **Compliance**: 
  - Adhere to Google Play policies and Android 12+ notification permissions.

---

## 7. Constraints & Assumptions

- **Device Requirements**: Android 6.0 (API 23) or higher.
- **Network**: Internet required for weather updates; offline mode for task management.
- **API Key**: Valid OpenWeather API key available and stored securely (e.g., BuildConfig).
- **No Backend**: All data remains on device; no remote sync or user data sharing.
- **Permissions**: User grants notification and internet permissions.

---

## 8. Known Issues & Potential Pitfalls

- **API Rate Limits**: OpenWeather may throttle requests. Mitigation: cache last result, limit refresh frequency, exponential backoff on errors.
- **Background Restrictions**: Doze mode or battery optimizations may delay alarms. Mitigation: prefer WorkManager, which adapts to system constraints.
- **Data Migrations**: Future schema changes in Room must include proper migration paths to prevent data loss.
- **User Denies Permissions**: Handle missing notification or network access gracefully and guide users to enable them.
- **Error Handling**: Ensure all network calls, database operations, and user inputs are validated with clear feedback.

By adhering to these requirements, SiTani’s first version will deliver a clear, reliable, and farmer-friendly tool that streamlines daily agricultural management without ambiguity for future AI-powered technical documentation.