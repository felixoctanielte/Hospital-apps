package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class DoctorActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_page)

        // --- 🔹 Inisialisasi drawer dan navigasi ---
        drawerLayout = findViewById(R.id.doctorDrawerLayout)
        navView = findViewById(R.id.navView)
        menuIcon = findViewById(R.id.menuIcon)

        setupHeader()          // Setup header dokter
        setupMenuActions()     // Setup click menu drawer
    }

    // --- 🔹 Fungsi untuk setup header profil dokter ---
    private fun setupHeader() {
        val headerView = navView.getHeaderView(0)
        val doctorName = headerView.findViewById<TextView>(R.id.doctorName)
        val doctorSpecialty = headerView.findViewById<TextView>(R.id.doctorSpecialty)
        val doctorImage = headerView.findViewById<ImageView>(R.id.doctorImage)

        // Data sementara, nanti bisa ambil dari DB
        doctorName.text = "Dr. John Doe"
        doctorSpecialty.text = "Spesialis Jantung"
        doctorImage.setImageResource(R.drawable.doctor)
    }

    // --- 🔹 Fungsi untuk setup menu drawer ---
    private fun setupMenuActions() {
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_antrian -> startActivity(Intent(this, AntrianActivity::class.java))
                R.id.nav_riwayat -> startActivity(Intent(this, PerawatanActivity::class.java))
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfilActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_logout -> {
                    // Tambahkan logika logout di sini
                    startActivity(Intent(this, MainActivity::class.java))

                }

                else -> return@setNavigationItemSelectedListener false
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


    }

    // --- 🔹 Optional: menutup drawer saat tekan back ---
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
