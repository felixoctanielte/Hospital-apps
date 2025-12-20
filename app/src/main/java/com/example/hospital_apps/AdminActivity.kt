package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AdminActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuIcon: ImageView

    private lateinit var totalPasienTV: TextView
    private lateinit var totalDokterTV: TextView
    private lateinit var totalPoliTV: TextView

    private lateinit var tableAktivitas: TableLayout
    private var aktivitasListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigationView)
        menuIcon = findViewById(R.id.menuIcon)

        totalPasienTV = findViewById(R.id.totalPasienTV)
        totalDokterTV = findViewById(R.id.totalDokterTV)
        totalPoliTV = findViewById(R.id.totalPoliTV)
        tableAktivitas = findViewById(R.id.tableAktivitas)

        Toast.makeText(this, "Berhasil masuk ke halaman Admin", Toast.LENGTH_SHORT).show()

        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        setupDrawerContent(navView)
        loadStatistics()
        listenAktivitasRealtime()
    }

    private fun loadStatistics() {
        db.collection("pasien").get()
            .addOnSuccessListener { totalPasienTV.text = it.size().toString() }
            .addOnFailureListener { totalPasienTV.text = "0" }

        db.collection("dokter").get()
            .addOnSuccessListener { totalDokterTV.text = it.size().toString() }
            .addOnFailureListener { totalDokterTV.text = "0" }

        db.collection("poli").get()
            .addOnSuccessListener { totalPoliTV.text = it.size().toString() }
            .addOnFailureListener { totalPoliTV.text = "0" }
    }

    /**
     * =========================
     * |  REALTIME AKTIVITAS  |
     * =========================
     * sumber dari koleksi appointments
     */
    private fun listenAktivitasRealtime() {

        aktivitasListener = db.collection("appointments")
            .orderBy("selectedTime")
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) return@addSnapshotListener

                // hapus baris lama kecuali header
                if (tableAktivitas.childCount > 1) {
                    tableAktivitas.removeViews(1, tableAktivitas.childCount - 1)
                }

                for (doc in snapshot.documents) {

                    val waktuTxt = doc.getString("selectedTime") ?: "-"
                    val dokter = doc.getString("doctorName") ?: "-"
                    val pasien = doc.getString("patientName") ?: "-"
                    val poli = doc.getString("poli") ?: "-"
                    val status = doc.getString("status") ?: "-"

                    val aktivitasTxt = "$dokter - $pasien ($poli)"

                    val row = TableRow(this)

                    val waktu = TextView(this).apply { text = waktuTxt }
                    val aktivitas = TextView(this).apply { text = aktivitasTxt }
                    val keterangan = TextView(this).apply { text = status }

                    row.addView(waktu)
                    row.addView(aktivitas)
                    row.addView(keterangan)

                    tableAktivitas.addView(row)
                }
            }
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.nav_master_data -> {
                    val intent = Intent(this, ShowActivity::class.java)
                    intent.putExtra("type", "dokter")
                    startActivity(intent)
                }

                R.id.nav_laporan -> {
                    val intent = Intent(this, AdminLaporanActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_logout -> {
                    auth.signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }

            drawerLayout.closeDrawer(GravityCompat.START)

            for (i in 0 until navigationView.menu.size()) {
                navigationView.menu.getItem(i).isChecked = false
            }
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        aktivitasListener?.remove()
    }
}
