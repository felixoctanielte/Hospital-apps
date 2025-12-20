package com.example.hospital_apps

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EditUserActivity : AppCompatActivity() {

    private lateinit var etNama: TextInputEditText
    private lateinit var etTanggal: TextInputEditText
    private lateinit var etNIK: TextInputEditText
    private lateinit var etAlamat: TextInputEditText
    private lateinit var etBPJS: TextInputEditText
    private lateinit var cbConfirm: CheckBox
    private lateinit var btnSimpan: Button
    private lateinit var btnBack: ImageButton
    private lateinit var progressBar: ProgressBar // Loading indicator

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initViews()

        // Isi form dengan data dari Intent (data yang dikirim dari UserActivity)
        etNama.setText(intent.getStringExtra("nama"))
        etTanggal.setText(intent.getStringExtra("tanggal"))
        etNIK.setText(intent.getStringExtra("nik"))
        etAlamat.setText(intent.getStringExtra("alamat"))
        etBPJS.setText(intent.getStringExtra("bpjs"))

        btnBack.setOnClickListener { finish() }

        // DatePicker Logic
        etTanggal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                // Format: 01 Januari 1990 (atau 01-01-1990 sesuai selera)
                val months = arrayOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
                val selectedDate = "$d ${months[m]} $y"
                etTanggal.setText(selectedDate)
            }, year, month, day)
            datePicker.show()
        }

        btnSimpan.setOnClickListener {
            handleSave()
        }
    }

    private fun initViews() {
        etNama = findViewById(R.id.etNama)
        etTanggal = findViewById(R.id.etTanggalLahir)
        etNIK = findViewById(R.id.etNIK)
        etAlamat = findViewById(R.id.etAlamat)
        etBPJS = findViewById(R.id.etBPJS)
        cbConfirm = findViewById(R.id.cbConfirm)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar) // Pastikan ada di XML
    }

    private fun handleSave() {
        val nama = etNama.text.toString().trim()
        val tanggal = etTanggal.text.toString().trim()
        val nik = etNIK.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val bpjs = etBPJS.text.toString().trim()

        // Validasi Input
        when {
            nama.isEmpty() -> showToast("Nama tidak boleh kosong")
            tanggal.isEmpty() -> showToast("Tanggal lahir belum diisi")
            nik.length != 16 || !nik.all { it.isDigit() } -> showToast("NIK harus 16 digit angka")
            alamat.isEmpty() -> showToast("Alamat tidak boleh kosong")
            bpjs.isEmpty() -> showToast("Nomor BPJS tidak boleh kosong")
            !cbConfirm.isChecked -> showToast("Mohon centang konfirmasi persetujuan")
            else -> {
                // Jika valid, simpan ke Firebase
                saveToFirestore(nama, tanggal, nik, alamat, bpjs)
            }
        }
    }

    private fun saveToFirestore(nama: String, dob: String, nik: String, alamat: String, bpjs: String) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            showToast("Sesi habis, silakan login ulang")
            return
        }

        setLoading(true)

        // Map data untuk update
        val updates = hashMapOf<String, Any>(
            "name" to nama,
            "dob" to dob,
            "nik" to nik,
            "address" to alamat,
            "bpjs" to bpjs
        )

        db.collection("users").document(uid)
            .update(updates)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, "Data berhasil diperbarui!", Toast.LENGTH_LONG).show()
                finish() // Kembali ke Profil dan otomatis reload
            }
            .addOnFailureListener { e ->
                setLoading(false)
                showToast("Gagal menyimpan: ${e.message}")
            }
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            btnSimpan.isEnabled = false
            btnSimpan.text = "Menyimpan..."
        } else {
            progressBar.visibility = View.GONE
            btnSimpan.isEnabled = true
            btnSimpan.text = "Simpan Perubahan"
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}