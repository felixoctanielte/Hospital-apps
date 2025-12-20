package com.example.hospital_apps

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class AdminLaporanActivity : AppCompatActivity() {
    private lateinit var btnBack: Button
    private lateinit var tableLayout: TableLayout
    private lateinit var dummyData: List<Array<String>>

    private lateinit var spPoli: Spinner
    private lateinit var spDokter: Spinner
    private lateinit var spStatus: Spinner
    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_laporan)

        tableLayout = findViewById(R.id.table_laporan)
        val btnTampilkan = findViewById<Button>(R.id.btn_filter_tampilkan)
        val btnUnduh = findViewById<Button>(R.id.btn_unduh_pdf)
        btnBack = findViewById(R.id.btn_back)
        spPoli = findViewById(R.id.sp_poli)
        spDokter = findViewById(R.id.sp_dokter)
        spStatus = findViewById(R.id.sp_status)
        etStartDate = findViewById(R.id.et_start_date)
        etEndDate = findViewById(R.id.et_end_date)

        // 🔹 Tombol kembali ke AdminActivity
        btnBack.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        //  Data dummy
        dummyData = listOf(
            arrayOf("1", "Budi Santoso", "3201011234560001", "Poli Umum", "dr. Andi", "10 Okt 2025", "Terverifikasi"),
            arrayOf("2", "Siti Aminah", "3201021234560002", "Poli Gigi", "drg. Rina", "10 Okt 2025", "Terverifikasi"),
            arrayOf("3", "Ahmad Fauzi", "3201031234560003", "Poli Anak", "dr. Dwi", "11 Okt 2025", "Belum Verifikasi"),
            arrayOf("4", "Rina Kurnia", "3201041234560004", "Poli Umum", "dr. Andi", "11 Okt 2025", "Terverifikasi"),
            arrayOf("5", "Heri Gunawan", "3201051234560005", "Poli Mata", "dr. Sari", "12 Okt 2025", "Terverifikasi"),
            arrayOf("6", "Lina Marlina", "3201061234560006", "Poli Gigi", "drg. Rina", "12 Okt 2025", "Belum Verifikasi"),
            arrayOf("7", "Bagus Pratama", "3201071234560007", "Poli Umum", "dr. Andi", "13 Okt 2025", "Terverifikasi"),
            arrayOf("8", "Sari Lestari", "3201081234560008", "Poli Anak", "dr. Dwi", "13 Okt 2025", "Terverifikasi"),
            arrayOf("9", "Nina Agustin", "3201151234560015", "Poli Gigi", "drg. Rina", "17 Okt 2025", "Terverifikasi"),
            arrayOf("10", "Doni Saputra", "3201161234560016", "Poli Umum", "dr. Andi", "17 Okt 2025", "Belum Verifikasi"),
            arrayOf("11", "Wulan Sari", "3201171234560017", "Poli Anak", "dr. Dwi", "18 Okt 2025", "Terverifikasi"),
            arrayOf("12", "Rangga Putra", "3201181234560018", "Poli Mata", "dr. Sari", "18 Okt 2025", "Terverifikasi"),
            arrayOf("13", "Maya Dewi", "3201191234560019", "Poli Umum", "dr. Andi", "19 Okt 2025", "Belum Verifikasi"),
            arrayOf("14", "Teguh Santosa", "3201201234560020", "Poli Gigi", "drg. Rina", "19 Okt 2025", "Terverifikasi"),
            arrayOf("15", "Dewi Kartika", "3201211234560021", "Poli Anak", "dr. Dwi", "20 Okt 2025", "Terverifikasi"),
            arrayOf("16", "Bambang Irawan", "3201221234560022", "Poli Mata", "dr. Sari", "20 Okt 2025", "Belum Verifikasi"),
            arrayOf("17", "Sinta Ayu", "3201231234560023", "Poli Umum", "dr. Andi", "21 Okt 2025", "Terverifikasi"),
            arrayOf("18", "Andika Prasetyo", "3201241234560024", "Poli Gigi", "drg. Rina", "21 Okt 2025", "Belum Verifikasi"),
            arrayOf("19", "Citra Puspita", "3201251234560025", "Poli Anak", "dr. Dwi", "22 Okt 2025", "Terverifikasi"),
            arrayOf("20", "Adi Wijaya", "3201261234560026", "Poli Mata", "dr. Sari", "22 Okt 2025", "Terverifikasi")
        )

        setupSpinners()

        //  Aksi tombol
        btnTampilkan.setOnClickListener { filterAndShowData() }
        btnUnduh.setOnClickListener { generatePDF() }
    }

    //  Isi pilihan Spinner
    private fun setupSpinners() {
        val poliList = listOf("Semua", "Poli Umum", "Poli Gigi", "Poli Anak", "Poli Mata")
        val dokterList = listOf("Semua", "dr. Andi", "drg. Rina", "dr. Dwi", "dr. Sari")
        val statusList = listOf("Semua", "Terverifikasi", "Belum Verifikasi")

        spPoli.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, poliList)
        spDokter.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dokterList)
        spStatus.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusList)
    }

    //  Filter + tampilkan tabel
    private fun filterAndShowData() {
        val selectedPoli = spPoli.selectedItem.toString()
        val selectedDokter = spDokter.selectedItem.toString()
        val selectedStatus = spStatus.selectedItem.toString()
        val startDate = etStartDate.text.toString()
        val endDate = etEndDate.text.toString()

        // Filter data
        val filteredData = dummyData.filter { row ->
            val poliMatch = selectedPoli == "Semua" || row[3] == selectedPoli
            val dokterMatch = selectedDokter == "Semua" || row[4] == selectedDokter
            val statusMatch = selectedStatus == "Semua" || row[6] == selectedStatus
            val tanggalMatch = (startDate.isEmpty() || row[5] >= startDate) &&
                    (endDate.isEmpty() || row[5] <= endDate)
            poliMatch && dokterMatch && statusMatch && tanggalMatch
        }

        tampilkanTabel(filteredData)
    }

    //  Tampilkan data di tabel
    private fun tampilkanTabel(data: List<Array<String>>) {
        tableLayout.removeAllViews()

        // Header
        val headerRow = TableRow(this)
        val headers = listOf("No", "Nama", "Poli", "Dokter", "Tanggal", "Status")
        for (title in headers) {
            val tvHeader = TextView(this)
            tvHeader.text = title
            tvHeader.setPadding(12, 8, 12, 8)
            tvHeader.textSize = 15f
            tvHeader.setTypeface(null, Typeface.BOLD)
            tvHeader.setBackgroundColor(Color.LTGRAY)
            headerRow.addView(tvHeader)
        }
        tableLayout.addView(headerRow)

        // Isi data
        for (rowData in data) {
            val row = TableRow(this)
            val values = listOf(rowData[0], rowData[1], rowData[3], rowData[4], rowData[5], rowData[6])
            for (text in values) {
                val tv = TextView(this)
                tv.text = text
                tv.setPadding(12, 8, 12, 8)
                tv.textSize = 14f
                row.addView(tv)
            }
            tableLayout.addView(row)
        }

        Toast.makeText(this, "Ditemukan ${data.size} data", Toast.LENGTH_SHORT).show()
    }

    //  Fungsi generate PDF (sama seperti sebelumnya)
    private fun generatePDF() {
        val pdfDocument = android.graphics.pdf.PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()
        val linePaint = Paint()

        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        titlePaint.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        titlePaint.textSize = 18f
        canvas.drawText("Laporan Kunjungan Pasien", 170f, 50f, titlePaint)

        val startX = 40f
        var startY = 100f
        val rowHeight = 25f
        val colWidths = listOf(30f, 120f, 80f, 90f, 90f, 100f)
        val headers = listOf("No", "Nama", "Poli", "Dokter", "Tanggal", "Status")

        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        linePaint.strokeWidth = 1.5f

        var x = startX
        for (i in headers.indices) {
            canvas.drawText(headers[i], x + 5, startY, paint)
            x += colWidths[i]
        }
        canvas.drawLine(startX, startY + 5, 560f, startY + 5, linePaint)

        var y = startY + rowHeight
        for (row in dummyData) {
            x = startX
            val values = listOf(row[0], row[1], row[3], row[4], row[5], row[6])
            for (i in values.indices) {
                canvas.drawText(values[i], x + 5, y, paint)
                x += colWidths[i]
            }
            canvas.drawLine(startX, y + 5, 560f, y + 5, linePaint)
            y += rowHeight
            if (y > 780) break
        }

        y += 40f
        paint.textSize = 11f
        canvas.drawText("Dicetak pada: 18 Oktober 2025", 40f, y, paint)
        canvas.drawText("Ttd, Admin Rumah Sakit", 400f, y, paint)

        pdfDocument.finishPage(page)

        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "laporan_pasien.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, "PDF tersimpan di: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal menyimpan PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }

        try {
            val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivity(Intent.createChooser(intent, "Buka PDF dengan..."))
        } catch (e: Exception) {
            Toast.makeText(this, "Tidak ada aplikasi PDF viewer", Toast.LENGTH_SHORT).show()
        }
    }
}
