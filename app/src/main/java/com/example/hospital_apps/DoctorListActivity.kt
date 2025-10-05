package com.example.hospital_apps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DoctorListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_list)

        val poli = intent.getParcelableExtra<Poli>("poli")

        supportActionBar?.title = "Spesialis ${poli?.name ?: "Dokter"}"

        val recyclerView: RecyclerView = findViewById(R.id.rv_doctors)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val doctors = sampleDoctorsFor(poli ?: Poli("unknown", "Umum", R.drawable.hex_blue))

        val adapter = DoctorAdapter(doctors) { doctor, selectedTime ->
            val intent = Intent(this, ConfirmationActivity::class.java).apply {
                putExtra("doctor", doctor)
                putExtra("poli", poli)
                putExtra("time", selectedTime)
            }
            startActivity(intent)
        }

        recyclerView.adapter = adapter
    }

    private fun sampleDoctorsFor(poli: Poli): List<Doctor> {
        return listOf(
            Doctor("d1", "dr. A", true, listOf("08:00", "13:00", "15:00"), R.drawable.ic_person, poli.hexBgRes),
            Doctor("d2", "dr. B", true, listOf("09:00", "14:00", "16:00"), R.drawable.ic_person, poli.hexBgRes),
            Doctor("d3", "dr. D", false, emptyList(), R.drawable.ic_person, R.drawable.hex_cyan)
        )
    }
}
