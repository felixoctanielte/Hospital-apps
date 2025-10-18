package com.example.hospital_apps

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SimpleAdapter(private val context: Context, private val type: String,
                    private val data: List<Pair<String, String>>) :
    RecyclerView.Adapter<SimpleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNo: TextView = view.findViewById(R.id.tv_no)
        val tvNama: TextView = view.findViewById(R.id.tv_nama)
        val tvInfo: TextView = view.findViewById(R.id.tv_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (nama, info) = data[position]
        holder.tvNo.text = (position + 1).toString()
        holder.tvNama.text = nama
        holder.tvInfo.text = info

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ShowActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("name", nama)
            intent.putExtra("info", info)
            holder.itemView.context.startActivity(intent)
        }

    }
}
