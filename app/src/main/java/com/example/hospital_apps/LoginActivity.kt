package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Login
        val btnLogin = findViewById<Button>(R.id.btn_login)
        btnLogin.setOnClickListener {
            Toast.makeText(this, "Login berhasil ", Toast.LENGTH_SHORT).show()

            // move to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //  Google
        val btnGoogle = findViewById<Button>(R.id.btn_google)
        btnGoogle.setOnClickListener {
            Toast.makeText(this, "Login dengan Google berhasil 🎉", Toast.LENGTH_SHORT).show()
        }

        //  Register
        val tvRegister = findViewById<TextView>(R.id.tv_register)
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //  Back
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish() // kembali ke halaman sebelumnya
        }
    }
}
