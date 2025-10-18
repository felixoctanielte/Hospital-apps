package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View

class ShowActivity : AppCompatActivity() {
    private lateinit var btnBack: Button

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private val itemList = mutableListOf<ItemData>()

    private lateinit var editName: EditText
    private lateinit var editSpecialty: EditText
    private lateinit var btnCreate: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    private lateinit var btnDokter: Button
    private lateinit var btnPoli: Button
    private lateinit var btnPasien: Button

    private var currentCategory = "dokter"
    private var selectedItemId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)

        // Tombol kembali ke AdminActivity
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView)
        editName = findViewById(R.id.editName)
        editSpecialty = findViewById(R.id.editSpecialty)
        btnCreate = findViewById(R.id.btnCreate)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
        btnDokter = findViewById(R.id.btnDokter)
        btnPoli = findViewById(R.id.btnPoli)
        btnPasien = findViewById(R.id.btnPasien)

        adapter = ItemAdapter(itemList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Load kategori default
        loadCategory("dokter")

        btnDokter.setOnClickListener { loadCategory("dokter") }
        btnPoli.setOnClickListener { loadCategory("poli") }
        btnPasien.setOnClickListener { loadCategory("pasien") }

        btnCreate.setOnClickListener { createItem() }
        btnUpdate.setOnClickListener { updateItem() }
        btnDelete.setOnClickListener { deleteItem() }

        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(this, recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val item = itemList[position]
                        editName.setText(item.name)
                        editSpecialty.setText(item.specialty)
                        selectedItemId = item.id
                    }

                    override fun onLongItemClick(view: View, position: Int) {}
                })
        )
    }

    private fun loadCategory(category: String) {
        currentCategory = category
        val data = when (category) {
            "dokter" -> listOf(
                ItemData(1, "dr. John Doe", "Sp. Jantung"),
                ItemData(2, "dr. Jane Smith", "Sp. Gigi")
            )
            "poli" -> listOf(
                ItemData(1, "Poli Gigi", "Gigi & Mulut"),
                ItemData(2, "Poli Umum", "Penyakit Dalam")
            )
            "pasien" -> listOf(
                ItemData(1, "Mamat Reza", "Umur 25"),
                ItemData(2, "Dewi Lestari", "Umur 30")
            )
            else -> emptyList()
        }
        adapter.setData(data)
        selectedItemId = null
        editName.text.clear()
        editSpecialty.text.clear()
    }

    private fun createItem() {
        val name = editName.text.toString()
        val specialty = editSpecialty.text.toString()
        if (name.isEmpty() || specialty.isEmpty()) {
            Toast.makeText(this, "Isi semua kolom dulu", Toast.LENGTH_SHORT).show()
            return
        }
        val newId = (itemList.maxOfOrNull { it.id } ?: 0) + 1
        adapter.addItem(ItemData(newId, name, specialty))
        editName.text.clear()
        editSpecialty.text.clear()
    }

    private fun updateItem() {
        val id = selectedItemId
        if (id == null) {
            Toast.makeText(this, "Pilih item dulu", Toast.LENGTH_SHORT).show()
            return
        }
        val name = editName.text.toString()
        val specialty = editSpecialty.text.toString()
        adapter.updateItem(id, name, specialty)
        editName.text.clear()
        editSpecialty.text.clear()
        selectedItemId = null
    }

    private fun deleteItem() {
        val id = selectedItemId
        if (id == null) {
            Toast.makeText(this, "Pilih item dulu", Toast.LENGTH_SHORT).show()
            return
        }
        adapter.deleteItem(id)
        editName.text.clear()
        editSpecialty.text.clear()
        selectedItemId = null
    }
}
