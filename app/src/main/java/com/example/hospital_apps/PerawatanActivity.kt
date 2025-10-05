package com.example.hospital_apps

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PerawatanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_perawatan)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val hiddenSection = findViewById<LinearLayout>(R.id.hiddenSection)
        val btnSeeMore = findViewById<TextView>(R.id.btnSeeMore)

        btnBack.setOnClickListener { finish() }

        // Toggle See More
        btnSeeMore.setOnClickListener {
            if (hiddenSection.visibility == View.GONE) {
                hiddenSection.visibility = View.VISIBLE
                btnSeeMore.text = "See Less"
            } else {
                hiddenSection.visibility = View.GONE
                btnSeeMore.text = "See More"
            }
        }

        // Tampilkan detail saat section diklik
        for (i in 1..8) {
            val sectionId = resources.getIdentifier("sectionOkt$i", "id", packageName)
            val sectionView = findViewById<View>(sectionId)
            val cardDetail = sectionView?.findViewById<LinearLayout>(R.id.cardDetail)

            sectionView?.setOnClickListener {
                if (cardDetail?.visibility == View.GONE) {
                    cardDetail.visibility = View.VISIBLE
                } else {
                    cardDetail?.visibility = View.GONE
                }
            }

            // Ganti label tanggal (biar gak Okt X semua)
            val tvTanggal = sectionView?.findViewById<TextView>(R.id.tvTanggal)
            tvTanggal?.text = "Okt $i"
        }
    }
}
