package com.example.hospital_apps

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var apiService: ApiService

    // Launcher untuk hasil scan QR Code
    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            Toast.makeText(this, "Scan Berhasil! Memproses...", Toast.LENGTH_SHORT).show()
            processScannedData(result.contents)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 2. CEK SESI LOGIN (PENTING)
        // Memastikan Dokter/Admin yang salah masuk dashboard langsung dilempar keluar
        checkUserSession()

        setContentView(R.layout.activity_main)

        // 3. Inisialisasi View
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)
        menuIcon = findViewById(R.id.menuIcon)
        btnCamera = findViewById(R.id.btn_camera)
        geoMap = findViewById(R.id.geoMapPreview)

        // 4. Setup Fitur-fitur Dashboard
        setupMap()
        setupNavigation()
        setupCategoryButtons()
        setupRetrofit()
        updateSidebarUser()

        // 5. Tombol Kamera untuk AI Prediction
        btnCamera.setOnClickListener {
            val options = ScanOptions()
            options.setPrompt("Arahkan kamera ke QR Code Pasien")
            options.setBeepEnabled(true)
            options.setOrientationLocked(true)
            scanLauncher.launch(options)
        }
    }

    override fun onResume() {
        super.onResume()
        // Update nama user di sidebar setiap kali halaman ini tampil
        updateSidebarUser()
    }

    // --- FUNGSI KEAMANAN: MENCEGAH DOKTER NYANGKUT DI SINI ---
    private fun checkUserSession() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val uid = currentUser.uid
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val role = document.getString("role")?.toLowerCase(Locale.getDefault())

                        when (role) {
                            "dokter" -> {
                                // Lempar ke Dashboard Dokter
                                val intent = Intent(this, DoctorActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            "admin" -> {
                                // Lempar ke Dashboard Admin
                                val intent = Intent(this, AdminActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            // Jika "pasien", tetap di sini (Aman)
                        }
                    }
                }
        }
    }

    // --- SETUP NAVIGASI SIDEBAR (SUDAH DIPERBARUI) ---
    private fun setupNavigation() {
        menuIcon.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        // Klik pada Peta Mini
        findViewById<LinearLayout>(R.id.mapBox).setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // UPDATE: Mengarah ke halaman antrian khusus pasien
                R.id.nav_antrian -> startActivity(Intent(this, PasienAntrianActivity::class.java))

                R.id.nav_jadwal -> startActivity(Intent(this, ScheduleActivity::class.java))

                // UPDATE: Mengarah ke riwayat khusus pasien (dengan filter)
                R.id.nav_riwayat -> startActivity(Intent(this, PasienRiwayatActivity::class.java))

                R.id.nav_profil -> startActivity(Intent(this, UserActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    // --- UPDATE TAMPILAN SIDEBAR ---
    private fun updateSidebarUser() {
        val headerView = navView.getHeaderView(0)
        val txtUsername = headerView.findViewById<TextView>(R.id.txtUsername)
        val txtLogin = headerView.findViewById<TextView>(R.id.txtLogin)
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val uid = currentUser.uid
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("username") ?: "User"
                        val email = document.getString("email") ?: currentUser.email
                        txtUsername.text = "Halo, $name"
                        txtLogin.text = "Logout ($email)"
                        txtLogin.setOnClickListener { showLogoutDialog() }
                    }
                }
                .addOnFailureListener {
                    txtUsername.text = "Halo, User"
                    txtLogin.text = "Logout"
                    txtLogin.setOnClickListener { showLogoutDialog() }
                }
        } else {
            txtUsername.text = "Guest"
            txtLogin.text = "Sambungkan akun anda?"
            txtLogin.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Keluar")
            .setMessage("Apakah anda yakin ingin logout?")
            .setPositiveButton("Ya") { _, _ ->
                auth.signOut()
                Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // --- SETUP API & AI ---
    private fun setupRetrofit() {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://172.30.5.251:5000/") // Pastikan IP Server Flask benar
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            apiService = retrofit.create(ApiService::class.java)
        } catch (e: Exception) {
            Toast.makeText(this, "Error Init API", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processScannedData(jsonString: String) {
        try {
            val qrData = Gson().fromJson(jsonString, PredictionRequest::class.java)
            if (qrData != null && qrData.age > 0) {
                val now = Date()
                val finalData = qrData.copy(
                    hour1 = SimpleDateFormat("H", Locale.getDefault()).format(now).toInt(),
                    weekday1 = if (SimpleDateFormat("u", Locale.getDefault()).format(now).toInt() <= 5) 1 else 0
                )
                sendToAI(finalData)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "QR Error: Format data salah", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendToAI(data: PredictionRequest) {
        apiService.predictWaitTime(data).enqueue(object : Callback<PredictionResponse> {
            override fun onResponse(call: Call<PredictionResponse>, response: Response<PredictionResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val res = response.body()!!
                    showAIResultDialog(res.result_text ?: "-", res.probability.toString(), data)
                }
            }
            override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Koneksi Error ke Server AI", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showAIResultDialog(status: String, probability: String, inputData: PredictionRequest) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_scan_result, null)
        val txtData = dialogView.findViewById<TextView>(R.id.txt_patient_data)
        val btnClose = dialogView.findViewById<Button>(R.id.btn_close)

        txtData.text = "Estimasi: $status\nKeyakinan: $probability"

        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    // --- SETUP DASHBOARD MENU ---
    private fun setupMap() {
        geoMap.settings.javaScriptEnabled = true
        geoMap.webViewClient = WebViewClient()
        val htmlContent = "<html><body><div id='map' style='width:100%;height:100%;background-color:#e0e0e0;'>Loading Map...</div></body></html>"
        geoMap.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

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
            findViewById<LinearLayout>(id)?.setOnClickListener {
                val intent = Intent(this, DiseaseSearchActivity::class.java)
                intent.putExtra("category", category)
                startActivity(intent)
            }
        }
    }
}