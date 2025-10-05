package com.example.hospital_apps

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class UserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val btnEditProfile: Button = findViewById(R.id.btnEditProfile)
        val btnEditData: Button = findViewById(R.id.btnEditData)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnEditProfile.setOnClickListener {
            Toast.makeText(this, "Edit Profil diklik", Toast.LENGTH_SHORT).show()
        }

        btnEditData.setOnClickListener {
            Toast.makeText(this, "Edit Data Diri diklik", Toast.LENGTH_SHORT).show()
        }

        // Set dummy data untuk menu
        setMenuData(R.id.menuApps, "Nama Lengkap", "John Doe")
        setMenuData(R.id.menuNotif, "Tanggal Lahir", "01 Januari 1990")
        setMenuData(R.id.menuSetting, "Nomor NIK", "1234567890123456")
        setMenuData(R.id.menuHelp, "No BPJS", "9876543210")
        setMenuData(R.id.menuAccount, "Alamat", "Jl. Contoh Alamat 123")
        setMenuData(R.id.menuLogout, "Alamat", "Kota Contoh, 12345")
    }

    private fun setMenuData(includeId: Int, title: String, value: String) {
        val layout: LinearLayout = findViewById(includeId)
        val tvTitle: TextView = layout.findViewById(R.id.tvMenuTitle)
        val tvValue: TextView = layout.findViewById(R.id.tvMenuValue)
        tvTitle.text = title
        tvValue.text = value
    }
}
