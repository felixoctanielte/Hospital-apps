package com.example.hospital_apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.CompoundButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class DoctorAdapter(
    private val items: List<Doctor>,
    private val onRegister: (Doctor, String) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.VH>() {

    // Map untuk menyimpan jam yang dipilih per dokter (Key: Nama Dokter, Value: Jam)
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

        // 1. SAFETY CHECK: Ambil pengidentifikasi unik (Nama atau ID)
        val docName = doctor.name ?: "Unknown"

        // 2. Set Text
        holder.tvName.text = doctor.name ?: "Dokter"
        holder.tvStatus.text = if (doctor.isAvailable) "Tersedia" else "Tidak Tersedia"

        // 3. Load Gambar (PERBAIKAN: Menggunakan imageResId sesuai Model baru)
        // Load Background Hexagon (jika ada)
        if (doctor.hexBgRes != 0) {
            Glide.with(holder.itemView.context).load(doctor.hexBgRes).into(holder.ivBg)
        }

        // Load Foto Profil (PERBAIKAN DISINI)
        if (doctor.imageResId != 0) {
            Glide.with(holder.itemView.context)
                .load(doctor.imageResId)
                .into(holder.ivAvatar)
        } else {
            // Gambar placeholder jika tidak ada foto
            holder.ivAvatar.setImageResource(R.drawable.ic_dokter_placeholder)
        }

        // 4. Logic Chip (Jam Praktek)
        holder.chipGroup.removeAllViews()
        holder.chipGroup.isSingleSelection = true

        val alreadySelected = selectedTimes[docName]

        // Jika data schedule dari Firestore kosong, buat dummy jam agar user bisa pilih
        val workingHours = if (doctor.schedule.isEmpty()) {
            listOf("08:00", "10:00", "13:00", "15:00")
        } else {
            doctor.schedule
        }

        for (time in workingHours) {
            val chip = Chip(holder.itemView.context)
            chip.text = time
            chip.isCheckable = true
            chip.isChecked = (time == alreadySelected)
            chip.isEnabled = doctor.isAvailable

            chip.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                if (isChecked) {
                    selectedTimes[docName] = time
                } else if (selectedTimes[docName] == time) {
                    selectedTimes[docName] = null
                }
                // Update tombol Daftar secara real-time
                holder.btnRegister.isEnabled = selectedTimes[docName] != null && doctor.isAvailable
            }
            holder.chipGroup.addView(chip)
        }

        // 5. Logic Tombol Daftar
        holder.btnRegister.isEnabled = (selectedTimes[docName] != null) && doctor.isAvailable

        holder.btnRegister.setOnClickListener {
            val sel = selectedTimes[docName]
            if (sel != null) {
                onRegister(doctor, sel)
            } else {
                Toast.makeText(holder.itemView.context, "Pilih jam terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }
}