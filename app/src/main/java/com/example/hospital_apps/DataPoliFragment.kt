package com.example.hospital_apps

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.ListView
import android.widget.ArrayAdapter

class DataPoliFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ArrayAdapter<String>
    private val dataList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_data_poli, container, false)
        val listView = view.findViewById<ListView>(R.id.list_poli)

        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            dataList
        )
        listView.adapter = adapter

        db = FirebaseFirestore.getInstance()

        db.collection("poli")
            .addSnapshotListener { snapshot, _ ->
                dataList.clear()
                snapshot?.forEach {
                    val nama = it.getString("nama") ?: ""
                    val lokasi = it.getString("lokasi") ?: ""
                    dataList.add("$nama - $lokasi")
                }
                adapter.notifyDataSetChanged()
            }

        return view
    }
}
