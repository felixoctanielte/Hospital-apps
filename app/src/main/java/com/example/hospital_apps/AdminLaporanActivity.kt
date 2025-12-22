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

    private lateinit var btnBack: ImageButton

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
        val btnBack = findViewById<ImageButton>(R.id.btn_kembali_admin)

        // spinner




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

        if(dataList.isEmpty()){
            Toast.makeText(this,"Tidak ada data untuk dicetak",Toast.LENGTH_SHORT).show()
            return
        }

        val pdf = android.graphics.pdf.PdfDocument()

        val pageWidth = 595
        val pageHeight = 842

        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(
            pageWidth, pageHeight, 1).create()

        val page = pdf.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply { textSize = 10f }
        val titlePaint = Paint().apply {
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        }

        // tanggal realtime
        val currentDate = java.text.SimpleDateFormat("dd-MM-yyyy",
            java.util.Locale.getDefault()).format(java.util.Date())

        canvas.drawText("Laporan Pasien - $currentDate", 200f, 30f, titlePaint)

        var startY = 60f
        val startX = 20f

        // header tabel
        val columnWidths = listOf(30,110,90,70,90,90,70)
        val headers = listOf("No","Nama","NIK","Poli","Dokter","Tanggal","Status")

        var x = startX
        for(i in headers.indices){
            canvas.drawText(headers[i], x, startY, titlePaint)
            x += columnWidths[i]
        }

        startY += 15f
        canvas.drawLine(startX, startY, pageWidth-20f, startY, paint)
        startY += 10f

        for((index,row) in dataList.withIndex()){

            x = startX

            val rowValues = listOf(
                (index+1).toString(),
                row["patientName"].toString(),
                row["nik"].toString(),
                row["poli"].toString(),
                row["doctorName"].toString(),
                row["selectedDate"].toString(),
                row["status"].toString()
            )

            for(i in rowValues.indices){
                canvas.drawText(rowValues[i], x, startY, paint)
                x += columnWidths[i]
            }

            startY += 18f

            // buat new page jika penuh biar tidak error
            if(startY > pageHeight - 60){
                pdf.finishPage(page)
                val newPage = pdf.startPage(pageInfo)
                startY = 40f
            }
        }

        pdf.finishPage(page)

        // simpan file pdf
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"laporan_pasien.pdf")

        pdf.writeTo(FileOutputStream(file))
        pdf.close()

        // buka pdf
        val uri = FileProvider.getUriForFile(this,"$packageName.provider",file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri,"application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    }

}
