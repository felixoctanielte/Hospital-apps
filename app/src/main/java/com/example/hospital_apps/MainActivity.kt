package com.example.hospital_apps

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    // Tambahkan di atas onCreate
    private lateinit var geoMapPreview: WebView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuIcon: ImageView

    private lateinit var btnCamera: ImageButton
    private lateinit var btnGallery: Button
    private lateinit var geoMap: WebView // tambahkan WebView untuk peta

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

        // === Geoapify Map WebView ===
        geoMap = findViewById(R.id.geoMapPreview)
        val webSettings: WebSettings = geoMap.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        geoMap.webViewClient = WebViewClient()

        // ==== Ganti API KEY milik kamu di sini ====
        val apiKey = "283f56a80bb04fd6a65cd9b98b46f18e"

        // Koordinat contoh (RS Land Of Dawn)
        val latitude = -6.200000
        val longitude = 106.816666
        val zoom = 13

        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
                <style>
                    html, body { height: 100%; margin: 0; }
                    #map { width: 100%; height: 100%; border-radius: 12px; }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    var map = L.map('map').setView([$latitude, $longitude], $zoom);
                    L.tileLayer('https://maps.geoapify.com/v1/tile/osm-carto/{z}/{x}/{y}.png?apiKey=$apiKey', {
                        attribution: '© OpenStreetMap contributors, © Geoapify',
                        maxZoom: 20,
                    }).addTo(map);
                    L.marker([$latitude, $longitude]).addTo(map)
                        .bindPopup('RS Land Of Dawn<br>Jakarta')
                        .openPopup();
                </script>
            </body>
            </html>
        """.trimIndent()

        geoMap.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)

        // ====== Drawer Menu Handling ======
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        findViewById<LinearLayout>(R.id.mapBox).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
        geoMapPreview = findViewById(R.id.geoMapPreview)

// Tampilkan peta dengan MapTiler atau OpenStreetMap (tanpa API berbayar)
        val mapUrl = "https://www.openstreetmap.org/export/embed.html?bbox=106.8227, -6.1754, 106.8456, -6.1600&layer=mapnik"
        geoMapPreview.settings.javaScriptEnabled = true
        geoMapPreview.loadUrl(mapUrl)


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

        val btnPsikiater = findViewById<LinearLayout>(R.id.btn_psikiater)
        val btnJantung = findViewById<LinearLayout>(R.id.btn_jantung)
        val btnGigi = findViewById<LinearLayout>(R.id.btn_gigi)
        val btnKehamilan = findViewById<LinearLayout>(R.id.btn_kehamilan)
        val btnParu = findViewById<LinearLayout>(R.id.btn_paru)
        val btnLainnya = findViewById<LinearLayout>(R.id.btn_lainnya)


        btnPsikiater.setOnClickListener { openDiseaseSearch("mental disorder") }
        btnJantung.setOnClickListener { openDiseaseSearch("heart disease") }
        btnGigi.setOnClickListener { openDiseaseSearch("tooth infection") }
        btnKehamilan.setOnClickListener { openDiseaseSearch("pregnancy") }
        btnParu.setOnClickListener { openDiseaseSearch("lung disease") }
        btnLainnya.setOnClickListener { openDiseaseSearch("common illness") }

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
         Dokter: dr. Andi, Sp.JP
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
