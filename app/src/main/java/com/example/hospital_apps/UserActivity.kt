package com.example.hospital_apps

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class UserActivity : AppCompatActivity() {

    private lateinit var btnEditData: Button
    private lateinit var btnLogout: Button

    private lateinit var layoutNama: LinearLayout
    private lateinit var layoutTanggal: LinearLayout
    private lateinit var layoutNIK: LinearLayout
    private lateinit var layoutAlamat: LinearLayout
    private lateinit var layoutBPJS: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnEditData = findViewById(R.id.btnEditData)
        btnLogout = findViewById(R.id.btnLogout)

        layoutNama = findViewById(R.id.menuApps)
        layoutTanggal = findViewById(R.id.menuNotif)
        layoutNIK = findViewById(R.id.menuSetting)
        layoutBPJS = findViewById(R.id.menuHelp)
        layoutAlamat = findViewById(R.id.menuAccount)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Set data awal
        setMenuData(layoutNama, "Nama Lengkap", "John Doe")
        setMenuData(layoutTanggal, "Tanggal Lahir", "01 Januari 1990")
        setMenuData(layoutNIK, "Nomor NIK", "1234567890123456")
        setMenuData(layoutBPJS, "No BPJS", "9876543210")
        setMenuData(layoutAlamat, "Alamat", "Jl. Contoh Alamat 123")

        // Tombol edit buka halaman edit user
        btnEditData.setOnClickListener {
            val intent = Intent(this, EditUserActivity::class.java).apply {
                putExtra("nama", getMenuValue(layoutNama))
                putExtra("tanggal", getMenuValue(layoutTanggal))
                putExtra("nik", getMenuValue(layoutNIK))
                putExtra("alamat", getMenuValue(layoutAlamat))
                putExtra("bpjs", getMenuValue(layoutBPJS))
            }
            startActivityForResult(intent, 100)
        }

        // ✅ Tombol logout dipindahkan ke sini
        btnLogout.setOnClickListener {
            Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setMenuData(layout: LinearLayout, title: String, value: String) {
        val tvTitle: TextView = layout.findViewById(R.id.tvMenuTitle)
        val tvValue: TextView = layout.findViewById(R.id.tvMenuValue)
        tvTitle.text = title
        tvValue.text = value
    }

    private fun getMenuValue(layout: LinearLayout): String {
        val tvValue: TextView = layout.findViewById(R.id.tvMenuValue)
        return tvValue.text.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            setMenuData(layoutNama, "Nama Lengkap", data.getStringExtra("nama") ?: "")
            setMenuData(layoutTanggal, "Tanggal Lahir", data.getStringExtra("tanggal") ?: "")
            setMenuData(layoutNIK, "Nomor NIK", data.getStringExtra("nik") ?: "")
            setMenuData(layoutAlamat, "Alamat", data.getStringExtra("alamat") ?: "")
            setMenuData(layoutBPJS, "No BPJS", data.getStringExtra("bpjs") ?: "")
        }
    }
}
