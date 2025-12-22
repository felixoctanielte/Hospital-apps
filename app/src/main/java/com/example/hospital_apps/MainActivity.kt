package com.example.hospital_apps

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    // Variable UI
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuIcon: ImageView
    private lateinit var btnCamera: ImageButton
    private lateinit var geoMap: WebView
    private lateinit var tvWelcomeName: TextView
    private lateinit var btnProfileHeader: CardView

    // Dashboard Cards
    private lateinit var cardGuestMode: CardView
    private lateinit var cardQueueEmpty: CardView
    private lateinit var cardQueueActive: CardView
    private lateinit var btnLoginGuest: Button

    // Antrian
    private lateinit var tvDoctorName: TextView
    private lateinit var tvPoliName: TextView
    private lateinit var tvQueueNumber: TextView
    private lateinit var tvHospitalName: TextView

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // --- TAMBAHAN UNTUK AI/ML ---
    private lateinit var apiService: ApiService

    // Launcher Scan
    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val scannedData = result.contents
            Toast.makeText(this, "Scan Berhasil! Sedang menganalisis...", Toast.LENGTH_SHORT).show()
            processScannedData(scannedData)
        } else {
            Toast.makeText(this, "Scan Dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Setup Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Cek Role Dokter
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")
                    if (role == "dokter") {
                        startActivity(Intent(this, DoctorActivity::class.java))
                        finish()
                    }
                }
        }

        // 2. Setup Retrofit dengan TIMEOUT PANJANG (Solusi Server Lambat)
        try {
            // Setting Timeout jadi 60 Detik (Default Android cuma 10 detik)
            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // Waktu nyambung ke server
                .readTimeout(60, TimeUnit.SECONDS)    // Waktu nunggu server mikir (AI process)
                .writeTimeout(60, TimeUnit.SECONDS)   // Waktu kirim data
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://levonchka.pythonanywhere.com/") // Pastikan akhiran '/'
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // <--- PENTING: Memasukkan settingan timeout tadi
                .build()

            apiService = retrofit.create(ApiService::class.java)

        } catch (e: Exception) {
            Toast.makeText(this, "Error Config: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        try {
            initViews()
            setupListeners()
            setupMap()
            updateUI()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error Init: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        updateDashboardUI()
        updateNavigationHeader()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)
        menuIcon = findViewById(R.id.menuIcon)
        btnCamera = findViewById(R.id.btn_camera)
        geoMap = findViewById(R.id.geoMapPreview)
        tvWelcomeName = findViewById(R.id.tvWelcomeName)
        btnProfileHeader = findViewById(R.id.btnProfileHeader)

        cardGuestMode = findViewById(R.id.cardGuestMode)
        cardQueueEmpty = findViewById(R.id.cardQueueEmpty)
        cardQueueActive = findViewById(R.id.cardQueueActive)
        btnLoginGuest = findViewById(R.id.btnLoginGuest)

        tvDoctorName = findViewById(R.id.tvDoctorName)
        tvPoliName = findViewById(R.id.tvPoliName)
        tvQueueNumber = findViewById(R.id.tvQueueNumber)
        tvHospitalName = findViewById(R.id.tvHospitalName)
    }

    private fun setupListeners() {
        menuIcon.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START)
            else
                drawerLayout.openDrawer(GravityCompat.START)
        }

        btnProfileHeader.setOnClickListener {
            if (auth.currentUser != null) {
                startActivity(Intent(this, UserActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_antrian -> startActivity(Intent(this, PasienAntrianActivity::class.java))
                R.id.nav_jadwal -> startActivity(Intent(this, ScheduleActivity::class.java))
                R.id.nav_riwayat -> startActivity(Intent(this, PasienRiwayatActivity::class.java))
                R.id.nav_profil -> startActivity(Intent(this, UserActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        btnLoginGuest.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnCamera.setOnClickListener {
            val options = ScanOptions()
            options.setCaptureActivity(CustomScannerActivity::class.java)
            options.setPrompt("Arahkan kamera ke QR Code Pasien")
            options.setOrientationLocked(true)
            options.setBeepEnabled(true)
            scanLauncher.launch(options)
        }

        setupCategoryClick(R.id.btn_jantung, "heart disease")
        setupCategoryClick(R.id.btn_gigi, "tooth infection")
        setupCategoryClick(R.id.btn_paru, "lung disease")
        setupCategoryClick(R.id.btn_psikiater, "mental disorder")
        setupCategoryClick(R.id.btn_kehamilan, "pregnancy")
        setupCategoryClick(R.id.btn_lainnya, "common illness")
    }

    private fun setupCategoryClick(id: Int, query: String) {
        findViewById<View>(id)?.setOnClickListener {
            val intent = Intent(this, DiseaseSearchActivity::class.java)
            intent.putExtra("category", query)
            startActivity(intent)
        }
    }

    // ==========================================
    // BAGIAN LOGIKA AI / MACHINE LEARNING
    // ==========================================

    private fun processScannedData(jsonString: String) {
        // Tampilkan Loading Dialog (Opsional tapi bagus untuk User Experience)
        Toast.makeText(this, "Sedang menghubungi AI Server...", Toast.LENGTH_LONG).show()

        try {
            val gson = Gson()
            val qrData = gson.fromJson(jsonString, PredictionRequest::class.java)

            if (qrData.age > 0) {
                val now = Date()
                val hourFormat = SimpleDateFormat("H", Locale.getDefault())
                val dayFormat = SimpleDateFormat("u", Locale.getDefault())

                val realTimeHour = hourFormat.format(now).toInt()
                val realTimeDay = dayFormat.format(now).toInt()
                val isWeekday = if (realTimeDay <= 5) 1 else 0

                val finalData = qrData.copy(
                    hour1 = realTimeHour,
                    weekday1 = isWeekday
                )

                sendToAI(finalData)
            } else {
                Toast.makeText(this, "Data QR Tidak Lengkap", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Format QR Salah! Gunakan QR Pasien.", Toast.LENGTH_LONG).show()
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
                        showAIResultDialog("ERROR SERVER", res?.message ?: "Unknown Error", data)
                    }
                } else {
                    // Menangani error HTTP (misal 404, 500)
                    Toast.makeText(applicationContext, "Gagal: Server Error code ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                // Menangani error koneksi (Timeout, No Internet)
                val pesanError = if (t.message?.contains("timeout") == true) {
                    "Server sibuk (Timeout). Coba lagi dalam 10 detik."
                } else {
                    "Koneksi Gagal: ${t.localizedMessage}"
                }

                Toast.makeText(applicationContext, pesanError, Toast.LENGTH_LONG).show()
                Log.e("API_ERROR", "Detail: ${t.message}")
            }
        })
    }

    private fun showAIResultDialog(status: String, probability: String, inputData: PredictionRequest) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_scan_result, null)
        val txtData = dialogView.findViewById<TextView>(R.id.txt_patient_data)
        val btnClose = dialogView.findViewById<Button>(R.id.btn_close)

        val now = Date()
        val formatLengkap = SimpleDateFormat("EEEE, dd MMM yyyy, HH:mm", Locale("id", "ID"))
        val waktuStr = formatLengkap.format(now) + " WIB"

        val probPersen = if (status == "ERROR SERVER") "-" else {
            try {
                val p = probability.toDouble()
                val conf = if(status.contains("CEPAT")) (1.0 - p) * 100 else p * 100
                String.format("%.0f%%", conf)
            } catch (e: Exception) { "0%" }
        }

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

    // ... LOGIKA UI LAINNYA TETAP SAMA SEPERTI SEBELUMNYA ...
    // ... (updateDashboardUI, updateNavigationHeader, checkActiveQueue, setupMap) ...
    // copy paste saja bagian bawahnya dari kode sebelumnya kalau belum ada
    private fun updateNavigationHeader() {
        val headerView = navView.getHeaderView(0)
        val tvHeaderName = headerView.findViewById<TextView>(R.id.tvHeaderName)
        val tvHeaderStatus = headerView.findViewById<TextView>(R.id.tvHeaderStatus)

        val currentUser = auth.currentUser

        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val name = document.getString("name") ?: document.getString("username") ?: "User"
                    tvHeaderName.text = name
                    tvHeaderStatus.text = "KELUAR AKUN"
                    tvHeaderStatus.setOnClickListener {
                        auth.signOut()
                        Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                        updateUI()
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                }
        } else {
            tvHeaderName.text = "Selamat Datang"
            tvHeaderStatus.text = "MASUK / DAFTAR"
            tvHeaderStatus.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
    }

    private fun updateDashboardUI() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            tvWelcomeName.text = "Halo, Tamu"
            cardGuestMode.visibility = View.VISIBLE
            cardQueueEmpty.visibility = View.GONE
            cardQueueActive.visibility = View.GONE
        } else {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val name = document.getString("name") ?: document.getString("username") ?: "User"
                    tvWelcomeName.text = "Halo, $name"
                    checkActiveQueue(currentUser.uid)
                }
                .addOnFailureListener {
                    tvWelcomeName.text = "Halo, User"
                }
        }
    }

    private fun checkActiveQueue(uid: String) {
        db.collection("appointments")
            .whereEqualTo("userId", uid)
            .whereIn("status", listOf("menunggu", "diproses"))
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]

                    cardGuestMode.visibility = View.GONE
                    cardQueueEmpty.visibility = View.GONE
                    cardQueueActive.visibility = View.VISIBLE

                    tvDoctorName.text = doc.getString("doctorName") ?: "Dokter"
                    tvPoliName.text = doc.getString("poli") ?: "Poli Umum"
                    tvQueueNumber.text = doc.getString("queueNumber") ?: "-"
                } else {
                    cardGuestMode.visibility = View.GONE
                    cardQueueEmpty.visibility = View.VISIBLE
                    cardQueueActive.visibility = View.GONE
                }
            }
            .addOnFailureListener {
                cardGuestMode.visibility = View.GONE
                cardQueueEmpty.visibility = View.VISIBLE
                cardQueueActive.visibility = View.GONE
            }
    }


    private fun setupMap() {
        geoMap.settings.javaScriptEnabled = true
        geoMap.webViewClient = WebViewClient()
        val apiKey = "283f56a80bb04fd6a65cd9b98b46f18e"
        val htmlContent = """
            <!DOCTYPE html><html><head><meta name="viewport" content="width=device-width, initial-scale=1.0"><link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" /><script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script><style>html, body { height: 100%; margin: 0; } #map { width: 100%; height: 100%; }</style></head><body><div id="map"></div><script>var map = L.map('map').setView([-6.200000, 106.816666], 13);L.tileLayer('https://maps.geoapify.com/v1/tile/osm-carto/{z}/{x}/{y}.png?apiKey=$apiKey', {maxZoom: 20,}).addTo(map);L.marker([-6.200000, 106.816666]).addTo(map).bindPopup('RS Land Of Dawn');</script></body></html>
        """.trimIndent()

        geoMap.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }
}