# 🌾 SiTani – Smart Farming Assistant

**SiTani** adalah aplikasi Android berbasis Java yang dirancang untuk membantu petani dalam mengelola aktivitas pertanian sehari-hari. Aplikasi ini menyediakan fitur login/register, manajemen to-do list, pengecekan cuaca melalui OpenWeatherMap API, notifikasi, serta pengaturan profil pengguna dengan data tersimpan di Firebase.

---

## Fitur Utama

### 1. Login & Register (Firebase Authentication)
- Sistem autentikasi pengguna menggunakan Firebase Authentication
- Registrasi dengan email, password, nama, telepon, dan alamat
- Session management dengan auto-login
- Validasi input dan error handling

### 2. Profile Management
- Menampilkan informasi profil pengguna
- Edit profil (nama, telepon, alamat)
- Data tersimpan di Firebase Firestore
- Fitur logout dengan konfirmasi

### 3. Home Dashboard
- **Welcome Message**: Pesan selamat datang personal
- **Weather Information**: Informasi cuaca real-time menggunakan OpenWeatherMap API
  - Temperature, humidity, wind speed
  - Location-based weather detection
  - Fallback data saat API tidak tersedia
- **Task Summary**: Ringkasan tugas harian
- **Bottom Navigation**: Navigasi mudah antar fitur

### 4. CRUD To-Do List (Firebase Firestore)
- **Create** – Tambah aktivitas baru dengan title dan description
- **Read** – Tampilkan daftar aktivitas dengan RecyclerView
- **Update** – Edit detail aktivitas
- **Delete** – Hapus aktivitas dengan konfirmasi
- **Status Management** – Mark task as completed/uncompleted
- **Real-time Sync** – Data tersimpan dan sinkronisasi di Firebase Firestore
- **Empty State** – Tampilan ketika tidak ada task

### 5. Weather Integration (OpenWeatherMap API)
- **Current Weather**: Data cuaca real-time
- **Location-based**: Otomatis mendeteksi lokasi user
- **API Integration**: Menggunakan Retrofit2 untuk HTTP request
- **Error Handling**: Graceful fallback ketika API gagal
- **Weather Details**: Menampilkan suhu, kelembaban, kecepatan angin

### 6. Notifications System
- **Weather Notifications**: Notifikasi cuaca harian
- **Task Reminders**: Pengingat untuk task yang pending
- **Scheduled Notifications**: Notifikasi otomatis pada waktu tertentu
- **Notification Channels**: Proper notification channels untuk Android 8.0+
- **Boot Receiver**: Restore notifications setelah device restart

---

## Tech Stack & Tools

| Teknologi | Penggunaan |
|----------|------------|
| **Java** | Bahasa utama pengembangan Android |
| **Android Studio** | IDE utama |
| **Firebase Authentication** | Login & Register system |
| **Firebase Firestore** | Cloud database untuk user profiles & to-do list |
| **OpenWeatherMap API** | Data cuaca real-time |
| **Retrofit2** | HTTP client untuk API calls |
| **Gson** | JSON parsing |
| **RecyclerView** | Menampilkan daftar task |
| **Material Design Components** | UI/UX components |
| **ViewBinding** | Binding XML ke Java code |
| **Android Notifications** | Weather & task reminders |
| **FusedLocationProvider** | Location services |

---

## Struktur Project

```
FarmerApp/
├── src/main/
│   ├── java/com/farmerapp/
│   │   ├── activities/
│   │   │   ├── LoginActivity.java
│   │   │   ├── RegisterActivity.java
│   │   │   ├── MainActivity.java
│   │   │   ├── ProfileActivity.java
│   │   │   └── TodoActivity.java
│   │   ├── models/
│   │   │   ├── User.java
│   │   │   ├── TodoItem.java
│   │   │   └── WeatherResponse.java
│   │   ├── adapters/
│   │   │   └── TodoAdapter.java
│   │   ├── utils/
│   │   │   ├── Constants.java
│   │   │   ├── FirebaseHelper.java
│   │   │   ├── WeatherApiService.java
│   │   │   └── WeatherApiClient.java
│   │   ├── notifications/
│   │   │   ├── WeatherNotificationService.java
│   │   │   └── WeatherReceiver.java
│   │   └── FarmerApplication.java
│   ├── res/
│   │   ├── layout/ (XML layouts untuk activities & dialogs)
│   │   ├── values/ (strings, colors, dimensions, themes)
│   │   ├── menu/ (bottom navigation)
│   │   ├── drawable/ (icons & shapes)
│   │   └── xml/ (backup rules, data extraction)
│   └── AndroidManifest.xml
├── build.gradle (app-level)
├── proguard-rules.pro
└── README.md
```

---

## Setup Instructions

### Prerequisites
- Android Studio (Arctic Fox atau lebih tinggi)
- Java Development Kit (JDK 8)
- Android SDK (API level 24 atau lebih tinggi)
- Firebase project setup
- OpenWeatherMap API key

### 1. Firebase Setup
1. Buat project baru di [Firebase Console](https://console.firebase.google.com/)
2. Enable Firebase Authentication dan Firestore Database
3. Download `google-services.json` dan tempatkan di folder `app/`
4. Konfigurasi Firebase Authentication (Email/Password method)
5. Setup Firestore Database security rules

### 2. OpenWeatherMap API
1. Register di [OpenWeatherMap](https://openweathermap.org/api)
2. Dapatkan API key gratis
3. Ganti `YOUR_API_KEY_HERE` di `Constants.java` dengan API key kamu

### 3. Build Configuration
1. Clone repository ini
2. Buka Android Studio
3. Import project
4. Sync Gradle files
5. Build dan run aplikasi

---

## User Flow

1. **First Launch**: User melihat Login/Register screen
2. **Registration**: User membuat account dengan informasi lengkap
3. **Login**: User login dengan email dan password
4. **Home Screen**: Lihat cuaca, ringkasan task, dan navigasi ke fitur lain
5. **Task Management**: Add, edit, delete tasks di Todo Activity
6. **Profile**: View dan edit informasi profil
7. **Logout**: Secure logout dan kembali ke login screen

---

## Cara Kerja Fitur

### Authentication System
- Menggunakan Firebase Authentication untuk user management
- Email validation dan password strength checking
- Auto-login untuk user yang sudah login sebelumnya
- Secure logout dengan session clearing

### Weather API Integration
- Retrofit2 untuk HTTP request ke OpenWeatherMap API
- Automatic location detection menggunakan device GPS
- JSON parsing dengan Gson library
- Error handling dengan fallback data

### Task Management
- Real-time CRUD operations dengan Firebase Firestore
- RecyclerView untuk efficient list display
- Custom adapter dengan checkbox dan action buttons
- Real-time sync across devices

### Notification System
- Scheduled notifications menggunakan Android NotificationManager
- Proper notification channels untuk Android 8.0+
- Boot receiver untuk restore notifications setelah restart

---

## Permissions yang Dibutuhkan

- `INTERNET` - Untuk Firebase dan API calls
- `ACCESS_NETWORK_STATE` - Check network connectivity
- `ACCESS_FINE_LOCATION` - GPS untuk location-based weather
- `ACCESS_COARSE_LOCATION` - General location
- `POST_NOTIFICATIONS` - Weather dan task notifications

---

## Future Enhancements

- **Offline Support**: Cache data untuk akses tanpa internet
- **Multiple Locations**: Support untuk multiple weather locations
- **Task Categories**: Organize tasks dengan categories
- **Weather Forecasts**: Extended weather predictions
- **Push Notifications**: Firebase Cloud Messaging
- **Dark Mode**: Theme switching
- **Data Export**: Export tasks ke CSV
- **Agricultural Tips**: Integration dengan farming advice
- **Market Prices**: Local crop price information

---

## Dependencies Utama

```gradle
// Firebase
implementation platform('com.google.firebase:firebase-bom:32.7.4')
implementation 'com.google.firebase:firebase-auth'
implementation 'com.google.firebase:firebase-database'
implementation 'com.google.firebase:firebase-firestore'

// Retrofit untuk API calls
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// Material Design
implementation 'com.google.android.material:material:1.11.0'

// RecyclerView & Components
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
implementation 'androidx.cardview:cardview:1.0.0'
```

---

**Note**: Project ini menggunakan Firebase dan OpenWeatherMap API. Pastikan untuk mengganti placeholder API keys dan Firebase configurations dengan credentials kamu sebelum deploying ke production.