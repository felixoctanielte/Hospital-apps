package com.example.hospital_apps

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.hospital_apps.api.HealthApiResponse
import com.example.hospital_apps.api.RetrofitInstance as RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiseaseSearchActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
    private lateinit var btnSearch: Button
    private lateinit var btnBack: Button  // Tambahkan ini
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val topics = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disease_search)

        // Inisialisasi view
        searchInput = findViewById(R.id.searchInput)
        btnSearch = findViewById(R.id.btnSearch)
        btnBack = findViewById(R.id.btnBack)  // Inisialisasi tombol back
        listView = findViewById(R.id.listView)

        // Setup adapter untuk ListView
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, topics)
        listView.adapter = adapter

        // Cek jika ada category yang dikirim via Intent
        val category = intent.getStringExtra("category") ?: ""
        if (category.isNotEmpty()) searchHealthTopics(category)

        // Tombol search
        btnSearch.setOnClickListener {
            val query = searchInput.text.toString().trim()
            if (query.isNotEmpty()) searchHealthTopics(query)
            else Toast.makeText(this, "Masukkan nama penyakit", Toast.LENGTH_SHORT).show()
        }

        // Tombol back
        btnBack.setOnClickListener {
            finish() // Kembali ke activity sebelumnya
        }
    }

    // Fungsi mencari topik penyakit via API
    private fun searchHealthTopics(query: String) {
        Toast.makeText(this, "Mencari: $query", Toast.LENGTH_SHORT).show()

        RetrofitClient.api.searchTopics(query).enqueue(object : Callback<HealthApiResponse> {
            override fun onResponse(call: Call<HealthApiResponse>, response: Response<HealthApiResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()?.Result?.Resources?.Resource ?: emptyList()
                    topics.clear()
                    for (item in result) {
                        topics.add(item.Title ?: "Tidak ada judul")
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        this@DiseaseSearchActivity,
                        "Gagal ambil data (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<HealthApiResponse>, t: Throwable) {
                Toast.makeText(this@DiseaseSearchActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
