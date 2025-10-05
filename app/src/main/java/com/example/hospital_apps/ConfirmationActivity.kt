package com.example.hospital_apps

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        val doctor = intent.getParcelableExtra<Doctor>("doctor")
        val poli = intent.getParcelableExtra<Poli>("poli")
        val time = intent.getStringExtra("time")
        val userName = "Jeremy Dominic" // contoh user login

        // Bind view
        val tvUser: TextView = findViewById(R.id.tv_user_name)
        val tvDoctor: TextView = findViewById(R.id.tv_doctor_name)
        val tvPoli: TextView = findViewById(R.id.tv_poli_name)
        val tvTime: TextView = findViewById(R.id.tv_time)
        val btnConfirm: Button = findViewById(R.id.btn_confirm)
        val btnCancel: Button = findViewById(R.id.btn_cancel)

        tvUser.text = userName
        tvDoctor.text = doctor?.name
        tvPoli.text = poli?.name
        tvTime.text = time

        btnConfirm.setOnClickListener {
            android.widget.Toast.makeText(this, "Pendaftaran berhasil!", android.widget.Toast.LENGTH_SHORT).show()
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}
