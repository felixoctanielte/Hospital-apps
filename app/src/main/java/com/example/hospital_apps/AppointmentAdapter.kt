package com.example.hospital_apps

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentAdapter(
    private val appointmentList: List<Appointment>,
    private val onItemClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_ticket_name)
        val tvTime: TextView = view.findViewById(R.id.tv_ticket_time)
        // TAMBAHAN: TextView untuk Tanggal
        val tvDate: TextView = view.findViewById(R.id.tv_ticket_date)

        val tvDoctor: TextView = view.findViewById(R.id.tv_ticket_doctor)
        val tvStatus: TextView = view.findViewById(R.id.chip_status)
        val statusStrip: View = view.findViewById(R.id.view_status_color)
        val tvQueueInfo: TextView = view.findViewById(R.id.tv_queue_position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_antrian_ticket, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = appointmentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = appointmentList[position]

        holder.tvName.text = appointment.patientName ?: "Tanpa Nama"
        holder.tvTime.text = appointment.selectedTime ?: "-"
        holder.tvDoctor.text = "${appointment.poli} - ${appointment.doctorName}"

        // --- UPDATE: SET TANGGAL ---
        // Jika data 'date' kosong di database, tampilkan "-"
        holder.tvDate.text = appointment.date ?: "-"

        val statusText = appointment.status?.uppercase() ?: "MENUNGGU"

        // --- UPDATE LOGIKA WARNA ---
        if (statusText == "DIPROSES" || statusText == "SEDANG DIPERIKSA") {
            val green = Color.parseColor("#4CAF50")
            holder.statusStrip.setBackgroundColor(green)
            holder.tvStatus.setBackgroundColor(green)
            holder.tvStatus.text = "GILIRAN ANDA"
            holder.tvQueueInfo.visibility = View.GONE
        }
        else if (statusText == "SELESAI") {
            val blue = Color.parseColor("#2196F3")
            holder.statusStrip.setBackgroundColor(blue)
            holder.tvStatus.setBackgroundColor(blue)
            holder.tvStatus.text = "SELESAI"
            holder.tvQueueInfo.visibility = View.GONE
        }
        else {
            val amber = Color.parseColor("#FFC107")
            holder.statusStrip.setBackgroundColor(amber)
            holder.tvStatus.setBackgroundColor(amber)
            holder.tvStatus.text = "MENUNGGU"
            holder.tvQueueInfo.visibility = View.VISIBLE
            countPeopleAhead(holder.tvQueueInfo, appointment)
        }

        holder.itemView.setOnClickListener { onItemClick(appointment) }
    }

    private fun countPeopleAhead(textView: TextView, myAppt: Appointment) {
        val doctorName = myAppt.doctorName
        val myTime = myAppt.selectedTime

        if (doctorName == null || myTime == null) return

        db.collection("appointments")
            .whereEqualTo("doctorName", doctorName)
            .whereEqualTo("status", "menunggu")
            .whereLessThan("selectedTime", myTime)
            .get()
            .addOnSuccessListener { snapshots ->
                val count = snapshots.size()
                if (count == 0) {
                    textView.text = "Anda antrian selanjutnya!"
                    textView.setTextColor(Color.parseColor("#2E7D32"))
                } else {
                    textView.text = "Ada $count orang di depan Anda"
                    textView.setTextColor(Color.parseColor("#E65100"))
                }
            }
            .addOnFailureListener {
                textView.text = "Info antrian tidak tersedia"
            }
    }
}