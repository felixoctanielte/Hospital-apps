package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import androidx.core.widget.addTextChangedListener

class ScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // RecyclerView setup
        val recyclerView: RecyclerView = findViewById(R.id.rv_poli)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // --- BAGIAN INI YANG KITA PERBAIKI ---
        // Sesuaikan nama poli dengan field "specialist" di Firebase agar datanya nyambung
        val poliList = listOf(
            // 1. Poli UMUM (dr. Tirta)
            Poli("p1", "Umum", R.drawable.hex_blue),

            // 2. Poli KANDUNGAN (dr. Boyke) -> Perhatikan namanya "Kandungan", bukan "Kehamilan"
            Poli("p2", "Kandungan", R.drawable.hex_purple),

            // 3. Poli ANAK (dr. Seto)
            Poli("p3", "Anak", R.drawable.hex_green)

            // Poli lain dihapus dulu supaya tidak membuka halaman kosong
        )

        // Adapter
        val adapter = PoliAdapter(poliList) { selectedPoli ->
            val intent = Intent(this, DoctorListActivity::class.java)
            // UBAH INI: Kirim String nama poli saja, jangan object utuh
            intent.putExtra("poliName", selectedPoli.name)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // 🔍 Search poli
        val searchView = findViewById<TextInputEditText>(R.id.searchPoli)
        searchView.addTextChangedListener { text ->
            val query = text.toString()
            val filteredList = if (query.isEmpty()) {
                poliList
            } else {
                poliList.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            }
            // Pastikan di PoliAdapter.kt kamu ada fungsi updateData()
            // Jika error merah di baris bawah ini, hapus saja bagian search-nya dulu sementara
            adapter.updateData(filteredList)
        }
    }
}