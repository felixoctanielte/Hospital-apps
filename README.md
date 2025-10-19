# 🏥 Clinic N Quick  
**Aplikasi Rumah Sakit Rawat Jalan**  
_Mobile Application Programming – UTS Project_

---

## 📖 Deskripsi Aplikasi
**Clinic N Quick** adalah aplikasi mobile berbasis **Android** yang dirancang untuk meningkatkan efisiensi sistem **antrian rawat jalan di rumah sakit**.  
Melalui aplikasi ini, pasien dapat melakukan **pendaftaran poli online**, memilih dokter dan jadwal praktik, serta memantau **status antrian secara real-time** tanpa harus menunggu lama di rumah sakit.

Aplikasi ini juga dilengkapi dengan fitur **Machine Learning** untuk memperkirakan **estimasi waktu tunggu pasien**, berdasarkan data historis antrian dan durasi pelayanan dokter.  
Selain itu, sistem memiliki 3 peran utama — **Pasien**, **Dokter**, dan **Admin Rumah Sakit** — dengan fungsi dan hak akses yang berbeda-beda.

---

## 🎯 Tujuan Utama
- Mengurangi waktu tunggu pasien secara signifikan.  
- Mempermudah proses pendaftaran dan manajemen antrian.  
- Mendukung digitalisasi sistem rumah sakit melalui aplikasi mobile.  
- Menyediakan data laporan yang akurat dan terstruktur untuk admin rumah sakit.

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

## 🌟 Fitur Unggulan

### 👨‍⚕️ Untuk Pasien:
- Pendaftaran rawat jalan secara online  
- Melihat jadwal dokter dan daftar poli  
- Melihat estimasi waktu tunggu antrian  
- Melihat riwayat kunjungan dan hasil pemeriksaan  
- Scan QR untuk melihat status antrian  

### 🩺 Untuk Dokter:
- Melihat daftar pasien hari ini  
- Menginput hasil pemeriksaan dan resep  
- Melihat riwayat pasien sebelumnya  
- Mengelola profil dokter  

### 🧾 Untuk Admin:
- CRUD **Master Data** (Poli, Dokter, Pasien)  
- Verifikasi data pasien baru  
- Manajemen akun dan hak akses pengguna  
- Membuat dan mengunduh **laporan harian (PDF/Excel)**  

---

## 🧠 Teknologi yang Digunakan
| Komponen | Teknologi |
|-----------|------------|
| Bahasa Pemrograman | **Kotlin** |
| Framework | **Android Jetpack (ViewModel, LiveData, Navigation)** |
| Database | **Firebase / SQLite (opsional)** |
| API Integrasi | **GeoLocation API**, **ICD & MyHealthFinder API** |
| Desain UI | **Material Design (XML Layout)** |
| ML Model | Estimasi waktu tunggu berbasis data antrian |
| Build System | **Gradle (KTS)** |

---

## 📲 Cara Instalasi & Menjalankan Aplikasi

1. **Clone Repository**
   ```bash
   git clone https://github.com/yourusername/clinic-n-quick.git
