# 🌾 SiTani – Smart Farming Assistant

**SiTani** adalah aplikasi Android yang dirancang untuk membantu petani dalam mengelola aktivitas pertanian sehari-hari. Aplikasi ini menyediakan fitur manajemen to-do list, pengecekan cuaca melalui API, pengingat aktivitas, serta pengaturan profil pengguna.  
Project ini dikembangkan sebagai tugas akhir semester mata kuliah **Mobile Programming**.

---

## Fitur Utama

### 1. Login & Register
- Sistem autentikasi dasar untuk pengguna baru dan pengguna lama.

### 2. Edit Profile  
- Mengelola informasi pengguna seperti nama, lokasi, dan detail lainnya.

### 3. Home Dashboard
- Menampilkan to-do list harian menggunakan RecyclerView  
- Menampilkan cuaca hari ini melalui integrasi **OpenWeather API**  
- Informasi cuaca disajikan secara real-time dengan Retrofit

### 4. CRUD To-Do List
- **Create** – Tambah aktivitas baru  
- **Read** – Tampilkan daftar aktivitas  
- **Update** – Edit detail aktivitas  
- **Delete** – Hapus aktivitas  
- Data disimpan dengan **Room Database**

### 5. Reminder / Notification
- Mengatur pengingat aktivitas menggunakan AlarmManager atau WorkManager  
- Notifikasi muncul sesuai jadwal yang ditetapkan pengguna

---

## Tech Stack & Tools

| Teknologi | Penggunaan |
|----------|------------|
| **Java** | Bahasa utama pengembangan |
| **Android Studio** | IDE utama |
| **Room Database** | Penyimpanan data lokal (To-Do List) |
| **Retrofit + Open-Meteo API** | Mendapatkan data cuaca real-time |
| **RecyclerView** | Menampilkan daftar task |
| **ViewBinding / XML Layout** | Desain UI |
| **AlarmManager / WorkManager** | Notifikasi & reminder |

---
