package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PoliActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poli)

        supportActionBar?.title = "Pilih Poli"

        val rv: RecyclerView = findViewById(R.id.rv_polis)
        rv.layoutManager = LinearLayoutManager(this)

        // LIST POLI
        // Disini kita hanya menampilkan Poli yang sudah ada datanya di Firebase
        val polis = listOf(
            // 1. Poli UMUM (Sesuai data JSON: specialist="Umum")
            Poli("p1", "Umum", R.drawable.hex_blue),

            // 2. Poli KANDUNGAN (Sesuai data JSON: specialist="Kandungan")
            Poli("p2", "Kandungan", R.drawable.hex_purple),

            // 3. Poli ANAK (Sesuai data JSON: specialist="Anak")
            Poli("p3", "Anak", R.drawable.hex_green)
        )

        val adapter = PoliAdapter(polis) { poli ->
            // Intent diarahkan ke DoctorListActivity untuk mengambil data dari Firebase
            val i = Intent(this, DoctorListActivity::class.java)
            i.putExtra("poli", poli)
            startActivity(i)
        }
        rv.adapter = adapter
    }
}