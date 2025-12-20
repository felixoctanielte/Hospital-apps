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
        setContentView(R.layout.activity_doctor_page)

        // 1. Init Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // 2. Init Views
        initViews()

        // 3. Setup RecyclerView
        setupRecyclerView()

        // 4. Setup Navigasi Sidebar (Logika Klik Menu)
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

        // Header Info Dashboard
        tvCurrentName = findViewById(R.id.tv_current_patient_name)
        tvCurrentPoli = findViewById(R.id.tv_current_patient_poli)
        tvCurrentTime = findViewById(R.id.tv_current_time)
        tvCurrentStatus = findViewById(R.id.tv_current_status)
    }

    private fun setupRecyclerView() {
        // Menggunakan Horizontal scroll sesuai desain layout XML
        rvPasien.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        adapter = DoctorTaskAdapter(appointmentList) { selectedAppt ->
            showStatusDialog(selectedAppt)
        }
        rvPasien.adapter = adapter
    }

    private fun setupNavigation() {
        // 1. Tombol Buka Sidebar (Hamburger Menu)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 2. Logika Klik Menu di Sidebar
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    // Tutup drawer saja karena sudah di dashboard
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_antrian -> {
                    Toast.makeText(this, "Fitur Antrian Terbuka", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_riwayat -> {
                    Toast.makeText(this, "Fitur Riwayat Terbuka", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_profile -> {
                    // Direct ke Profil Dokter
                    startActivity(Intent(this, DoctorProfileActivity::class.java))
                }
                R.id.nav_logout -> {
                    auth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // 3. Logika Klik Header Sidebar (Area Foto/Nama)
        val headerView = navView.getHeaderView(0)
        headerView.setOnClickListener {
            startActivity(Intent(this, DoctorProfileActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun loadDoctorProfileAndFetchPatients() {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        // Ambil referensi header sidebar untuk update info dokter
        val headerView = navView.getHeaderView(0)
        val tvName = headerView.findViewById<TextView>(R.id.doctorName)
        val tvSpec = headerView.findViewById<TextView>(R.id.doctorSpecialty)

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "Dokter"
                    val spec = document.getString("poli") ?: "Umum"

                    tvName?.text = name
                    tvSpec?.text = "Spesialis $spec"

                    currentDoctorName = name
                    // Ambil data janji temu berdasarkan nama dokter ini
                    fetchAppointmentsFirestore(currentDoctorName)
                }
            }
    }

    private fun fetchAppointmentsFirestore(doctorName: String) {
        firestoreListener = db.collection("appointments")
            .whereEqualTo("doctorName", doctorName)
            .whereIn("status", listOf("menunggu", "diproses", "sedang diperiksa", "Booked"))
            .orderBy("selectedTime", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                appointmentList.clear()

                if (snapshots != null && !snapshots.isEmpty) {
                    for (doc in snapshots) {
                        val appt = doc.toObject(Appointment::class.java)
                        appt.id = doc.id
                        appointmentList.add(appt)
                    }

                    tvEmptyState.visibility = View.GONE
                    rvPasien.visibility = View.VISIBLE
                    updateCurrentPatientHeader(appointmentList.firstOrNull())
                } else {
                    tvEmptyState.visibility = View.VISIBLE
                    rvPasien.visibility = View.GONE
                    updateCurrentPatientHeader(null)
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun updateCurrentPatientHeader(appt: Appointment?) {
        if (appt != null) {
            tvCurrentName.text = appt.patientName ?: "Pasien"
            tvCurrentPoli.text = appt.poli ?: "-"
            tvCurrentTime.text = "Jam: ${appt.selectedTime}"
            tvCurrentStatus.text = appt.status?.uppercase() ?: "ANTRI"
        } else {
            tvCurrentName.text = "Menunggu Pasien..."
            tvCurrentPoli.text = "-"
            tvCurrentTime.text = "--:--"
            tvCurrentStatus.text = "STANDBY"
        }
    }

    private fun showStatusDialog(appointment: Appointment) {
        val options = arrayOf("Panggil Pasien", "Selesai Periksa", "Batal")

        AlertDialog.Builder(this)
            .setTitle("Aksi untuk ${appointment.patientName}")
            .setItems(options) { _, which ->
                val docId = appointment.id ?: return@setItems
                when (which) {
                    0 -> updateStatusInFirestore(docId, "diproses")
                    1 -> updateStatusInFirestore(docId, "selesai")
                }
            }
            .show()
    }

    private fun updateStatusInFirestore(docId: String, status: String) {
        db.collection("appointments").document(docId)
            .update("status", status)
            .addOnSuccessListener {
                Toast.makeText(this, "Status diperbarui", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        firestoreListener?.remove()
    }
}