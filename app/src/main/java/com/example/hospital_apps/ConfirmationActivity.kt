package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ConfirmationActivity : AppCompatActivity() {

    // Variabel Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Variabel Data
    private var patientNameReal: String = ""
    private var doctorNameStr: String? = null
    private var poliNameStr: String? = null
    private var timeStr: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        // 1. Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 2. Bind View
        val tvUser: TextView = findViewById(R.id.tv_user_name)
        val tvDoctor: TextView = findViewById(R.id.tv_doctor_name)
        val tvPoli: TextView = findViewById(R.id.tv_poli_name)
        val tvTime: TextView = findViewById(R.id.tv_time)
        val btnConfirm: Button = findViewById(R.id.btn_confirm)
        val btnCancel: Button = findViewById(R.id.btn_cancel)

        // 3. Ambil Data dari Intent (Pastikan pengirim mengirim String)
        // Kita pakai String agar mudah disimpan ke database
        doctorNameStr = intent.getStringExtra("doctorName") ?: "Dokter Umum"
        poliNameStr = intent.getStringExtra("poliName") ?: "Poli Umum"
        timeStr = intent.getStringExtra("time") ?: "-"

        // Set Text awal
        tvDoctor.text = doctorNameStr
        tvPoli.text = poliNameStr
        tvTime.text = timeStr
        tvUser.text = "Memuat nama..." // Placeholder loading

        // 4. AMBIL NAMA ASLI USER (Gantikan Jeremy Dominic)
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("username") ?: "Pasien"
                        patientNameReal = name
                        tvUser.text = name // Tampilkan nama asli
                    }
                }
                .addOnFailureListener {
                    tvUser.text = "Gagal memuat profil"
                }
        }

        // 5. LOGIKA TOMBOL KONFIRMASI (SIMPAN KE DB)
        btnConfirm.setOnClickListener {
            if (patientNameReal.isEmpty()) {
                Toast.makeText(this, "Sedang memuat data profil...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveToFirestore()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveToFirestore() {
        val uid = auth.currentUser?.uid ?: return

        // 1. TAMBAHAN BARU: AMBIL TANGGAL HARI INI
        // Format: "Sab, 20 Des 2025" (Bahasa Indonesia)
        val sdf = java.text.SimpleDateFormat("EEE, dd MMM yyyy", java.util.Locale("id", "ID"))
        val tanggalHariIni = sdf.format(java.util.Date())

        // Data yang akan dikirim ke Dokter
        val appointmentData = hashMapOf(
            "userId" to uid,
            "patientName" to patientNameReal,
            "doctorName" to doctorNameStr,
            "poli" to poliNameStr,
            "selectedTime" to timeStr,
            "status" to "menunggu",

            // 2. MASUKKAN TANGGAL KE DATABASE
            "date" to tanggalHariIni
        )

        db.collection("appointments")
            .add(appointmentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Pendaftaran Berhasil!", Toast.LENGTH_LONG).show()

                // Kembali ke Menu Utama
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal Booking: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}