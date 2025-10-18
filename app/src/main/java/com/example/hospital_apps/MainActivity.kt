package com.example.hospital_apps

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuIcon: ImageView

    private lateinit var btnCamera: ImageButton
    private lateinit var btnGallery: Button

    companion object {
        private const val REQUEST_CAMERA = 1001
        private const val REQUEST_GALLERY = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)
        menuIcon = findViewById(R.id.menuIcon)

        btnCamera = findViewById(R.id.btn_camera)


        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

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

        val headerView = navView.getHeaderView(0)
        val txtLogin = headerView.findViewById<TextView>(R.id.txtLogin)
        txtLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }

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

        // Tombol Camera & Gallery dummy
        btnCamera.setOnClickListener { showDummyPatientData("Camera") }
    }

    private fun openDiseaseSearch(category: String) {
        val intent = Intent(this, DiseaseSearchActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }

    private fun showDummyPatientData(source: String) {
        val patientData = """
       
        
         Nama: Dimas Tele
         NIK: 3275012202000005
         Alamat: Jl. Melati No. 12, Tangerang
         No. HP: 0812-3456-7890
         Tanggal Lahir: 22 Feb 2000
         BPJS: 000142553812 (Aktif)
         Poli Tujuan: Poli Jantung
        ‍️ Dokter: dr. Andi, Sp.JP
         Jadwal: 04 Okt 2025, 09:00 - 09:30
    """.trimIndent()

        val dialogView = layoutInflater.inflate(R.layout.dialog_scan_result, null)
        val txtPatientData = dialogView.findViewById<TextView>(R.id.txt_patient_data)
        val btnClose = dialogView.findViewById<Button>(R.id.btn_close)

        txtPatientData.text = patientData

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnClose.setOnClickListener { dialog.dismiss() }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

}
