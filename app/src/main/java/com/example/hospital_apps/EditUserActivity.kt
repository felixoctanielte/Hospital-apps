package com.example.hospital_apps

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class EditUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        val etNama = findViewById<EditText>(R.id.etNama)
        val etTanggal = findViewById<EditText>(R.id.etTanggalLahir)
        val etNIK = findViewById<EditText>(R.id.etNIK)
        val etAlamat = findViewById<EditText>(R.id.etAlamat)
        val etBPJS = findViewById<EditText>(R.id.etBPJS)
        val cbConfirm = findViewById<CheckBox>(R.id.cbConfirm)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // Ambil data lama dari intent (kalau ada)
        etNama.setText(intent.getStringExtra("nama"))
        etTanggal.setText(intent.getStringExtra("tanggal"))
        etNIK.setText(intent.getStringExtra("nik"))
        etAlamat.setText(intent.getStringExtra("alamat"))
        etBPJS.setText(intent.getStringExtra("bpjs"))

        // Tombol Back
        btnBack.setOnClickListener {
            finish() // Kembali ke halaman profil tanpa menyimpan
        }

        // DatePicker untuk tanggal lahir
        etTanggal.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                etTanggal.setText(String.format("%02d-%02d-%04d", d, m + 1, y))
            }, year, month, day)

            datePicker.show()
        }

        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val tanggal = etTanggal.text.toString().trim()
            val nik = etNIK.text.toString().trim()
            val alamat = etAlamat.text.toString().trim()
            val bpjs = etBPJS.text.toString().trim()

            // Validasi data
            when {
                nama.isEmpty() -> showToast("Nama tidak boleh kosong")
                tanggal.isEmpty() -> showToast("Tanggal lahir belum diisi")
                nik.length != 16 || !nik.all { it.isDigit() } -> showToast("NIK harus 16 angka")
                alamat.isEmpty() -> showToast("Alamat tidak boleh kosong")
                alamat.length > 100 -> showToast("Alamat maksimal 100 karakter")
                bpjs.isEmpty() -> showToast("Nomor BPJS tidak boleh kosong")
                !bpjs.all { it.isDigit() } -> showToast("Nomor BPJS hanya boleh angka")
                !cbConfirm.isChecked -> showToast("Centang konfirmasi terlebih dahulu")
                else -> {
                    val resultIntent = Intent().apply {
                        putExtra("nama", nama)
                        putExtra("tanggal", tanggal)
                        putExtra("nik", nik)
                        putExtra("alamat", alamat)
                        putExtra("bpjs", bpjs)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
