    package com.example.hospital_apps

    import android.content.Intent
    import android.os.Bundle
    import android.view.View
    import android.widget.*
    import androidx.appcompat.app.AppCompatActivity
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore
    import java.util.*

    class UserActivity : AppCompatActivity() {
        private lateinit var layoutPoliAktif: View


        // Tombol & Navigasi
        private lateinit var btnEditData: Button
        private lateinit var btnLogout: Button
        private lateinit var btnBack: ImageButton

        // Header Text (Nama & Role di area Biru)
        private lateinit var tvHeaderName: TextView
        private lateinit var tvHeaderRole: TextView

        // Layout untuk Include (Baris data list)
        private lateinit var layoutNama: View
        private lateinit var layoutTanggal: View
        private lateinit var layoutNIK: View
        private lateinit var layoutAlamat: View
        private lateinit var layoutBPJS: View

        // Firebase
        private lateinit var auth: FirebaseAuth
        private lateinit var db: FirebaseFirestore

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_user)

            auth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()

            try {
                initViews()
                setupClickListeners()


                // Set Label awal agar rapi sebelum data masuk
                setLabelOnly(layoutNama, "NAMA LENGKAP")
                setLabelOnly(layoutTanggal, "TANGGAL LAHIR")
                setLabelOnly(layoutNIK, "NOMOR NIK")
                setLabelOnly(layoutBPJS, "NO BPJS")
                setLabelOnly(layoutAlamat, "ALAMAT DOMISILI")

                loadUserData()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error inisialisasi UI: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onResume() {
            super.onResume()
            // Reload data saat kembali dari halaman Edit agar update otomatis
            loadUserData()
        }

        private fun initViews() {
            // 1. Tombol
            btnBack = findViewById(R.id.btnBack)
            btnEditData = findViewById(R.id.btnEditData)
            btnLogout = findViewById(R.id.btnLogout)

            // 2. Header (Nama & Role)
            tvHeaderName = findViewById(R.id.tvHeaderName)
            tvHeaderRole = findViewById(R.id.tvHeaderRole)

            // 3. Container List Data (Sesuai ID di activity_user.xml)
            layoutNama = findViewById(R.id.menuApps)
            layoutTanggal = findViewById(R.id.menuNotif)
            layoutNIK = findViewById(R.id.menuSetting)
            layoutBPJS = findViewById(R.id.menuHelp)
            layoutAlamat = findViewById(R.id.menuAccount)
        }

        private fun setupClickListeners() {
            btnBack.setOnClickListener { finish() }

            // Tombol Edit
            btnEditData.setOnClickListener { goToEditPage() }

            // Tombol Logout
            btnLogout.setOnClickListener {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            // Klik pada Baris Data juga bisa menuju Edit
            layoutNama.setOnClickListener { goToEditPage() }
            layoutTanggal.setOnClickListener { goToEditPage() }
            layoutNIK.setOnClickListener { goToEditPage() }
            layoutBPJS.setOnClickListener { goToEditPage() }
            layoutAlamat.setOnClickListener { goToEditPage() }
        }

        private fun loadUserData() {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            // Ambil Data (Handle null dengan safe call ?:)
                            val nama = document.getString("name") ?: document.getString("username") ?: "-"
                            val dob = document.getString("dob") ?: "-"
                            val nik = document.getString("nik") ?: "-"
                            val bpjs = document.getString("bpjs") ?: "-"
                            val alamat = document.getString("address") ?: "-"
                            val role = document.getString("role") ?: "Pasien"

                            // Update List Data (Kartu Putih)
                            updateMenuData(layoutNama, "NAMA LENGKAP", nama)
                            updateMenuData(layoutTanggal, "TANGGAL LAHIR", dob)
                            updateMenuData(layoutNIK, "NOMOR NIK", nik)
                            updateMenuData(layoutBPJS, "NO BPJS", bpjs)
                            updateMenuData(layoutAlamat, "ALAMAT DOMISILI", alamat)

                            // Update Header (Area Biru)
                            tvHeaderName.text = nama

                            // Kapitalisasi huruf pertama role (misal: pasien -> Pasien)
                            tvHeaderRole.text = role.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal memuat profil", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        private fun loadActivePoli(uid: String) {
            db.collection("appointments")
                .whereEqualTo("userId", uid)
                .whereIn("status", listOf("menunggu", "diproses"))
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val doc = documents.documents[0]
                        val poli = doc.getString("poli") ?: "-"
                        val status = doc.getString("status") ?: "-"

                        updateMenuData(
                            layoutPoliAktif,
                            "POLI AKTIF",
                            "$poli (${status.uppercase()})"
                        )
                    } else {
                        updateMenuData(
                            layoutPoliAktif,
                            "POLI AKTIF",
                            "Tidak ada"
                        )
                    }
                }
                .addOnFailureListener {
                    updateMenuData(layoutPoliAktif, "POLI AKTIF", "Tidak tersedia")
                }
        }

        // Fungsi update UI per baris
        private fun updateMenuData(container: View, label: String, value: String) {
            val tvLabel: TextView = container.findViewById(R.id.tvMenuTitle)
            val tvValue: TextView = container.findViewById(R.id.tvMenuValue)

            tvLabel.text = label
            tvValue.text = value
        }

        // Fungsi set label saja (initial state)
        private fun setLabelOnly(container: View, label: String) {
            val tvLabel: TextView = container.findViewById(R.id.tvMenuTitle)
            tvLabel.text = label
        }

        // Mengambil data bersih untuk dikirim ke EditUserActivity
        private fun getMenuValue(container: View): String {
            val tvValue: TextView = container.findViewById(R.id.tvMenuValue)
            val text = tvValue.text.toString()
            // Filter text placeholder agar form edit bersih
            return if (text == "Memuat..." || text == "-" || text == "Memuat Data...") "" else text
        }

        private fun goToEditPage() {
            val intent = Intent(this, EditUserActivity::class.java).apply {
                putExtra("nama", getMenuValue(layoutNama))
                putExtra("tanggal", getMenuValue(layoutTanggal))
                putExtra("nik", getMenuValue(layoutNIK))
                putExtra("alamat", getMenuValue(layoutAlamat))
                putExtra("bpjs", getMenuValue(layoutBPJS))
            }
            startActivity(intent)
        }
    }