package com.example.hospital_apps

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PerawatanActivity : AppCompatActivity() {

    private lateinit var adapter: AppointmentAdapter
    private val historyList = ArrayList<Appointment>()
    private lateinit var db: FirebaseFirestore

    private lateinit var rvHistory: RecyclerView
    private lateinit var tvEmpty: LinearLayout
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perawatan)

        initViews()

        // 1. Setup Layout Manager
        rvHistory.layoutManager = LinearLayoutManager(this)

        // 2. PERBAIKAN: Tambahkan parameter kedua (callback klik) agar tidak merah
        // Di halaman riwayat, kita biarkan kosong saja jika tidak ada aksi khusus
        adapter = AppointmentAdapter(historyList) { selectedAppt ->
            // Opsional: Tampilkan pesan informasi singkat
            Toast.makeText(this, "Detail: ${selectedAppt.patientName}", Toast.LENGTH_SHORT).show()
        }
        rvHistory.adapter = adapter

        btnBack.setOnClickListener { finish() }

        fetchHistoryFirestore()
    }

    private fun initViews() {
        rvHistory = findViewById(R.id.rv_history)
        tvEmpty = findViewById(R.id.tv_empty_history)
        btnBack = findViewById(R.id.btn_back)
        db = FirebaseFirestore.getInstance()
    }

    private fun fetchHistoryFirestore() {
        // Filter: Hanya ambil yang status == "selesai" milik dr. Tirta
        db.collection("appointments")
            .whereEqualTo("status", "selesai")
            .whereEqualTo("doctorName", "dr. Tirta") // Pastikan filter dokter tetap ada
            .orderBy("selectedTime", Query.Direction.DESCENDING) // Tampilkan yang terbaru di atas
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error load history: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                historyList.clear()
                if (snapshots != null && !snapshots.isEmpty) {
                    for (doc in snapshots) {
                        val appt = doc.toObject(Appointment::class.java)
                        appt.id = doc.id
                        historyList.add(appt)
                    }
                }

                adapter.notifyDataSetChanged()

                // Logic tampilan Empty State
                if (historyList.isEmpty()) {
                    tvEmpty.visibility = View.VISIBLE
                    rvHistory.visibility = View.GONE
                } else {
                    tvEmpty.visibility = View.GONE
                    rvHistory.visibility = View.VISIBLE
                }
            }
    }
}