package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
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
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {

    // Variable UI
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var menuIcon: ImageView
    private lateinit var btnCamera: ImageButton
    private lateinit var geoMap: WebView
    private lateinit var tvWelcomeName: TextView
    private lateinit var btnProfileHeader: CardView // Variable untuk tombol profil di header

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

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            Toast.makeText(this, "QR Code: ${result.contents}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        //disini
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")
                    if (role == "dokter") {
                        // Jika "nyasar" ke sini padahal dokter, paksa pindah ke page dokter
                        startActivity(Intent(this, DoctorActivity::class.java))
                        finish()
                    }
                }
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
        btnProfileHeader = findViewById(R.id.btnProfileHeader) // Bind tombol profil

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
        // 1. Sidebar Toggle
        menuIcon.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START)
            else
                drawerLayout.openDrawer(GravityCompat.START)
        }

        // 2. Klik Profil di Header
        btnProfileHeader.setOnClickListener {
            if (auth.currentUser != null) {
                // Ke Halaman Profil
                startActivity(Intent(this, UserActivity::class.java))
            } else {
                // Ke Halaman Login
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        // 3. Navigasi Sidebar
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

        // 4. Tombol Login Guest
        btnLoginGuest.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // 5. Custom Scanner
        btnCamera.setOnClickListener {
            val options = ScanOptions()
            options.setCaptureActivity(CustomScannerActivity::class.java)
            options.setOrientationLocked(true)
            options.setBeepEnabled(true)
            scanLauncher.launch(options)
        }

        // 6. Kategori
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

    // --- Header Sidebar Logic ---
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

    // --- Dashboard Logic ---
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
            .whereEqualTo("status", "Booked")
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
        val htmlContent = "<html><body style='margin:0;padding:0;'><iframe width='100%' height='100%' frameborder='0' src='https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3966.052!2d106.618!3d-6.256!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x0!2zNsKwMTUnMjEuNiJTIDEwNsKwMzcnMDQuOCJF!5e0!3m2!1sen!2sid!4v16345'></iframe></body></html>"
        geoMap.loadData(htmlContent, "text/html", "UTF-8")
    }
}