package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.widget.*
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

        // buka navigation drawer
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // klik menu drawer
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_antrian -> startActivity(Intent(this, AntrianActivity::class.java))
                R.id.nav_jadwal -> startActivity(Intent(this, ScheduleActivity::class.java))
                R.id.nav_riwayat -> startActivity(Intent(this, PerawatanActivity::class.java))
                R.id.nav_profil -> startActivity(Intent(this, UserActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // login dari header
        val headerView = navView.getHeaderView(0)
        val txtLogin = headerView.findViewById<TextView>(R.id.txtLogin)
        txtLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // =======================================================
        // 🔹 Hubungkan tombol kategori ke DiseaseSearchActivity
        // =======================================================
        val btnPsikiater = findViewById<Button>(R.id.btn_psikiater)
        val btnJantung = findViewById<Button>(R.id.btn_jantung)
        val btnGigi = findViewById<Button>(R.id.btn_gigi)
        val btnKehamilan = findViewById<Button>(R.id.btn_kehamilan)
        val btnParu = findViewById<Button>(R.id.btn_paru)
        val btnLainnya = findViewById<Button>(R.id.btn_lainnya)

        btnPsikiater.setOnClickListener { openDiseaseSearch("mental disorder") }
        btnJantung.setOnClickListener { openDiseaseSearch("heart disease") }
        btnGigi.setOnClickListener { openDiseaseSearch("tooth infection") }
        btnKehamilan.setOnClickListener { openDiseaseSearch("pregnancy") }
        btnParu.setOnClickListener { openDiseaseSearch("lung disease") }
        btnLainnya.setOnClickListener { openDiseaseSearch("common illness") }
    }

    // 🔹 Fungsi untuk membuka halaman pencarian penyakit
    private fun openDiseaseSearch(category: String) {
        val intent = Intent(this, DiseaseSearchActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }
}
