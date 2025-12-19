package com.example.hospital_apps

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EditUserActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etTanggal: EditText
    private lateinit var etNIK: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etBPJS: EditText
    private lateinit var cbConfirm: CheckBox
    private lateinit var btnSimpan: Button
    private lateinit var btnBack: ImageButton

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        // 🔗 Bind View
        etNama = findViewById(R.id.etNama)
        etTanggal = findViewById(R.id.etTanggalLahir)
        etNIK = findViewById(R.id.etNIK)
        etAlamat = findViewById(R.id.etAlamat)
        etBPJS = findViewById(R.id.etBPJS)
        cbConfirm = findViewById(R.id.cbConfirm)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnBack = findViewById(R.id.btnBack)

        // 🔥 Load data dari Firestore
        loadUserData()

        // 🔒 Nama dari login (tidak boleh diedit)
        etNama.isEnabled = false
        etNama.isFocusable = false

        btnBack.setOnClickListener { finish() }
        etTanggal.setOnClickListener { showDatePicker() }
        btnSimpan.setOnClickListener { updateUserData() }
    }

    // =========================
    // 🔥 AMBIL DATA USER
    // =========================
    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    etNama.setText(doc.getString("name") ?: "")
                    etTanggal.setText(doc.getString("tanggal_lahir") ?: "")
                    etNIK.setText(doc.getString("nik") ?: "")
                    etAlamat.setText(doc.getString("alamat") ?: "")
                    etBPJS.setText(doc.getString("bpjs") ?: "")
                }
            }
            .addOnFailureListener {
                showToast("Gagal memuat data user")
            }
    }

    // =========================
    // 📅 DATE PICKER
    // =========================
    private fun showDatePicker() {
        val c = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                etTanggal.setText(
                    String.format("%02d-%02d-%04d", day, month + 1, year)
                )
            },
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // =========================
    // 💾 UPDATE DATA USER
    // =========================
    private fun updateUserData() {
        val tanggal = etTanggal.text.toString().trim()
        val nik = etNIK.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val bpjs = etBPJS.text.toString().trim()

        when {
            tanggal.isEmpty() ->
                showToast("Tanggal lahir belum diisi")

            nik.length != 16 || !nik.all { it.isDigit() } ->
                showToast("NIK harus 16 angka")

            alamat.isEmpty() ->
                showToast("Alamat tidak boleh kosong")

            alamat.length > 100 ->
                showToast("Alamat maksimal 100 karakter")

            bpjs.isEmpty() || !bpjs.all { it.isDigit() } ->
                showToast("BPJS harus berupa angka")

            !cbConfirm.isChecked ->
                showToast("Centang konfirmasi terlebih dahulu")

            else -> {
                val uid = auth.currentUser?.uid ?: return

                val updateData = hashMapOf<String, Any>(
                    "tanggal_lahir" to tanggal,
                    "nik" to nik,
                    "alamat" to alamat,
                    "bpjs" to bpjs
                )

                db.collection("users").document(uid)
                    .update(updateData)
                    .addOnSuccessListener {
                        showToast("Data berhasil diperbarui")
                        finish()
                    }
                    .addOnFailureListener {
                        showToast("Gagal memperbarui data")
                    }
            }
        }
    }

    // =========================
    // 🔔 TOAST
    // =========================
    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
