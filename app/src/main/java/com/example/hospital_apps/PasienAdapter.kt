package com.example.hospital_apps

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

// Model dengan status
data class Pasien(
    val nama: String,
    val poli: String,
    var status: String = "Belum Terverifikasi"
)

class PasienAdapter(
    private val context: Context,
    private val data: MutableList<Pasien> // mutable supaya status bisa berubah
) : RecyclerView.Adapter<PasienAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tv_nama_pasien)
        val tvPoli: TextView = view.findViewById(R.id.tv_poli_pasien)
        val tvStatus: TextView = view.findViewById(R.id.tv_status_pasien)
        val btnVerifikasi: Button = view.findViewById(R.id.btn_verifikasi)
        val btnTolak: Button = view.findViewById(R.id.btn_tolak)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pasien, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pasien = data[position]

        holder.tvNama.text = pasien.nama
        holder.tvPoli.text = "Poli : ${pasien.poli}"
        holder.tvStatus.text = "Status: ${pasien.status}"

        // ubah warna status sesuai nilainya
        when (pasien.status) {
            "Belum Terverifikasi" -> holder.tvStatus.setTextColor(Color.parseColor("#FFA000")) // oranye
            "Terverifikasi" -> holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")) // hijau
            "Ditolak" -> holder.tvStatus.setTextColor(Color.parseColor("#F44336")) // merah
        }

        // tombol verifikasi
        holder.btnVerifikasi.setOnClickListener {
            pasien.status = "Terverifikasi"
            notifyItemChanged(position) // update tampilan
            Toast.makeText(context, "${pasien.nama} telah diverifikasi", Toast.LENGTH_SHORT).show()
        }

        // tombol tolak
        holder.btnTolak.setOnClickListener {
            pasien.status = "Ditolak"
            notifyItemChanged(position)
            Toast.makeText(context, "${pasien.nama} ditolak", Toast.LENGTH_SHORT).show()
        }
    }
}
