package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        supportActionBar?.title = "Pilih Poli - Jadwal Dokter"

        val recyclerView: RecyclerView = findViewById(R.id.rv_poli)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Daftar Poli
        val poliList = listOf(
            Poli("p_psikologi", "Psikologi", R.drawable.hex_orange),
            Poli("p_jantung", "Jantung", R.drawable.hex_red),
            Poli("p_gigi", "Gigi", R.drawable.hex_green),
            Poli("p_kehamilan", "Kehamilan", R.drawable.hex_purple),
            Poli("p_paru", "Paru-Paru", R.drawable.hex_blue),
            Poli("p_kanker", "Kanker", R.drawable.hex_yellow),
            Poli("p_tumor", "Tumor", R.drawable.hex_cyan)
        )

        // Adapter untuk daftar Poli
        val adapter = PoliAdapter(poliList) { selectedPoli ->
            val intent = Intent(this, DoctorListActivity::class.java)
            intent.putExtra("poli", selectedPoli)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
}
