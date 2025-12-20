package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfilActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var tvNama: TextView
    private lateinit var tvPoli: TextView
    private lateinit var tvSip: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_doctor)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // View binding manual
        tvNama = findViewById(R.id.doctorName)
        tvPoli = findViewById(R.id.doctorSpecialty)
        tvSip = findViewById(R.id.tvSip)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)

        val btnBack = findViewById<ImageButton>(R.id.buttonBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this, ActivityDoctorPage::class.java))
            finish()
        }

        loadDoctorProfile()
    }

    private fun loadDoctorProfile() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    Toast.makeText(this, "Data dokter tidak ditemukan", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                tvNama.text = doc.getString("name") ?: "-"
                tvPoli.text = doc.getString("poli") ?: "-"
                tvSip.text = "SIP: ${doc.getString("sip") ?: "-"}"
                tvEmail.text = "Email: ${doc.getString("email") ?: "-"}"
                tvPhone.text = "Kontak: ${doc.getString("phone") ?: "-"}"
            }
            .addOnFailureListener { e ->
                Log.e("ProfilActivity", e.message.toString())
                Toast.makeText(this, "Gagal memuat data dokter", Toast.LENGTH_SHORT).show()
            }
    }
}
