package com.example.hospital_apps

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // <- pastikan ini activity_main.xml kamu

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)
        menuIcon = findViewById(R.id.menuIcon)

        // pastikan drawer muncul di atas konten
        navView.bringToFront()

        // klik hamburger buka drawer
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // klik item di drawer
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_antrian -> showToast("Cek Antrian diklik")
                R.id.nav_jadwal -> showToast("Jadwal Dokter diklik")
                R.id.nav_riwayat -> showToast("Riwayat diklik")
                R.id.nav_profil -> showToast("Profil diklik")
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_antrian -> {
                    val intent = Intent(this, AntrianActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_jadwal -> showToast("Jadwal Dokter diklik")

                R.id.nav_riwayat -> {
                    val intent = Intent(this, PerawatanActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_profil -> {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                }
            }


            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


    }

    private fun showToast(msg: String) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
    }
}
