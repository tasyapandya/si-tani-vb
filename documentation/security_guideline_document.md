# Security Guidelines for SiTani - Smart Farming Assistant

This document provides actionable security best practices and recommendations for the SiTani Android application. It aligns with industry standards to ensure confidentiality, integrity, and availability of user data and system components.

---

## 1. Authentication & Access Control

- **Secure Registration & Login**
  - Enforce strong password policies (minimum length, complexity) on the client side and re-validate server-side if applicable.
  - Store passwords hashed with a modern algorithm (Argon2 or bcrypt) and a unique salt for each user.
  - Use Android’s **EncryptedSharedPreferences** or **Android Keystore** to store session tokens, avoiding plaintext storage.
  - Implement session timeouts (idle and absolute) and provide a clear logout mechanism that clears all sensitive data.
- **Multi-Factor Authentication (MFA)**
  - Offer an optional second factor (e.g., TOTP via Google Authenticator) for high-privilege actions.
- **Role-Based Access Control (RBAC)**
  - Clearly define user roles (e.g., Farmer, Admin) and enforce permission checks in every activity or ViewModel.
  - Never trust client-side flags—validate authorization on the server or in a secured backend component if one exists.

---

## 2. Input Handling & Processing

- **Server-Side & Client-Side Validation**
  - Validate all user inputs (e.g., task titles, descriptions) for length, allowed characters, and context.
  - Use regular expressions or a validation library to enforce patterns and reject unexpected content.
- **Prevent Injection Attacks**
  - Leverage Room’s DAO queries (which use parameterized statements) to avoid SQL injection.
  - Avoid building raw queries by concatenating strings.
- **Output Encoding & XSS Prevention**
  - When displaying dynamic text in WebViews or HTML contexts (if any), HTML-encode user input.
  - Avoid evaluating or injecting user-provided HTML or JavaScript.

---

## 3. Data Protection & Privacy

- **Encryption at Rest**
  - Use **Room** combined with **SQLCipher** or Android’s **EncryptedFile** APIs to encrypt local databases.
  - Store sensitive user data (e.g., location) in encrypted storage (EncryptedSharedPreferences).  
- **Secure API Key Management**
  - Do not hardcode the OpenWeather API key in source. Instead, inject it via Gradle `buildConfigField` from CI/CD environment variables or use the Android Keystore.
- **Encryption in Transit**
  - Configure **Retrofit** and **OkHttp** to enforce TLS 1.2+ and reject self-signed certificates unless explicitly pinned.
  - Optionally implement certificate pinning to mitigate MITM attacks.
- **Data Minimization & Retention**
  - Store only the data required for the app’s functionality.  
  - Purge stale data (e.g., completed tasks older than a configurable threshold) to reduce exposure.

---

## 4. API & Service Security

- **HTTPS Only**
  - Enforce HTTPS for all network calls. Reject insecure (HTTP) endpoints via the Network Security Configuration file.
- **Input Sanitization**
  - Sanitize and validate all JSON responses before parsing (e.g., check for nulls, unexpected types).
- **Rate Limiting & Throttling**
  - Within the client, prevent rapid-fire calls (e.g., debounce UI actions) to avoid accidental spamming of the weather API.
- **Error Handling**
  - Do not expose internal error messages or stack traces to users. Log detailed errors securely (e.g., to an encrypted file) and show generic messages like “Unable to fetch weather data.”

---

## 5. Mobile Application Security Hygiene

- **Secure Network Configuration**
  - Provide an XML Network Security Configuration to reject cleartext traffic and configure allowed CAs.
- **Secure Notifications**
  - Avoid including PII in notification text. Use minimal context (e.g., "You have 1 task due soon").
- **CSRF & Intent-Spoofing**
  - Validate incoming Intents (action, data URI) in Activities and reject those from untrusted sources.
- **Sensitive Log & Debugging Controls**
  - Disable logging of sensitive information (API keys, tokens) in production builds.
  - Remove or guard any `Log.d`/`Log.e` statements that output internal state.
- **AndroidManifest Hardening**
  - Set `android:exported="false"` for components that do not require external invocation.
  - Enforce `android:allowBackup="false"` to prevent data extraction via adb backup.

---

## 6. Infrastructure & Configuration Management

- **Secure Build & Release Process**
  - Sign releases with a securely stored keystore. Protect keystore passwords in a secrets manager (e.g., GitHub Secrets, Jenkins Credentials).
  - Enforce code reviews and automated security scans in CI/CD pipelines (e.g., SonarQube, dependency-check).
- **Dependency Updates & Vulnerability Scanning**
  - Use Gradle’s `dependencyUpdates` plugin and `./gradlew dependencies` to track outdated libraries.
  - Integrate a Software Composition Analysis (SCA) tool to detect known CVEs in dependencies.

---

## 7. Dependency Management & Secure Coding Practices

- **Minimal & Trusted Dependencies**
  - Only include libraries that are actively maintained and widely adopted (e.g., Room, Retrofit, OkHttp).
- **Static Analysis & Linting**
  - Enable Android Lint, SpotBugs, and Detekt/Checkstyle to enforce coding standards and catch potential vulnerabilities early.
- **Architectural Security Patterns**
  - Adopt MVVM with a DI framework (Hilt) to ensure a clear separation of concerns and easier unit testing.
- **Testing & Monitoring**
  - Write unit tests (JUnit) for business logic and UI tests (Espresso) for core user flows, including negative scenarios (e.g., network failures).
  - Consider runtime monitoring (e.g., Firebase Crashlytics) to detect and triage security-relevant crashes or anomalies.

---

By adhering to these guidelines, the SiTani application will be better positioned to protect farmer data, maintain user trust, and comply with industry best practices for mobile security. Secure coding is an ongoing process—regular reviews, updates, and vigilance are essential to keep pace with emerging threats.
