package com.example.hospital_apps

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

data class Pasien(val nama: String, val poli: String)

class PasienAdapter(
    private val context: Context,
    private val data: List<Pasien>
) : RecyclerView.Adapter<PasienAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tv_nama_pasien)
        val tvPoli: TextView = view.findViewById(R.id.tv_poli_pasien)
        val btnVerifikasi: Button = view.findViewById(R.id.btn_verifikasi)
        val btnTolak: Button = view.findViewById(R.id.btn_tolak)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pasien, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pasien = data[position]
        holder.tvNama.text = pasien.nama
        holder.tvPoli.text = "Poli : ${pasien.poli}"

        holder.btnVerifikasi.setOnClickListener {
            Toast.makeText(context, "${pasien.nama} telah diverifikasi ✅", Toast.LENGTH_SHORT).show()
        }

        holder.btnTolak.setOnClickListener {
            Toast.makeText(context, "${pasien.nama} ditolak ❌", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = data.size
}
