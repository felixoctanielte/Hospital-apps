package com.example.hospital_apps

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream

class AdminLaporanActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var tableLayout: TableLayout

    private lateinit var spPoli: Spinner
    private lateinit var spDokter: Spinner
    private lateinit var spStatus: Spinner
    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText

    // list data dari Firestore
    private val dataAppointments = mutableListOf<Map<String, Any>>()
    private var filteredData = mutableListOf<Map<String, Any>>()

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_laporan)

        db = FirebaseFirestore.getInstance()

        tableLayout = findViewById(R.id.table_laporan)
        val btnTampilkan = findViewById<Button>(R.id.btn_filter_tampilkan)
        val btnUnduh = findViewById<Button>(R.id.btn_unduh_pdf)
        btnBack = findViewById(R.id.btn_back)

        spPoli = findViewById(R.id.sp_poli)
        spDokter = findViewById(R.id.sp_dokter)
        spStatus = findViewById(R.id.sp_status)
        etStartDate = findViewById(R.id.et_start_date)
        etEndDate = findViewById(R.id.et_end_date)

        btnBack.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        }

        setupSpinners()
        loadAppointments() // ambil dari Firestore

        btnTampilkan.setOnClickListener { filterAndShowData() }
        btnUnduh.setOnClickListener { generatePDF(filteredData) }
    }

    private fun setupSpinners() {

        val poliList = listOf("Semua","Umum","Gigi","Anak","Mata")
        val statusList = listOf("Semua","menunggu","selesai")
        val dokterList = listOf("Semua","dr. Tirta","dr. Andi","drg. Rina") // opsional

        spPoli.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, poliList)
        spStatus.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusList)
        spDokter.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dokterList)
    }

    /**
     * ==========================
     * LOAD DATA FIRESTORE
     * ==========================
     */
    private fun loadAppointments() {
        db.collection("appointments")
            .get()
            .addOnSuccessListener { result ->

                dataAppointments.clear()

                for (doc in result) {
                    dataAppointments.add(doc.data)
                }

                Toast.makeText(this, "Loaded ${dataAppointments.size} data dari Firebase", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * ==========================
     * FILTER DATA
     * ==========================
     */
    private fun filterAndShowData() {

        val selectedPoli = spPoli.selectedItem.toString()
        val selectedDokter = spDokter.selectedItem.toString()
        val selectedStatus = spStatus.selectedItem.toString()
        val startDate = etStartDate.text.toString()
        val endDate = etEndDate.text.toString()

        filteredData = dataAppointments.filter { row ->

            val poliMatch = selectedPoli == "Semua" || row["poli"] == selectedPoli
            val dokterMatch = selectedDokter == "Semua" || row["doctorName"] == selectedDokter
            val statusMatch = selectedStatus == "Semua" || row["status"] == selectedStatus

            val tanggal = row["selectedDate"]?.toString() ?: ""
            val tanggalMatch = (startDate.isEmpty() || tanggal >= startDate) &&
                    (endDate.isEmpty() || tanggal <= endDate)

            poliMatch && dokterMatch && statusMatch && tanggalMatch

        }.toMutableList()

        tampilkanTabel(filteredData)
    }

    /**
     * ==========================
     * TAMPILKAN TABEL
     * ==========================
     */
    private fun tampilkanTabel(dataList: List<Map<String, Any>>) {

        tableLayout.removeViews(1, tableLayout.childCount - 1)

        var counter = 1

        for (row in dataList) {

            val tableRow = TableRow(this)

            val namaPasien = row["patientName"] ?: "-"
            val nik = row["nik"] ?: "-"
            val poli = row["poli"] ?: "-"
            val dokter = row["doctorName"] ?: "-"
            val tanggal = row["selectedDate"] ?: "-"
            val status = row["status"] ?: "-"

            val values = listOf(counter.toString(), namaPasien.toString(), nik.toString(), poli.toString(), dokter.toString(), tanggal.toString(), status.toString())

            for (value in values) {
                val tv = TextView(this)
                tv.text = value
                tv.setPadding(12, 8, 12, 8)
                tableRow.addView(tv)
            }

            counter++
            tableLayout.addView(tableRow)
        }

        filteredData = dataList.toMutableList()

        Toast.makeText(this, "Total ${dataList.size} data ditemukan", Toast.LENGTH_SHORT).show()
    }

    /**
     * ==========================
     * PDF GENERATOR
     * ==========================
     */
    private fun generatePDF(dataList: List<Map<String, Any>>) {

        val pdfDocument = android.graphics.pdf.PdfDocument()

        val paint = Paint()
        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        var yPos = 40f

        paint.textSize = 16f
        canvas.drawText("Laporan Pasien", 200f, yPos, paint)

        yPos += 40f
        paint.textSize = 12f

        for ((i,row) in dataList.withIndex()) {
            val line = "${i+1}. ${row["patientName"]} | ${row["poli"]} | ${row["doctorName"]} | ${row["status"]}"
            canvas.drawText(line, 30f, yPos, paint)
            yPos += 20f
        }

        pdfDocument.finishPage(page)

        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"laporan_pasien.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        val uri = FileProvider.getUriForFile(this,"$packageName.provider",file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri,"application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    }
}
