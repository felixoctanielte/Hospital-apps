package com.example.hospital_apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DataDokterFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ArrayAdapter<String>
    private val dataList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_data_dokter, container, false)
        val listView = view.findViewById<ListView>(R.id.list_dokter)

        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            dataList
        )
        listView.adapter = adapter

        db = FirebaseFirestore.getInstance()

        db.collection("dokter")
            .addSnapshotListener { snapshot, _ ->
                dataList.clear()
                snapshot?.forEach {
                    val nama = it.getString("nama") ?: ""
                    val spesialis = it.getString("spesialis") ?: ""
                    dataList.add("$nama - $spesialis")
                }
                adapter.notifyDataSetChanged()
            }

        return view
    }
}
