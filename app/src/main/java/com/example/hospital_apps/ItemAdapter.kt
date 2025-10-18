package com.example.hospital_apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class ItemData(val id: Int, val name: String, val specialty: String)

class ItemAdapter(private val items: MutableList<ItemData>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_nama)
        val tvSpecialty: TextView = view.findViewById(R.id.tv_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvSpecialty.text = item.specialty
    }

    // --- Tambahan untuk ShowActivity ---
    fun setData(newData: List<ItemData>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }

    fun addItem(item: ItemData) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun updateItem(id: Int, name: String, specialty: String) {
        val index = items.indexOfFirst { it.id == id }
        if (index != -1) {
            items[index] = items[index].copy(name = name, specialty = specialty)
            notifyItemChanged(index)
        }
    }

    fun deleteItem(id: Int) {
        val index = items.indexOfFirst { it.id == id }
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
