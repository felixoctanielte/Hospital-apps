package com.example.hospital_apps

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class AdminActivity : AppCompatActivity() {

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

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_master_data ->
                    Toast.makeText(this, "Buka Master Data", Toast.LENGTH_SHORT).show()
                R.id.nav_verifikasi ->
                    Toast.makeText(this, "Buka Verifikasi", Toast.LENGTH_SHORT).show()
                R.id.nav_laporan ->
                    Toast.makeText(this, "Buka Laporan", Toast.LENGTH_SHORT).show()
                R.id.nav_cetak ->
                    Toast.makeText(this, "Buka Cetak Data", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}
