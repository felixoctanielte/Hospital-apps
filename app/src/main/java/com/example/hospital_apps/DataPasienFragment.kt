package com.example.hospital_apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DataPasienFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_data_pasien, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_pasien)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data = listOf(
            "Mamat Reza" to "Jantung",
            "Faiz Lembayung" to "Gigi",
            "Siti Aminah" to "Anak"
        )

        recyclerView.adapter = SimpleAdapter(requireContext(), "Pasien", data)
        return view
    }
}
