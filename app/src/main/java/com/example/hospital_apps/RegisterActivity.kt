package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // UI Components
    private lateinit var btnRegister: Button
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnRegister = findViewById(R.id.btn_register)
        val tvSignin = findViewById<TextView>(R.id.tv_signin)

        // Init Progress Bar (aman jika null)
        try {
            progressBar = findViewById(R.id.progressBar)
        } catch (e: Exception) {
            progressBar = null
        }

        val etName = findViewById<EditText>(R.id.et_name)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPhone = findViewById<EditText>(R.id.et_phone)
        val etPassword = findViewById<EditText>(R.id.et_password)

        btnBack.setOnClickListener { finish() }

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validasi Input
            if (name.isEmpty()) { etName.error = "Nama wajib diisi"; return@setOnClickListener }
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.error = "Email tidak valid"; return@setOnClickListener }
            if (phone.isEmpty()) { etPhone.error = "No HP wajib diisi"; return@setOnClickListener }
            if (password.length < 6) { etPassword.error = "Password min 6 karakter"; return@setOnClickListener }

            loading(true)

            // 1. Register ke Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val uid = authResult.user?.uid

                    if (uid != null) {
                        // 2. Simpan ke Firestore
                        // PENTING: Role kita kunci jadi "pasien"
                        saveUserToFirestore(uid, name, email, phone)
                    } else {
                        loading(false)
                        Toast.makeText(this, "Gagal mendapatkan UID", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    loading(false)
                    Toast.makeText(this, "Gagal daftar: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        tvSignin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun saveUserToFirestore(uid: String, name: String, email: String, phone: String) {
        val userMap = hashMapOf(
            "uid" to uid,
            "username" to name, // Konsisten dengan MainActivity
            "email" to email,
            "phone" to phone,
            "role" to "pasien"  // <--- HARDCODE OTOMATIS JADI PASIEN
        )

        db.collection("users").document(uid).set(userMap)
            .addOnSuccessListener {
                loading(false)
                Toast.makeText(this, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()

                // Redirect ke Login agar user login ulang untuk verifikasi
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                loading(false)
                // Jika gagal simpan data, hapus akun auth agar user bisa daftar ulang
                auth.currentUser?.delete()
                Toast.makeText(this, "Gagal simpan data: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            btnRegister.isEnabled = false
            btnRegister.text = "Loading..."
            progressBar?.visibility = View.VISIBLE
        } else {
            btnRegister.isEnabled = true
            btnRegister.text = "DAFTAR"
            progressBar?.visibility = View.GONE
        }
    }
}