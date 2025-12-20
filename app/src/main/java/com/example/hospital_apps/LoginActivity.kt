package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    // TAG untuk Logcat
    private val TAG = "LoginActivity"

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Mengambil referensi View dari layout (Pastikan ID sudah benar di activity_login.xml)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val btnGoogle = findViewById<Button>(R.id.btn_google)
        val tvRegister = findViewById<TextView>(R.id.tv_register)
        val btnBack = findViewById<ImageButton>(R.id.btn_back)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty()) {
                etEmail.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Password tidak boleh kosong"
                return@setOnClickListener
            }

            // 1. Proses Login menggunakan Firebase Auth (Authentication)
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->

                    val user = authResult.user
                    val uid = user?.uid

                    if (uid == null) {
                        Log.e(TAG, "UID pengguna tidak ditemukan setelah login berhasil.")
                        Toast.makeText(this, "Error: User tidak ditemukan setelah otentikasi.", Toast.LENGTH_LONG).show()
                        return@addOnSuccessListener
                    }

                    Log.d(TAG, "Login berhasil, UID: $uid")

                    // 2. Ambil role dari Firestore (Authorization)
                    db.collection("users").document(uid)
                        .get()
                        .addOnSuccessListener { document ->

                            // Cek apakah dokumen pengguna ada di Firestore
                            if (!document.exists()) {
                                Log.e(TAG, "Dokumen pengguna tidak ditemukan di koleksi 'users' dengan UID: $uid")
                                Toast.makeText(this, "Error: Data profil pengguna tidak lengkap. Silakan daftar ulang.", Toast.LENGTH_LONG).show()
                                auth.signOut() // Logout pengguna yang datanya tidak lengkap
                                return@addOnSuccessListener
                            }

                            // Ambil nilai role dan konversi ke huruf kecil untuk menghindari masalah case sensitivity
                            val role = document.getString("role")?.toLowerCase()
                            Log.d(TAG, "Role ditemukan: $role")

                            // 3. Navigasi Berdasarkan Role
                            when (role) {
                                "admin" -> {
                                    Toast.makeText(this, "Login sebagai Admin", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, AdminActivity::class.java))
                                }
                                "dokter" -> { // Asumsi role dokter menggunakan huruf kecil
                                    Toast.makeText(this, "Login sebagai Dokter", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, DoctorActivity::class.java))
                                }
                                else -> {
                                    // Semua role lain diarahkan ke MainActivity/UserActivity
                                    Toast.makeText(this, "Login sebagai Pengguna Biasa", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, MainActivity::class.java))
                                }
                            }
                            finish() // Tutup LoginActivity
                        }
                        .addOnFailureListener { firestoreException ->
                            // Kegagalan saat mengambil data dari Firestore (misal: masalah koneksi/rule)
                            Log.e(TAG, "Gagal mengambil data role dari Firestore: ${firestoreException.message}")
                            Toast.makeText(this, "Error: Gagal memuat role pengguna.", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { authException ->
                    // Kegagalan Login Firebase Auth (Password salah, user tidak ditemukan, dll.)
                    Log.e(TAG, "Login Gagal: ${authException.message}")
                    Toast.makeText(this, "Login gagal: ${authException.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // --- Penanganan Tombol Lain ---

        btnGoogle.setOnClickListener {
            Toast.makeText(this, "Google Login belum dihubungkan", Toast.LENGTH_SHORT).show()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Jika user sudah login (Auto-login saat restart)
            // Cek Role di Firestore sebelum menentukan halaman
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")
                    if (role == "dokter") {
                        // Jika dia dokter, lempar ke DoctorActivity
                        startActivity(Intent(this, DoctorActivity::class.java))
                    } else {
                        // Jika dia pasien, lempar ke MainActivity (file yang kamu kirim)
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    finish() // Hapus dari backstack
                }
                .addOnFailureListener {
                    // Jika gagal cek role, paksa login ulang
                    auth.signOut()
                }
        }
    }
}