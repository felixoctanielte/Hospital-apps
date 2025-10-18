package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class ProfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_doctor)

        // Tombol back
        val buttonBack = findViewById<ImageButton>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            // Kembali ke ActivityDoctorPage
            val intent = Intent(this, ActivityDoctorPage::class.java)
            startActivity(intent)
            finish() // optional, agar ProfilActivity ditutup
        }
    }
}
