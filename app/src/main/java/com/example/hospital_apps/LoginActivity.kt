package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

            if (email.equals("admin@gmail.com", ignoreCase = true) && password == "12345") {
                Toast.makeText(this, "Login sebagai Admin", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
                finish()
            } else if (email.equals("dokter@gmail.com", ignoreCase = true) && password == "12345") {
                Toast.makeText(this, "Login sebagai Dokter", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DoctorActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "Login sebagai User", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }


        btnGoogle.setOnClickListener {
            Toast.makeText(this, "Login dengan Google berhasil 🎉", Toast.LENGTH_SHORT).show()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnBack.setOnClickListener { finish() }
    }
}
