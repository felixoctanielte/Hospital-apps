package com.example.hospital_apps

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        val doctor = intent.getParcelableExtra<Doctor>("doctor")
        val poli = intent.getParcelableExtra<Poli>("poli")
        val time = intent.getStringExtra("time")

        findViewById<TextView>(R.id.tv_poli).text = "Poli: ${poli?.name ?: "-"}"
        findViewById<TextView>(R.id.tv_doctor).text = "Dokter: ${doctor?.name ?: "-"}"
        findViewById<TextView>(R.id.tv_time).text = "Waktu: ${time ?: "-"}"

        findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            // TODO: panggil API / simpan ke DB sesuai logic
            Toast.makeText(this, "Berhasil mendaftar pada ${doctor?.name} jam $time", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
