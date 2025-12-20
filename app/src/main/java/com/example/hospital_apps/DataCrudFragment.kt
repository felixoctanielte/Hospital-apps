package com.example.hospital_apps

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class DataCrudFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DataAdapter
    private val dataList = mutableListOf<DataItem>()

    private lateinit var editName: EditText
    private lateinit var editSpecialty: EditText

    private var selectedItemId: String? = null
    private var currentCategory = "dokter" // default

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_data_crud, container, false)

        db = FirebaseFirestore.getInstance()

        // Init UI
        editName = view.findViewById(R.id.editName)
        editSpecialty = view.findViewById(R.id.editSpecialty)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DataAdapter(dataList) { item ->
            editName.setText(item.name)
            editSpecialty.setText(item.specialty)
            selectedItemId = item.id
        }
        recyclerView.adapter = adapter

        // Hanya pakai view pertama
        val btnDokter = view.findViewById<Button>(R.id.btnDokter)
        val btnPoli = view.findViewById<Button>(R.id.btnPoli)
        val btnPasien = view.findViewById<Button>(R.id.btnPasien)

        val btnCreate = view.findViewById<Button>(R.id.btnCreate)
        val btnUpdate = view.findViewById<Button>(R.id.btnUpdate)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnBack = view.findViewById<Button>(R.id.btnBack)


        btnDokter.setOnClickListener { switchCategory("dokter") }
        btnPoli.setOnClickListener { switchCategory("poli") }
        btnPasien.setOnClickListener { switchCategory("pasien") }

        btnCreate.setOnClickListener { createData() }
        btnUpdate.setOnClickListener { updateData() }
        btnDelete.setOnClickListener { deleteData() }
        btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        loadData() // load default category

        return view
    }


    private fun switchCategory(category: String) {
        currentCategory = category
        selectedItemId = null
        editName.text.clear()
        editSpecialty.text.clear()
        loadData()
    }

    private fun loadData() {
        db.collection(currentCategory)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                dataList.clear()
                snapshot?.forEach { doc ->
                    val name = doc.getString("name") ?: ""
                    val specialty = doc.getString("specialty") ?: ""
                    dataList.add(DataItem(doc.id, name, specialty))
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun createData() {
        val name = editName.text.toString().trim()
        val specialty = editSpecialty.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Nama wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }
        val item = hashMapOf("name" to name, "specialty" to specialty)
        db.collection(currentCategory).add(item)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Data berhasil dibuat", Toast.LENGTH_SHORT).show()
                editName.text.clear()
                editSpecialty.text.clear()
            }
    }

    private fun updateData() {
        val id = selectedItemId
        if (id == null) {
            Toast.makeText(requireContext(), "Pilih data terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        val name = editName.text.toString().trim()
        val specialty = editSpecialty.text.toString().trim()
        db.collection(currentCategory).document(id)
            .update("name", name, "specialty", specialty)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                editName.text.clear()
                editSpecialty.text.clear()
                selectedItemId = null
            }
    }

    private fun deleteData() {
        val id = selectedItemId
        if (id == null) {
            Toast.makeText(requireContext(), "Pilih data terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        db.collection(currentCategory).document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                editName.text.clear()
                editSpecialty.text.clear()
                selectedItemId = null
            }
    }

    // Data class
    data class DataItem(val id: String, val name: String, val specialty: String)

    // Adapter RecyclerView
    class DataAdapter(
        private val items: List<DataItem>,
        private val clickListener: (DataItem) -> Unit
    ) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val txtName: TextView = view.findViewById(android.R.id.text1)
            init {
                view.setOnClickListener {
                    clickListener(items[adapterPosition])
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.txtName.text = "${items[position].name} - ${items[position].specialty}"
        }

        override fun getItemCount(): Int = items.size
    }
}
