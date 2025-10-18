package com.example.hospital_apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DataPoliFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_data_poli, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_poli)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data = listOf(
            "Poli Jantung" to "Lantai 2",
            "Poli Gigi" to "Lantai 1",
            "Poli Anak" to "Lantai 3"
        )

        recyclerView.adapter = SimpleAdapter(requireContext(), "Poli Klinik", data)
        return view
    }
}
