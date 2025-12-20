package com.example.hospital_apps

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class CustomScannerActivity : AppCompatActivity() {

    // 1. Deklarasikan variabel captureManager di sini
    private lateinit var captureManager: CaptureManager
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_scanner)

        // 2. Inisialisasi View
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner)
        btnBack = findViewById(R.id.btnBackScanner)

        // 3. Setup CaptureManager (Ini inti agar scanner jalan)
        captureManager = CaptureManager(this, barcodeScannerView)
        captureManager.initializeFromIntent(intent, savedInstanceState)
        captureManager.decode()

        // 4. Logika Tombol Back
        btnBack.setOnClickListener {
            finish() // Menutup activity ini dan kembali ke dashboard
        }
    }

    // --- WAJIB: Override Lifecycle agar kamera tidak error/blank ---

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        captureManager.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        captureManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}