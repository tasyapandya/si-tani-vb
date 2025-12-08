# Frontend Guideline Document for SiTani (Smart Farming Assistant)

This document provides a clear, high-level overview of the frontend setup for the SiTani Android application. It’s written in everyday language so that anyone—whether technical or not—can understand how the app is built, styled, and maintained.

## 1. Frontend Architecture

### Frameworks and Libraries Used
- **Programming Language:** Java (the main language for all screens and logic).  
- **Android SDK & AndroidX (Jetpack) Components:**  
  • Room Database (local data storage)  
  • WorkManager (reliable background tasks)  
  • ViewBinding (type-safe access to UI elements)  
  • RecyclerView (efficient lists)  
- **Networking:** Retrofit (type-safe HTTP calls) with OkHttp under the hood.  
- **Background Scheduling:** AlarmManager or WorkManager (for reminders and notifications).  

### How It’s Organized
- **UI Layer:** Activities (for full-screen flows) and optional Fragments (for reusable parts of a screen).  
- **Data Layer:**  
  • **Room Entities & DAOs** handle all local database operations (tasks, user info).  
  • **Retrofit Service Interfaces** fetch weather data from OpenWeather API.  
- **Helper Packages:** Adapters (for RecyclerView), Utils (date formatting, notification helpers), Services or BroadcastReceivers (for scheduled alarms).

### Supporting Scalability, Maintainability, and Performance
- **Separation of Concerns:** Each feature (authentication, to-do list, weather, reminders) lives in its own package. This makes it easy to find and update code.  
- **Modular Design:** Clear boundaries between UI, data, and background tasks help multiple developers work in parallel without conflicts.  
- **Efficient Data Handling:** Room and Retrofit both run on background threads, keeping the app snappy and responsive.

---

## 2. Design Principles

### Key Principles
1. **Usability:** Simple screens and clear labels guide farmers through logging in, viewing tasks, and checking the weather.  
2. **Accessibility:** Meaningful content descriptions for icons, sufficient color contrast, and support for larger text sizes help a wide range of users.  
3. **Responsiveness:** Layouts adjust to different screen sizes (phones, tablets) using ConstraintLayout and flexible resources.

### How They’re Applied
- **Consistent Navigation:** A bottom or top bar (depending on design) lets users move between Home, Tasks, Profile, and Settings.  
- **Clear Feedback:** Buttons change color on tap, and form errors are shown inline so users know exactly what to fix.  
- **Readable Fonts & Icons:** Large touch targets and legible text help in outdoor or bright-light conditions.

---

## 3. Styling and Theming

### Styling Approach
- **Material-inspired, Flat Design:** Clean lines, simple shapes, and minimal shadows keep the interface modern and uncluttered.  
- **XML Styles & Themes:** All colors, text sizes, and spacing live in `res/values/styles.xml` and `colors.xml` for easy updates.

### Color Palette
- **Primary:** Green (#4CAF50) – represents growth and agriculture.  
- **Secondary:** Brown (#795548) – earthy tone for accents.  
- **Accent:** Yellow (#FFEB3B) – highlights important actions or warnings.  
- **Background:** White (#FFFFFF) – clean canvas for data.  
- **Text:** Dark Gray (#212121) – strong contrast for readability.

### Typography
- **Font Family:** Roboto (Android’s default, clean and versatile).  
- **Headings:** Medium weight, 20–24sp.  
- **Body Text:** Regular weight, 14–16sp.

### Theming
- **Light Theme by Default:** Clear backgrounds and high contrast for outdoor use.  
- **Optional Dark Theme:** Can be added later by inverting colors in `themes.xml`.

---

## 4. Component Structure

### How Components Are Organized
- **activities/** — All full-screen Activities (Login, Register, Home, TaskList, Profile).  
- **fragments/** — Reusable UI pieces if the app moves to a single-activity model.  
- **data/model/** — Plain Java objects representing User, Task, Weather.  
- **data/database/** — Room Entities, DAOs, and AppDatabase.  
- **data/remote/** — Retrofit interfaces and response models.  
- **adapters/** — RecyclerView adapters for showing lists of tasks.  
- **utils/** — Helper classes for dates, notifications, and common logic.  
- **services/** — BroadcastReceivers or Worker classes for scheduled reminders.

### Benefits of Component-Based Design
- **Reusability:** Widgets like a Task Card or Weather Display can be used in multiple screens.  
- **Maintainability:** Changing one component (e.g., how tasks look) automatically updates every place it’s used.  
- **Isolation:** Bugs are easier to track down when code responsibilities are well separated.

---

## 5. State Management

### How State Is Handled Today
1. **Persistent State:** Room Database holds all saved tasks and user data.  
2. **Session State:** SharedPreferences stores login tokens and basic user preferences.  
3. **In-Memory State:** Activity and Fragment variables track the current screen’s data (e.g., the task being edited).

### Sharing State Across Screens
- **Intents & Extras:** Data passed between Activities when users navigate (e.g., passing a Task ID to EditTaskActivity).  
- **DAO Queries:** Each screen fetches fresh data from Room, ensuring consistency.

(As the app grows, adopting Android ViewModel + LiveData can further simplify state handling and UI updates.)

---

## 6. Routing and Navigation

### How Users Move Around
- **Explicit Intents:** Each button or menu item calls `startActivity()` with the target Activity class.  
- **Back Stack Management:** Android’s default back button behavior handles navigation history automatically.  
- **Manifest Declarations:** Every Activity is declared in `AndroidManifest.xml` with its own `<activity>` tag.

### Future Navigation Options
- **AndroidX Navigation Component:** A single graph to visualize all screens and transitions, reducing boilerplate code.

---

## 7. Performance Optimization

### Current Strategies
- **RecyclerView with ViewHolder Pattern:** Reuses list item views for smooth scrolling through long task lists.  
- **Background Threads for Disk & Network:** Room and Retrofit calls run off the main thread to keep the UI snappy.  
- **WorkManager:** Bundles background work (like sending reminders) into efficient, battery-friendly jobs.  
- **Optimized Assets:** Use of vector drawables instead of large image files where possible.

### Additional Tips
- **Code Splitting:** Keep modules small to reduce APK size.  
- **Image Compression:** If you add photos later, compress them or load with a library like Glide.

---

## 8. Testing and Quality Assurance

### Recommended Testing Strategies
1. **Unit Tests (JUnit):** Test individual pieces of logic, such as Task DAO methods or date formatting functions.  
2. **Integration Tests:** Verify that Room and Retrofit work together as expected (e.g., saving and then fetching data).  
3. **UI Tests (Espresso):** Automate common user flows—logging in, adding a task, checking the weather—to catch regressions.

### Tools and Frameworks
- **JUnit & AndroidX Test** for unit and integration tests.  
- **Espresso** for on-device UI tests.  
- **Mockito** for mocking dependencies (e.g., simulating API responses).  
- **Android Lint & StrictMode:** Catch common mistakes and enforce coding standards.

---

## 9. Conclusion and Overall Frontend Summary

SiTani’s frontend is a straightforward, well-organized Android application that helps farmers manage tasks and check weather in one place. By combining Java, AndroidX components (Room, WorkManager, ViewBinding), and Retrofit, the app strikes a balance between performance and maintainability.

Key takeaways:
- **Modular Architecture:** Clear separation between UI, data, and background processing.  
- **User-Centered Design:** Simple, accessible screens with a Material-inspired look and farm-friendly color palette.  
- **Scalable Practices:** Room for offline data, Retrofit for network calls, RecyclerView for efficient lists.  
- **Quality Focus:** Background tasks, error handling hooks, and recommended testing strategies lay the groundwork for a robust product.

With these guidelines, anyone joining the SiTani project will have a solid understanding of how the frontend is built, styled, and optimized. Happy coding!