package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)
        menuIcon = findViewById(R.id.menuIcon)

        navView.bringToFront()

        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_antrian -> {
                    val intent = Intent(this, AntrianActivity::class.java)
                    startActivity(intent)
                }


                R.id.nav_jadwal -> {
                    val intent = Intent(this, ScheduleActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_jadwal -> showToast("Jadwal Dokter diklik")


                R.id.nav_riwayat -> {
                    val intent = Intent(this, PerawatanActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_profil -> {

                    showToast("Menu Profil diklik")

                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)

                }
            }


            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}
