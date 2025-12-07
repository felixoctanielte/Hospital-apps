package com.example.hospital_apps

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuIcon: ImageView
    private lateinit var btnCamera: ImageButton
    private lateinit var geoMap: WebView

    private lateinit var apiService: ApiService

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val scannedData = result.contents
            Toast.makeText(this, "Scan Berhasil! Memproses...", Toast.LENGTH_SHORT).show()
            processScannedData(scannedData)
        } else {
            Toast.makeText(this, "Scan Dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)
        menuIcon = findViewById(R.id.menuIcon)
        btnCamera = findViewById(R.id.btn_camera)
        geoMap = findViewById(R.id.geoMapPreview)

        setupMap()
        setupNavigation()
        setupCategoryButtons()

        // GANTI IP DI SINI
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.108:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        btnCamera.setOnClickListener {
            val options = ScanOptions()
            options.setPrompt("Arahkan kamera ke QR Code Pasien")
            options.setBeepEnabled(true)
            options.setOrientationLocked(true)
            options.setBarcodeImageEnabled(false)
            scanLauncher.launch(options)
        }
    }

    private fun processScannedData(jsonString: String) {
        try {
            val gson = Gson()
            val qrData = gson.fromJson(jsonString, PredictionRequest::class.java)

            if (qrData.age > 0) {
                // === LOGIKA REAL-TIME ===
                // Kita ganti jam & hari dari QR dengan Waktu HP Saat Ini
                val now = Date()
                val hourFormat = SimpleDateFormat("H", Locale.getDefault()) // 0-23
                val dayFormat = SimpleDateFormat("u", Locale.getDefault()) // 1=Senin

                val realTimeHour = hourFormat.format(now).toInt()
                val realTimeDay = dayFormat.format(now).toInt()
                val isWeekday = if (realTimeDay <= 5) 1 else 0

                // Gabungkan Data QR + Data Realtime
                // Pastikan PredictionRequest adalah 'data class'
                val finalData = qrData.copy(
                    hour1 = realTimeHour,
                    weekday1 = isWeekday
                )

                sendToAI(finalData)
            } else {
                Toast.makeText(this, "Data QR Tidak Lengkap", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "QR Error!", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendToAI(data: PredictionRequest) {
        apiService.predictWaitTime(data).enqueue(object : Callback<PredictionResponse> {
            override fun onResponse(call: Call<PredictionResponse>, response: Response<PredictionResponse>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    if (res?.status == "success") {
                        showAIResultDialog(res.result_text ?: "-", res.probability.toString(), data)
                    } else {
                        showAIResultDialog("ERROR SERVER", res?.message ?: "-", data)
                    }
                } else {
                    Toast.makeText(applicationContext, "Gagal Koneksi", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showAIResultDialog(status: String, probability: String, inputData: PredictionRequest) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_scan_result, null)
        val txtData = dialogView.findViewById<TextView>(R.id.txt_patient_data)
        val btnClose = dialogView.findViewById<Button>(R.id.btn_close)

        // 1. Waktu Tampilan
        val now = Date()
        val formatLengkap = SimpleDateFormat("EEEE, dd MMM yyyy, HH:mm", Locale("id", "ID"))
        val waktuStr = formatLengkap.format(now) + " WIB"

        // 2. Probabilitas
        val probPersen = if (status == "ERROR SERVER") "-" else {
            try {
                val p = probability.toDouble()
                val conf = if(status.contains("CEPAT")) (1.0 - p) * 100 else p * 100
                String.format("%.0f%%", conf)
            } catch (e: Exception) { "0%" }
        }

        // 3. Info Lain
        val rsStr = if (inputData.crowd1 == 1) "Sangat Ramai (High Traffic)" else "Lengang (Low Traffic)"

        val pesanHasil = """
            ===== HASIL ANALISIS AI =====
            
            Estimasi:
            $status
            
            Keyakinan AI:
            $probPersen
            
            -----------------------------
            DETAIL:
            • Waktu : $waktuStr
            • Pasien : Umur ${inputData.age} Tahun
            • Kondisi RS : $rsStr
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

    // ... (Helper Functions Map, Navigation, Category tetap sama seperti sebelumnya) ...
    // ... Copy-paste dari kode sebelumnya saja untuk bagian ini ...
    // --- Helper: Setup Map ---
    private fun setupMap() {
        val webSettings: WebSettings = geoMap.settings
        webSettings.javaScriptEnabled = true
        geoMap.webViewClient = WebViewClient()
        val apiKey = "283f56a80bb04fd6a65cd9b98b46f18e"
        val htmlContent = """
            <!DOCTYPE html><html><head><meta name="viewport" content="width=device-width, initial-scale=1.0"><link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" /><script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script><style>html, body { height: 100%; margin: 0; } #map { width: 100%; height: 100%; }</style></head><body><div id="map"></div><script>var map = L.map('map').setView([-6.200000, 106.816666], 13);L.tileLayer('https://maps.geoapify.com/v1/tile/osm-carto/{z}/{x}/{y}.png?apiKey=$apiKey', {maxZoom: 20,}).addTo(map);L.marker([-6.200000, 106.816666]).addTo(map).bindPopup('RS Land Of Dawn');</script></body></html>
        """.trimIndent()
        geoMap.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    // --- Helper: Navigation ---
    private fun setupNavigation() {
        menuIcon.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        findViewById<LinearLayout>(R.id.mapBox).setOnClickListener { startActivity(Intent(this, MapActivity::class.java)) }
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
        headerView.findViewById<TextView>(R.id.txtLogin).setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }
    }

    // --- Helper: Category Buttons ---
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