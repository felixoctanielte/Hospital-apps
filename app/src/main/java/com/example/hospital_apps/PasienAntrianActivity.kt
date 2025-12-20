package com.example.hospital_apps

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PasienAntrianActivity : AppCompatActivity() {

    private lateinit var rvAntrian: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var btnBack: ImageView // 1. Deklarasi Variabel

    private lateinit var adapter: AppointmentAdapter
    private val antrianList = ArrayList<Appointment>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasien_antrian)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        rvAntrian = findViewById(R.id.rv_antrian_pasien)
        tvEmpty = findViewById(R.id.tv_empty_antrian)
        btnBack = findViewById(R.id.btn_back) // 2. Hubungkan dengan ID di XML

        // 3. LOGIKA TOMBOL BACK (Tutup Activity ini)
        btnBack.setOnClickListener {
            finish()
        }

        rvAntrian.layoutManager = LinearLayoutManager(this)

        // Menggunakan Adapter
        adapter = AppointmentAdapter(antrianList) {
            // Aksi klik item (bisa dikosongkan)
        }
        rvAntrian.adapter = adapter

        loadMyQueue()
    }

    private fun loadMyQueue() {
        val currentUser = auth.currentUser
        if (currentUser == null) return

        val uid = currentUser.uid

        db.collection("appointments")
            .whereEqualTo("userId", uid) // INI YANG MEMBUAT HANYA ANTRIAN KITA SAJA
            .whereEqualTo("status", "menunggu")
            .orderBy("selectedTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Gagal memuat: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                antrianList.clear()
                if (snapshots != null && !snapshots.isEmpty) {
                    for (doc in snapshots) {
                        val appt = doc.toObject(Appointment::class.java)
                        appt.id = doc.id
                        antrianList.add(appt)
                    }
                    rvAntrian.visibility = View.VISIBLE
                    tvEmpty.visibility = View.GONE
                } else {
                    rvAntrian.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                }
                adapter.notifyDataSetChanged()
            }
    }
}