package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Back
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Regist
        val btnRegister = findViewById<Button>(R.id.btn_register)
        btnRegister.setOnClickListener {
            Toast.makeText(this, "Pendaftaran berhasil ", Toast.LENGTH_SHORT).show()

            // Back to Login after regist
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //  Link to Login
        val tvSignin = findViewById<TextView>(R.id.tv_signin)
        tvSignin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}