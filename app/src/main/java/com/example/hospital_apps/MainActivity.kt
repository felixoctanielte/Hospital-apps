package com.example.hospital_apps

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuIcon: ImageView
    private lateinit var btnCamera: ImageButton
    private lateinit var geoMap: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Inisialisasi View
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)
        menuIcon = findViewById(R.id.menuIcon)
        btnCamera = findViewById(R.id.btn_camera)
        geoMap = findViewById(R.id.geoMapPreview)

        // 2. Setup Peta (WebView)
        setupMap()

        // 3. Setup Navigasi Drawer
        setupNavigation()

        // 4. Setup Tombol Kategori (Jantung, Gigi, dll)
        setupCategoryButtons()

        // ============================================================
        // 5. INTEGRASI AI (RETROFIT)
        // ============================================================

        // PENTING: Ganti URL ini dengan IP Laptop Anda yang muncul di Terminal Python!
        // Contoh: "http://192.168.1.10:5000/"
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.51.212:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // Logika Tombol Kamera: "Simulasi Scan & Kirim ke AI"
        btnCamera.setOnClickListener {

            // Tampilkan loading sebentar
            Toast.makeText(this, "Mengirim data ke AI...", Toast.LENGTH_SHORT).show()

            // Data Dummy (Seolah-olah hasil scan QR Code pasien)
            // Kasus: Lansia (65), Datang Jam Sibuk (1), RS Ramai (1) -> Harusnya LAMA
            val requestData = PredictionRequest(
                age = 65,
                hour1 = 1,
                weekday1 = 1,
                triagehtn1 = 1,
                abnvs1 = 0,
                moa1 = 0,
                mm1 = 1,
                crowd1 = 1
            )

            // Kirim ke Server Python
            apiService.predictWaitTime(requestData).enqueue(object : Callback<PredictionResponse> {
                override fun onResponse(
                    call: Call<PredictionResponse>,
                    response: Response<PredictionResponse>
                ) {
                    if (response.isSuccessful) {
                        val hasil = response.body()
                        val status = hasil?.result_text ?: "Tidak Diketahui"
                        val prob = hasil?.probability.toString()

                        // Tampilkan Hasil Prediksi Asli dari AI
                        showAIResultDialog(status, prob)
                    } else {
                        Toast.makeText(applicationContext, "Gagal terhubung ke Server", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    // --- Fungsi Helper untuk Menampilkan Dialog Hasil AI ---
    private fun showAIResultDialog(status: String, probability: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_scan_result, null)

        // Asumsi di layout dialog_scan_result ada TextView dengan ID txt_patient_data
        // Kita ubah isinya jadi hasil prediksi
        val txtData = dialogView.findViewById<TextView>(R.id.txt_patient_data)
        val btnClose = dialogView.findViewById<Button>(R.id.btn_close)

        val pesanHasil = """
            ===== HASIL PREDIKSI AI =====
            
            Status Waktu Tunggu:
            $status
            
            Probabilitas (Keyakinan):
            $probability
            
            -----------------------------
            Data Pasien (Simulasi):
            Umur: 65 Tahun
            Jam Datang: Sibuk
            Kondisi RS: Ramai
        """.trimIndent()

        txtData.text = pesanHasil

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    // --- Fungsi Helper Setup Peta ---
    private fun setupMap() {
        val webSettings: WebSettings = geoMap.settings
        webSettings.javaScriptEnabled = true
        geoMap.webViewClient = WebViewClient()

        val apiKey = "283f56a80bb04fd6a65cd9b98b46f18e"
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
                <style>html, body { height: 100%; margin: 0; } #map { width: 100%; height: 100%; }</style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    var map = L.map('map').setView([-6.200000, 106.816666], 13);
                    L.tileLayer('https://maps.geoapify.com/v1/tile/osm-carto/{z}/{x}/{y}.png?apiKey=$apiKey', {
                        maxZoom: 20,
                    }).addTo(map);
                    L.marker([-6.200000, 106.816666]).addTo(map).bindPopup('RS Land Of Dawn');
                </script>
            </body>
            </html>
        """.trimIndent()
        geoMap.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    // --- Fungsi Helper Navigasi ---
    private fun setupNavigation() {
        menuIcon.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        findViewById<LinearLayout>(R.id.mapBox).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
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

        // Header Login
        val headerView = navView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.txtLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    // --- Fungsi Helper Kategori ---
    private fun setupCategoryButtons() {
        val categories = mapOf(
            R.id.btn_psikiater to "mental disorder",
            R.id.btn_jantung to "heart disease",
            R.id.btn_gigi to "tooth infection",
            R.id.btn_kehamilan to "pregnancy",
            R.id.btn_paru to "lung disease",
            R.id.btn_lainnya to "common illness"
        )

        for ((id, category) in categories) {
            findViewById<LinearLayout>(id).setOnClickListener {
                val intent = Intent(this, DiseaseSearchActivity::class.java)
                intent.putExtra("category", category)
                startActivity(intent)
            }
        }
    }
}