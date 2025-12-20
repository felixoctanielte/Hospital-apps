package com.example.hospital_apps

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AntrianActivity : AppCompatActivity() {

    private lateinit var adapter: AppointmentAdapter
    private val appointmentList = ArrayList<Appointment>()
    private lateinit var db: FirebaseFirestore

    private lateinit var rvQueue: RecyclerView
    private lateinit var layoutEmpty: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_antrian)

        initViews()

        // 1. Setup RecyclerView
        rvQueue.layoutManager = LinearLayoutManager(this)

        // 2. Perbaikan Adapter: Menambahkan parameter kedua untuk menangani klik
        adapter = AppointmentAdapter(appointmentList) { selectedAppt ->
            // Ketika item di antrian diklik, munculkan dialog untuk menyelesaikan pemeriksaan
            showStatusDialog(selectedAppt)
        }
        rvQueue.adapter = adapter

        findViewById<ImageView>(R.id.btn_back_antrian).setOnClickListener { finish() }

        fetchDataFirestore()
    }

    private fun initViews() {
        rvQueue = findViewById(R.id.rv_all_queue)
        layoutEmpty = findViewById(R.id.layout_empty_antrian)
        db = FirebaseFirestore.getInstance()
    }

    private fun fetchDataFirestore() {
        // Mengambil antrian dengan status "menunggu" dan diurutkan berdasarkan waktu
        db.collection("appointments")
            .whereEqualTo("status", "menunggu")
            .orderBy("selectedTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                appointmentList.clear()
                if (snapshots != null && !snapshots.isEmpty) {
                    for (doc in snapshots) {
                        val appt = doc.toObject(Appointment::class.java)
                        appt.id = doc.id // Sangat penting untuk proses update status
                        appointmentList.add(appt)
                    }
                }

                adapter.notifyDataSetChanged()

                // Logic tampilan jika antrian kosong
                if (appointmentList.isEmpty()) {
                    layoutEmpty.visibility = View.VISIBLE
                    rvQueue.visibility = View.GONE
                } else {
                    layoutEmpty.visibility = View.GONE
                    rvQueue.visibility = View.VISIBLE
                }
            }
    }

    // Dialog untuk memproses antrian menjadi selesai
    private fun showStatusDialog(appointment: Appointment) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Proses Antrian")
        builder.setMessage("Selesaikan pemeriksaan untuk ${appointment.patientName}?")

        builder.setPositiveButton("Selesai") { _, _ ->
            val docId = appointment.id ?: return@setPositiveButton

            // Update status di Firestore menjadi "selesai"
            db.collection("appointments").document(docId)
                .update("status", "selesai")
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil diperbarui", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        builder.setNegativeButton("Batal", null)
        builder.show()
    }
}