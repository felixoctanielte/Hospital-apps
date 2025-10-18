package com.example.hospital_apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DataDokterFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_data_dokter, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_dokter)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data = listOf(
            "dr. Brando, Sp.JP" to "Jantung",
            "dr. Maya, Sp.KK" to "Kulit",
            "dr. Rizky, Sp.PD" to "Penyakit Dalam"
        )

        recyclerView.adapter = SimpleAdapter(requireContext(), "Dokter", data)
        return view
    }
}
