package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var patientNameReal: String = ""
    private var doctorNameStr: String? = null
    private var poliNameStr: String? = null
    private var timeStr: String? = null
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val tvUser: TextView = findViewById(R.id.tv_user_name)
        val tvDoctor: TextView = findViewById(R.id.tv_doctor_name)
        val tvPoli: TextView = findViewById(R.id.tv_poli_name)
        val tvTime: TextView = findViewById(R.id.tv_time)
        val btnConfirm: Button = findViewById(R.id.btn_confirm)
        val btnCancel: Button = findViewById(R.id.btn_cancel)
        progressBar = findViewById(R.id.progressBar)

        doctorNameStr = intent.getStringExtra("doctorName") ?: "Dokter Umum"
        poliNameStr = intent.getStringExtra("poliName") ?: "Poli Umum"
        timeStr = intent.getStringExtra("time") ?: "-"

        tvDoctor.text = doctorNameStr
        tvPoli.text = poliNameStr
        tvTime.text = timeStr

        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        patientNameReal = document.getString("username") ?: "Pasien"
                        tvUser.text = patientNameReal
                    }
                }
        }

        btnConfirm.setOnClickListener {
            if (patientNameReal.isEmpty()) {
                Toast.makeText(this, "Sedang memuat profil...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            checkBookingAndSave()
        }

        btnCancel.setOnClickListener { finish() }
    }

    private fun checkBookingAndSave() {
        val uid = auth.currentUser?.uid ?: return

        // Format tanggal Indonesia: Hari, Tgl Bln Thn
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))
        val tanggalHariIni = sdf.format(Date())

        progressBar.visibility = View.VISIBLE
        findViewById<Button>(R.id.btn_confirm).isEnabled = false

        db.collection("appointments")
            .whereEqualTo("userId", uid)
            .whereEqualTo("date", tanggalHariIni)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    progressBar.visibility = View.GONE
                    findViewById<Button>(R.id.btn_confirm).isEnabled = true
                    Toast.makeText(this, "Anda sudah memiliki janji temu hari ini.", Toast.LENGTH_LONG).show()
                } else {
                    saveAppointment(uid, tanggalHariIni)
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                findViewById<Button>(R.id.btn_confirm).isEnabled = true
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveAppointment(uid: String, tanggal: String) {
        val appointmentData = hashMapOf(
            "userId" to uid,
            "patientName" to patientNameReal,
            "doctorName" to doctorNameStr,
            "poli" to poliNameStr,
            "selectedTime" to timeStr,
            "status" to "menunggu",
            "date" to tanggal
        )

        db.collection("appointments")
            .add(appointmentData)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Pendaftaran Berhasil!", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                findViewById<Button>(R.id.btn_confirm).isEnabled = true
                Toast.makeText(this, "Gagal simpan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}