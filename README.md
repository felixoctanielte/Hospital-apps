# 🏥 Clinic & Quick  
**Aplikasi Rumah Sakit Rawat Jalan**  
_Mobile Application Programming – UAS Project_

---

## 📖 Deskripsi Aplikasi
**Clinic & Quick** adalah aplikasi mobile berbasis **Android** yang dirancang untuk meningkatkan efisiensi sistem **antrian rawat jalan di rumah sakit**.  
Melalui aplikasi ini, pasien dapat melakukan **pendaftaran poli secara online**, memilih dokter dan jadwal praktik, serta memantau **status antrian secara real-time** tanpa harus menunggu lama di rumah sakit.

Aplikasi ini menggunakan **Firebase Cloud Firestore** sebagai backend utama serta **Firebase Authentication** untuk manajemen akun pengguna.  
Selain itu, aplikasi terintegrasi dengan **API berbasis Python** yang dideploy di **PythonAnywhere** untuk mendukung proses backend tambahan.

Sistem memiliki **3 peran utama**, yaitu:
- **Pasien**
- **Dokter**
- **Admin Rumah Sakit**

---

## 🎯 Tujuan Aplikasi
- Mengurangi waktu tunggu pasien di rumah sakit  
- Digitalisasi sistem pendaftaran rawat jalan  
- Mempermudah manajemen antrian dan data pasien  
- Menyediakan sistem yang terstruktur dan mudah diuji  

---

## 👥 Anggota Kelompok

| NIM | Nama Lengkap |
|-----|-----------------------------|
| 00000103941 | **Jeremy Dominic Adestus Gerungan** |
| 00000101926 | **Felix Octaniel Telaumbanua** |
| 00000104848 | **Denito Fransiskus Triarta Samosir** |
| 00000102674 | **Evan Luthfi Wibowo** |

📘 **Program Studi Informatika**  
**Fakultas Teknik dan Informatika**  
**Universitas Multimedia Nusantara (UMN)**  
**Tahun: 2025**

---

## 🌟 Fitur Utama

### 👤 Pasien
- Registrasi dan login akun pasien  
- Pendaftaran rawat jalan (appointment)  
- Memilih poli dan dokter  
- Melihat status antrian (**Menunggu / Diproses / Selesai**)  
- Melihat riwayat kunjungan  

### 🩺 Dokter
- Melihat daftar pasien hari ini  
- Mengubah status pemeriksaan pasien  
- Melihat data pasien  
- Mengelola akun dokter  

### 🧾 Admin
- CRUD **Master Data**:
  - Poli
  - Dokter
  - Pasien
- Manajemen akun pengguna  
- Monitoring seluruh appointment  

---

## 🧠 Teknologi yang Digunakan

| Komponen | Teknologi |
|--------|----------|
| Bahasa Pemrograman | Kotlin |
| Platform | Android |
| UI Design | Material Design (XML) |
| Database | Firebase Cloud Firestore |
| Authentication | Firebase Authentication |
| Backend API | Python (Flask) |
| API Deployment | PythonAnywhere |
| Build System | Gradle |

---

## ☁️ Struktur Firebase (Cloud Firestore)

Struktur utama koleksi pada Firebase:
appointments
dokter
pasien
poli
users

Semua data antrian, dokter, pasien, dan pengguna disimpan secara **real-time** menggunakan Cloud Firestore.

---

## 🔗 API Integration (Python)

Aplikasi terintegrasi dengan API berbasis Python yang di-deploy di PythonAnywhere:

https://levonchka.pythonanywhere.com


---

## 🔐 Akun Demo (Login Credentials)

Gunakan akun berikut untuk **menguji aplikasi tanpa perlu registrasi**:

### 👤 Pasien
Email: jeremyd@gmail.com
Password: 123456

Email: domi@gmail.com
Password: 123456


### 🩺 Dokter
Email: tirta@gmail.com
Password: 123456

Email: peter@gmail.com
Password: 1234567


### 🧾 Admin
Email: admin@gmail.com
Password: 1234567

---

## 📲 Cara Menjalankan Aplikasi

1. Clone repository
   ```bash
   git clone https://github.com/yourusername/clinic-n-quick.git
2. Buka project menggunakan Android Studio
3. Pastikan koneksi internet aktif
(Aplikasi menggunakan Firebase Cloud Firestore dan API Python)
4. Jalankan aplikasi di emulator atau device fisik
5. Login menggunakan akun demo sesuai role yang ingin diuji


