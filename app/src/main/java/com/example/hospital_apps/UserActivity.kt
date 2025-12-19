package com.example.hospital_apps

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

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

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnEditData = findViewById(R.id.btnEditData)
        btnLogout = findViewById(R.id.btnLogout)

        layoutNama = findViewById(R.id.menuApps)
        layoutTanggal = findViewById(R.id.menuNotif)
        layoutNIK = findViewById(R.id.menuSetting)
        layoutAlamat = findViewById(R.id.menuAccount)
        layoutBPJS = findViewById(R.id.menuHelp)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 🔥 Ambil data user dari Firestore
        loadUserData()

        // ✏️ Edit Data
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

        // 🚪 Logout
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    // =========================
    // 🔥 LOAD DATA DARI FIRESTORE
    // =========================
    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .addSnapshotListener { doc, error ->
                if (error != null || doc == null || !doc.exists()) return@addSnapshotListener

                setMenuData(layoutNama, "Nama Lengkap", doc.getString("name") ?: "-")
                setMenuData(layoutTanggal, "Tanggal Lahir", doc.getString("tanggal_lahir") ?: "-")
                setMenuData(layoutNIK, "Nomor NIK", doc.getString("nik") ?: "-")
                setMenuData(layoutAlamat, "Alamat", doc.getString("alamat") ?: "-")
                setMenuData(layoutBPJS, "No BPJS", doc.getString("bpjs") ?: "-")
            }
    }


    // 🔧 HELPER

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

    // =========================
    // ⬅️ HASIL EDIT USER
    // =========================
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {

            val uid = auth.currentUser?.uid ?: return

            val updateData = hashMapOf(
                "nama" to (data.getStringExtra("nama") ?: ""),
                "tanggal_lahir" to (data.getStringExtra("tanggal") ?: ""),
                "nik" to (data.getStringExtra("nik") ?: ""),
                "alamat" to (data.getStringExtra("alamat") ?: ""),
                "bpjs" to (data.getStringExtra("bpjs") ?: "")
            )

            // 🔄 Update Firestore
            db.collection("users").document(uid)
                .update(updateData as Map<String, Any>)
                .addOnSuccessListener {
                    loadUserData()
                    Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal update data", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
