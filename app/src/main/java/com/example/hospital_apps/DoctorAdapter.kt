package com.example.hospital_apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class DoctorAdapter(
    private val items: List<Doctor>,
    private val onRegister: (Doctor, String) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.VH>() {

    private val selectedTimes = mutableMapOf<String, String?>()

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_doctor_name)
        val tvStatus: TextView = view.findViewById(R.id.tv_status)
        val chipGroup: ChipGroup = view.findViewById(R.id.chipgroup_times)
        val btnRegister: Button = view.findViewById(R.id.btn_register)
        val ivBg: ImageView = view.findViewById(R.id.iv_hexagon_bg)
        val ivAvatar: ImageView = view.findViewById(R.id.iv_avatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor, parent, false)
        return VH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val doctor = items[position]
        holder.tvName.text = doctor.name
        holder.tvStatus.text = if (doctor.isAvailable) "Tersedia" else "Tidak Tersedia"

        Glide.with(holder.itemView.context).load(doctor.hexBgRes).into(holder.ivBg)
        Glide.with(holder.itemView.context).load(doctor.photoRes).into(holder.ivAvatar)

        holder.chipGroup.removeAllViews()
        holder.chipGroup.isSingleSelection = true

        val alreadySelected = selectedTimes[doctor.id]

        for (time in doctor.schedule) {
            val chip = Chip(holder.itemView.context)
            chip.text = time
            chip.isCheckable = true
            chip.isChecked = (time == alreadySelected)
            chip.isEnabled = doctor.isAvailable

            chip.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                if (isChecked) selectedTimes[doctor.id] = time
                else if (selectedTimes[doctor.id] == time) selectedTimes[doctor.id] = null

                holder.btnRegister.isEnabled =
                    selectedTimes[doctor.id] != null && doctor.isAvailable
            }
            holder.chipGroup.addView(chip)
        }

        holder.btnRegister.isEnabled =
            (selectedTimes[doctor.id] != null) && doctor.isAvailable

        holder.btnRegister.setOnClickListener {
            val sel = selectedTimes[doctor.id]
            if (sel != null) onRegister(doctor, sel)
            else android.widget.Toast.makeText(
                holder.itemView.context,
                "Pilih jam terlebih dahulu",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }

        if (!doctor.isAvailable) {
            holder.btnRegister.isEnabled = false
        }
    }
}
