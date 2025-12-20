package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore

class DoctorListActivity : AppCompatActivity() {

    // Firestore
    private lateinit var db: FirebaseFirestore

    // Adapter & List
    private lateinit var doctorAdapter: DoctorAdapter
    private val doctorList = ArrayList<Doctor>()

    // ProgressBar
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_list)

        // 1. Ambil Data Poli dari Halaman Sebelumnya (ScheduleActivity)
        // Kita ambil Namanya saja untuk filter (String)
        val poliObject = intent.getParcelableExtra<Poli>("poli")
        // Jika null, default ke "Umum"
        val filterPoliName = poliObject?.name ?: intent.getStringExtra("poliName") ?: "Umum"

        // 2. Setup Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = "Dokter $filterPoliName"
        toolbar.setNavigationOnClickListener { finish() }

        // Setup ProgressBar (jika ada di XML)
        try { progressBar = findViewById(R.id.progressBar) } catch (e: Exception) {}

        // 3. Setup RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.rv_doctors)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup Adapter
        doctorAdapter = DoctorAdapter(doctorList) { selectedDoctor, selectedTime ->
            // --- KLIK DOKTER: PINDAH KE KONFIRMASI ---
            val intent = Intent(this, ConfirmationActivity::class.java)

            // PENTING: Kirim sebagai STRING agar ConfirmationActivity bisa baca
            intent.putExtra("doctorName", selectedDoctor.name)
            intent.putExtra("poliName", filterPoliName) // Pakai nama poli dari filter
            intent.putExtra("time", selectedTime)

            startActivity(intent)
        }
        recyclerView.adapter = doctorAdapter

        // 4. Inisialisasi Firestore
        db = FirebaseFirestore.getInstance()

        // 5. Ambil Data
        fetchDoctorsFromFirestore(filterPoliName)
    }

    private fun fetchDoctorsFromFirestore(category: String) {
        progressBar?.visibility = View.VISIBLE

        db.collection("dokter")
            .get()
            .addOnSuccessListener { documents ->
                doctorList.clear()

                for (doc in documents) {
                    val name = doc.getString("name") ?: "Dokter Tanpa Nama"

                    // 1. PENANGANAN CASE SENSITIVE FIELD: Specialty vs specialty
                    val specialty = doc.getString("specialty") ?: doc.getString("Specialty") ?: ""

                    // 2. AMBIL STATUS TERSEDIA DARI FIRESTORE
                    // Jika field di Firebase adalah boolean, gunakan getBoolean
                    val available = doc.getBoolean("isAvailable") ?: false

                    if (specialty.contains(category, ignoreCase = true)) {
                        // 3. MASUKKAN STATUS KE OBJEK
                        val doctorObj = Doctor(
                            id = doc.id,
                            name = name,
                            specialist = specialty,
                            isAvailable = available, // <--- Ini yang tadi terlewat
                            imageResId = R.drawable.ic_dokter_placeholder
                        )
                        doctorList.add(doctorObj)
                    }
                }

                doctorAdapter.notifyDataSetChanged()
                progressBar?.visibility = View.GONE

                if (doctorList.isEmpty()) {
                    Toast.makeText(this, "Tidak ada dokter untuk poli ini", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                progressBar?.visibility = View.GONE
                Toast.makeText(this, "Gagal memuat dokter: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

}