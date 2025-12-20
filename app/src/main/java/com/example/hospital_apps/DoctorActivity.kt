package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class DoctorActivity : AppCompatActivity() {

    // --- Variabel UI ---
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuIcon: ImageView
    private lateinit var rvPasien: RecyclerView
    private lateinit var tvEmptyState: TextView

    // Header Dashboard (Info Pasien Saat Ini)
    private lateinit var tvCurrentName: TextView
    private lateinit var tvCurrentPoli: TextView
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvCurrentStatus: TextView

    // --- Firebase & Data ---
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var firestoreListener: ListenerRegistration? = null

    // Adapter & List Data
    private lateinit var adapter: DoctorTaskAdapter
    private val appointmentList = ArrayList<Appointment>()
    private var currentDoctorName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_page) // Pastikan nama layout Activity ini benar

        // 1. Init Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // 2. Init Views
        initViews()

        // 3. Setup RecyclerView dengan Adapter Baru
        setupRecyclerView()

        // 4. Setup Navigasi Sidebar
        setupNavigation()

        // 5. Load Data Profil Dokter & Ambil Pasien
        loadDoctorProfileAndFetchPatients()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.doctorDrawerLayout)
        navView = findViewById(R.id.navView)
        menuIcon = findViewById(R.id.menuIcon)

        // List Pasien
        rvPasien = findViewById(R.id.rv_today_patients)
        tvEmptyState = findViewById(R.id.tv_empty_state)

        // Header Info Pasien
        tvCurrentName = findViewById(R.id.tv_current_patient_name)
        tvCurrentPoli = findViewById(R.id.tv_current_patient_poli)
        tvCurrentTime = findViewById(R.id.tv_current_time)
        tvCurrentStatus = findViewById(R.id.tv_current_status)
    }

    private fun setupRecyclerView() {
        rvPasien.layoutManager = LinearLayoutManager(this)

        // Inisialisasi DoctorTaskAdapter
        adapter = DoctorTaskAdapter(appointmentList) { selectedAppt ->
            // Callback saat item diklik: Tampilkan Dialog Ubah Status
            showStatusDialog(selectedAppt)
        }
        rvPasien.adapter = adapter
    }

    private fun loadDoctorProfileAndFetchPatients() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid

            // Ambil referensi header sidebar untuk update nama dokter
            val headerView = navView.getHeaderView(0)
            val tvName = headerView.findViewById<TextView>(R.id.doctorName)
            val tvSpec = headerView.findViewById<TextView>(R.id.doctorSpecialty)

            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Ambil nama dokter dari profil
                        val name = document.getString("name") ?: document.getString("username") ?: "Dokter"
                        val spec = document.getString("poli") ?: "Dokter Umum"

                        // Update UI Sidebar
                        tvName?.text = name
                        tvSpec?.text = spec

                        // Simpan nama dokter untuk filter query appointment
                        currentDoctorName = name

                        // Mulai ambil data appointment
                        fetchAppointmentsFirestore(currentDoctorName)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal memuat profil dokter", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun fetchAppointmentsFirestore(doctorName: String) {
        // Query: Cari appointment milik dokter ini yang statusnya aktif
        // Urutkan berdasarkan waktu (Ascending/Pagi ke Siang)
        firestoreListener = db.collection("appointments")
            .whereEqualTo("doctorName", doctorName)
            .whereIn("status", listOf("menunggu", "diproses", "sedang diperiksa"))
            .orderBy("selectedTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // Handle error (biasanya karena belum index)
                    return@addSnapshotListener
                }

                appointmentList.clear()

                if (snapshots != null && !snapshots.isEmpty) {
                    for (doc in snapshots) {
                        val appt = doc.toObject(Appointment::class.java)
                        appt.id = doc.id // Simpan ID dokumen untuk update nanti
                        appointmentList.add(appt)
                    }

                    // Update UI jika ada data
                    tvEmptyState.visibility = View.GONE
                    rvPasien.visibility = View.VISIBLE

                    // Tampilkan pasien urutan pertama di Header "Pasien Saat Ini"
                    updateCurrentPatientHeader(appointmentList.firstOrNull())
                } else {
                    // Update UI jika kosong
                    tvEmptyState.visibility = View.VISIBLE
                    rvPasien.visibility = View.GONE
                    updateCurrentPatientHeader(null)
                }

                adapter.notifyDataSetChanged()
            }
    }

    private fun updateCurrentPatientHeader(appt: Appointment?) {
        if (appt != null) {
            tvCurrentName.text = appt.patientName ?: "Tanpa Nama"
            tvCurrentPoli.text = appt.poli ?: "Umum"
            tvCurrentTime.text = "Jam: ${appt.selectedTime}"
            tvCurrentStatus.text = appt.status?.uppercase() ?: "-"
        } else {
            tvCurrentName.text = "Tidak ada pasien"
            tvCurrentPoli.text = "-"
            tvCurrentTime.text = "--:--"
            tvCurrentStatus.text = "STANDBY"
        }
    }

    private fun showStatusDialog(appointment: Appointment) {
        // Pilihan aksi untuk dokter
        val options = arrayOf("Mulai Periksa (Hijau)", "Selesai Periksa (Hapus dari List)", "Batal")

        AlertDialog.Builder(this)
            .setTitle("Aksi: ${appointment.patientName}")
            .setItems(options) { _, which ->
                val docId = appointment.id ?: return@setItems
                var newStatus = ""

                when (which) {
                    0 -> newStatus = "diproses" // Ubah warna jadi hijau
                    1 -> newStatus = "selesai"  // Akan hilang dari list karena filter query
                    2 -> return@setItems // Batal
                }

                if (newStatus.isNotEmpty()) {
                    updateStatusInFirestore(docId, newStatus)
                }
            }
            .show()
    }

    private fun updateStatusInFirestore(docId: String, status: String) {
        db.collection("appointments").document(docId)
            .update("status", status)
            .addOnSuccessListener {
                Toast.makeText(this, "Status diubah menjadi $status", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal update status", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupNavigation() {
        menuIcon.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    auth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                // Tambahkan case lain jika ada menu lain di sidebar dokter
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hentikan listener realtime saat activity ditutup untuk hemat baterai/data
        firestoreListener?.remove()
    }
}