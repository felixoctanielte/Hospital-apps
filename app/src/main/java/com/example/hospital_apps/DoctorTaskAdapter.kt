package com.example.hospital_apps

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// KITA BERI NAMA BARU: DoctorTaskAdapter
class DoctorTaskAdapter(
    private val appointmentList: List<Appointment>,
    private val onItemClick: (Appointment) -> Unit
) : RecyclerView.Adapter<DoctorTaskAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Menggunakan ID dari layout item_doctor_task.xml
        val tvTime: TextView = view.findViewById(R.id.tv_task_time)
        val tvPatient: TextView = view.findViewById(R.id.tv_task_patient)
        val tvPoli: TextView = view.findViewById(R.id.tv_task_poli)
        val tvStatus: TextView = view.findViewById(R.id.chip_task_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Menggunakan layout khusus dokter yang tadi dibuat
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor_task, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = appointmentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointmentList[position]

        // 1. Set Data
        holder.tvTime.text = appointment.selectedTime ?: "-"
        holder.tvPatient.text = appointment.patientName ?: "Tanpa Nama"
        holder.tvPoli.text = appointment.poli ?: "Umum"

        val statusText = appointment.status?.uppercase() ?: "MENUNGGU"
        holder.tvStatus.text = statusText

        // 2. Warna Status
        when (statusText) {
            "DIPROSES", "SEDANG DIPERIKSA" -> {
                holder.tvStatus.setBackgroundColor(Color.parseColor("#4CAF50")) // Hijau
            }
            "SELESAI" -> {
                holder.tvStatus.setBackgroundColor(Color.parseColor("#2196F3")) // Biru
            }
            else -> {
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FFC107")) // Kuning
            }
        }

        // 3. Klik Item
        holder.itemView.setOnClickListener {
            onItemClick(appointment)
        }
    }
}