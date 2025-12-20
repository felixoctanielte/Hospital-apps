package com.example.hospital_apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DokterAdapter(
    private val list: List<Dokter>,
    private val onClick: (Dokter) -> Unit
) : RecyclerView.Adapter<DokterAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nama: TextView = view.findViewById(R.id.txtNama)
        val spesialis: TextView = view.findViewById(R.id.txtSpesialis)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dokter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dokter = list[position]
        holder.nama.text = dokter.nama
        holder.spesialis.text = dokter.spesialis

        holder.itemView.setOnClickListener {
            onClick(dokter)
        }
    }
}
