package com.example.hospital_apps

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)

        // ambil menu icon custom dari layout
        val menuIcon: ImageView = findViewById(R.id.menuIcon)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START) // buka navigation drawer
        }

        // handle klik menu di drawer
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_antrian -> { /* buka Cek Antrian */ }
                R.id.nav_jadwal -> { /* buka Jadwal Dokter */ }
                R.id.nav_riwayat -> { /* buka Riwayat */ }
                R.id.nav_profil -> { /* buka Profil */ }
            }
            drawerLayout.closeDrawers()
            true
        }
    }
}
