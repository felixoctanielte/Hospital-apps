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

        supportActionBar?.title = "Pilih Poli - Jadwal Dokter"

        val rv: RecyclerView = findViewById(R.id.rv_polis)
        rv.layoutManager = LinearLayoutManager(this)

        // Replace drawable names with whatever you have in res/drawable
        val polis = listOf(
            Poli("p1", "Psikologi", R.drawable.hex_orange),
            Poli("p2", "Jantung", R.drawable.hex_red),
            Poli("p3", "Gigi", R.drawable.hex_green),
            Poli("p4", "Kehamilan", R.drawable.hex_purple),
            Poli("p5", "Paru-Paru", R.drawable.hex_blue),
            Poli("p6", "Kanker", R.drawable.hex_yellow),
            Poli("p7", "Tumor", R.drawable.hex_cyan)
        )

        val adapter = PoliAdapter(polis) { poli ->
            val i = Intent(this, ScheduleActivity::class.java)
            i.putExtra("poli", poli)
            startActivity(i)
        }
        rv.adapter = adapter
    }
}
