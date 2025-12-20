package com.example.hospital_apps

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PasienRiwayatActivity : AppCompatActivity() {

    private lateinit var rvRiwayat: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var spinnerFilter: Spinner
    private lateinit var btnBack: ImageView

    private lateinit var adapter: AppointmentAdapter
    private val historyList = ArrayList<Appointment>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasien_riwayat)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        initViews()

        // --- 1. SETUP TOMBOL BACK ---
        btnBack.setOnClickListener {
            finish() // Kembali ke menu sebelumnya
        }

        // --- 2. SETUP RECYCLERVIEW ---
        rvRiwayat.layoutManager = LinearLayoutManager(this)
        adapter = AppointmentAdapter(historyList) {
            // Klik item riwayat (bisa dikosongkan)
        }
        rvRiwayat.adapter = adapter

        setupSpinner()
    }

    private fun initViews() {
        rvRiwayat = findViewById(R.id.rv_riwayat_pasien)
        tvEmpty = findViewById(R.id.tv_empty_riwayat)
        spinnerFilter = findViewById(R.id.spinner_filter)
        btnBack = findViewById(R.id.btn_back_history) // Sesuai ID di XML
    }

    private fun setupSpinner() {
        val options = arrayOf("5 Terakhir", "10 Terakhir", "Semua Riwayat")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = adapterSpinner

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> loadMyHistory(5)
                    1 -> loadMyHistory(10)
                    2 -> loadMyHistory(null)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadMyHistory(limit: Long?) {
        val uid = auth.currentUser?.uid ?: return

        // QUERY INI MEMBUTUHKAN INDEX BARU DI FIREBASE
        // userId (Asc) + status (Asc) + selectedTime (DESC)
        var query: Query = db.collection("appointments")
            .whereEqualTo("userId", uid)
            .whereEqualTo("status", "selesai") // Hanya yang selesai
            .orderBy("selectedTime", Query.Direction.DESCENDING) // Urutkan dari yang terbaru

        if (limit != null) {
            query = query.limit(limit)
        }

        query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                // JIKA MENTAL/CRASH, CEK LOGCAT!
                // Pasti ada link: https://console.firebase.google.com/v1/r/project/...
                Log.e("Riwayat", "Error query", e)
                Toast.makeText(this, "Gagal memuat (Cek Index Firestore)", Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            historyList.clear()
            if (snapshots != null && !snapshots.isEmpty) {
                for (doc in snapshots) {
                    val appt = doc.toObject(Appointment::class.java)
                    appt.id = doc.id
                    historyList.add(appt)
                }
                rvRiwayat.visibility = View.VISIBLE
                tvEmpty.visibility = View.GONE
            } else {
                rvRiwayat.visibility = View.GONE
                tvEmpty.visibility = View.VISIBLE
            }
            adapter.notifyDataSetChanged()
        }
    }
}