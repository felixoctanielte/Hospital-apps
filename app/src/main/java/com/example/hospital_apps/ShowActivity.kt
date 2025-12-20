package com.example.hospital_apps

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import android.widget.ImageButton

class ShowActivity : AppCompatActivity() {

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
    private var selectedItemId: String? = null // docId asli dari Firestore

    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)
        val btnKembali: ImageButton = findViewById(R.id.btn_kembali_admin)
        btnKembali.setOnClickListener {
            finish() // menutup ShowActivity dan kembali ke activity sebelumnya
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

        // Tombol kategori
        btnDokter.setOnClickListener { loadCategory("dokter") }
        btnPoli.setOnClickListener { loadCategory("poli") }
        btnPasien.setOnClickListener { loadCategory("pasien") }

        // Tombol CRUD
        btnCreate.setOnClickListener { createItem() }
        btnUpdate.setOnClickListener { updateItem() }
        btnDelete.setOnClickListener { deleteItem() }

        // Item click listener untuk memilih item
        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(this, recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: android.view.View, position: Int) {
                        val item = itemList[position]
                        editName.setText(item.name)
                        editSpecialty.setText(item.specialty)
                        selectedItemId = item.docId // pakai docId asli
                    }

                    override fun onLongItemClick(view: android.view.View, position: Int) {}
                })
        )

        // Load kategori default
        loadCategory("dokter")
    }

    private fun loadCategory(category: String) {
        currentCategory = category
        listenerRegistration?.remove()
        itemList.clear()
        adapter.notifyDataSetChanged()

        listenerRegistration = db.collection(category)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                itemList.clear()
                snapshot?.documents?.forEach { doc ->
                    val name = doc.getString("name") ?: ""
                    val specialty = doc.getString("specialty") ?: ""
                    val docId = doc.id // simpan docId asli
                    itemList.add(ItemData(docId, name, specialty))
                }
                adapter.notifyDataSetChanged()
                editName.text.clear()
                editSpecialty.text.clear()
                selectedItemId = null
            }
    }

    private fun createItem() {
        val name = editName.text.toString()
        val specialty = editSpecialty.text.toString()
        if (name.isEmpty() || specialty.isEmpty()) {
            Toast.makeText(this, "Isi semua kolom dulu", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf(
            "name" to name,
            "specialty" to specialty
        )
        db.collection(currentCategory)
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Item ditambahkan", Toast.LENGTH_SHORT).show()
                editName.text.clear()
                editSpecialty.text.clear()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateItem() {
        if (selectedItemId == null) {
            Toast.makeText(this, "Pilih item dulu", Toast.LENGTH_SHORT).show()
            return
        }
        val name = editName.text.toString()
        val specialty = editSpecialty.text.toString()
        if (name.isEmpty() || specialty.isEmpty()) {
            Toast.makeText(this, "Isi semua kolom dulu", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection(currentCategory)
            .document(selectedItemId!!) // pakai docId asli
            .update("name", name, "specialty", specialty)
            .addOnSuccessListener {
                Toast.makeText(this, "Item diperbarui", Toast.LENGTH_SHORT).show()
                editName.text.clear()
                editSpecialty.text.clear()
                selectedItemId = null
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteItem() {
        if (selectedItemId == null) {
            Toast.makeText(this, "Pilih item dulu", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection(currentCategory)
            .document(selectedItemId!!) // pakai docId asli
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Item dihapus", Toast.LENGTH_SHORT).show()
                editName.text.clear()
                editSpecialty.text.clear()
                selectedItemId = null
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroy() {
        listenerRegistration?.remove()
        super.onDestroy()
    }
}
