package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AdminActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigationView)
        menuIcon = findViewById(R.id.menuIcon)

        Toast.makeText(this, "Berhasil masuk ke halaman Admin", Toast.LENGTH_SHORT).show()

        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        setupDrawerContent(navView)

        // Fragment default bisa kosong / dashboard
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, DashboardFragment()) // Fragment kosong / info singkat
            .commit()
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_master_data -> {
                    val intent = Intent(this, ShowActivity::class.java)
                    intent.putExtra("type", "dokter")
                    startActivity(intent)
                }
//                R.id.nav_verifikasi -> {
//                    val intent = Intent(this, VerifikasiPasienActivity::class.java)
//                    startActivity(intent)
//                }
                R.id.nav_laporan -> {
                    Toast.makeText(this, "Menu laporan diklik", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AdminLaporanActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_logout -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            // Tutup drawer
            drawerLayout.closeDrawer(GravityCompat.START)

            // Reset highlight menu
            for (i in 0 until navigationView.menu.size()) {
                navigationView.menu.getItem(i).isChecked = false
            }

            true
        }
    }

}
