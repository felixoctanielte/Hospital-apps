package com.example.hospital_apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class DataPasienFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ArrayAdapter<String>
    private val dataList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_data_pasien, container, false)

        // Inisialisasi komponen UI
        val listView = view.findViewById<ListView>(R.id.list_pasien)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_kembali_admin)
        val btnSimpan = view.findViewById<Button>(R.id.btn_simpan)
        val editNama = view.findViewById<EditText>(R.id.edit_nama)
        val editPoli = view.findViewById<EditText>(R.id.edit_poli)

        // Tombol kembali
        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Setup ListView
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataList)
        listView.adapter = adapter

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance()

        // Listener perubahan data pasien
        db.collection("pasien")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                dataList.clear()
                snapshot?.forEach { document ->
                    val nama = document.getString("nama") ?: "Nama kosong"
                    val poli = document.getString("poli") ?: "Poli kosong"
                    dataList.add("$nama - Poli $poli")
                }
                adapter.notifyDataSetChanged()
            }

        // Tombol simpan pasien baru
        btnSimpan.setOnClickListener {
            val nama = editNama.text.toString().trim()
            val poli = editPoli.text.toString().trim()

            if (nama.isEmpty() || poli.isEmpty()) {
                Toast.makeText(requireContext(), "Isi semua data!", Toast.LENGTH_SHORT).show()
            } else {
                val pasien = hashMapOf(
                    "nama" to nama,
                    "poli" to poli
                )

                db.collection("pasien")
                    .add(pasien)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Data pasien tersimpan", Toast.LENGTH_SHORT).show()
                        editNama.text.clear()
                        editPoli.text.clear()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        return view
    }
}
