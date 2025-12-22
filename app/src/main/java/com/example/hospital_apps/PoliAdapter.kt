package com.example.hospital_apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PoliAdapter(
    private var items: List<Poli>,
    private val activePoliName: String? = null, // ubah dari val ke var supaya bisa di-update
    private val onClick: (Poli) -> Unit
) : RecyclerView.Adapter<PoliAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val ivHex: ImageView = view.findViewById(R.id.iv_hexagon)
        val tvName: TextView = view.findViewById(R.id.tv_poli_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_poli, parent, false)
        return VH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.tvName.text = p.name
        holder.ivHex.setImageResource(p.hexBgRes)

        val isLocked = activePoliName != null && p.name != activePoliName

        if (isLocked) {
            // Poli lain dikunci
            holder.itemView.alpha = 0.4f
            holder.itemView.isEnabled = false
            holder.itemView.setOnClickListener(null)
        } else {
            // Poli aktif / belum ada antrian
            holder.itemView.alpha = 1f
            holder.itemView.isEnabled = true
            holder.itemView.setOnClickListener { onClick(p) }
        }
    }


    // 🔍 Tambahkan fungsi ini agar bisa update daftar poli saat pencarian
    fun updateData(newList: List<Poli>) {
        items = newList
        notifyDataSetChanged()
    }

}
