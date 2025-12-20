package com.example.hospital_apps



import android.content.Intent

import android.os.Bundle

import android.view.View

import android.widget.Button

import android.widget.ImageButton

import android.widget.TextView

import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore



class DoctorProfileActivity : AppCompatActivity() {



    private lateinit var auth: FirebaseAuth

    private lateinit var db: FirebaseFirestore



    private lateinit var tvHeaderName: TextView

    private lateinit var tvHeaderSpesialis: TextView

    private lateinit var btnBack: ImageButton

    private lateinit var btnLogout: Button



// Containers

    private lateinit var layoutName: View

    private lateinit var layoutSIP: View

    private lateinit var layoutSTR: View

    private lateinit var layoutEmail: View

    private lateinit var layoutHospital: View



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_doctor_profile)



        auth = FirebaseAuth.getInstance()

        db = FirebaseFirestore.getInstance()



        initViews()

        setupListeners()

        loadDoctorData()

    }



    private fun initViews() {

        tvHeaderName = findViewById(R.id.tvHeaderName)

        tvHeaderSpesialis = findViewById(R.id.tvHeaderSpesialis)

        btnBack = findViewById(R.id.btnBack)

        btnLogout = findViewById(R.id.btnLogout)



// Init Include Layouts

        layoutName = findViewById(R.id.menuName)

        layoutSIP = findViewById(R.id.menuSIP)

        layoutSTR = findViewById(R.id.menuSTR)

        layoutEmail = findViewById(R.id.menuEmail)

        layoutHospital = findViewById(R.id.menuHospital)



// Set Label Awal (Agar tidak kosong saat loading)

        setMenuLabel(layoutName, "NAMA LENGKAP & GELAR", "-")

        setMenuLabel(layoutSIP, "NOMOR SIP", "-")

        setMenuLabel(layoutSTR, "NOMOR STR", "-")

        setMenuLabel(layoutEmail, "EMAIL TERDAFTAR", "-")

        setMenuLabel(layoutHospital, "LOKASI PRAKTIK", "RS Land Of Dawn")

    }



    private fun setupListeners() {

        btnBack.setOnClickListener { finish() }



        btnLogout.setOnClickListener {

            auth.signOut()

            val intent = Intent(this, LoginActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()

        }

    }



    private fun loadDoctorData() {

        val uid = auth.currentUser?.uid ?: return



        db.collection("users").document(uid).get()

            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val name = document.getString("name") ?: document.getString("username") ?: "Dokter"

                    val sip = document.getString("sip") ?: "-"

                    val str = document.getString("str") ?: "-"

                    val email = document.getString("email") ?: "-"

                    val specialist = document.getString("spesialis") ?: "Umum"

                    val hospital = document.getString("hospital") ?: "RS Land Of Dawn"



// Update UI

                    tvHeaderName.text = name

                    tvHeaderSpesialis.text = "Spesialis $specialist"



                    setMenuLabel(layoutName, "NAMA LENGKAP", name)

                    setMenuLabel(layoutSIP, "NOMOR SIP", sip)

                    setMenuLabel(layoutSTR, "NOMOR STR", str)

                    setMenuLabel(layoutEmail, "EMAIL", email)

                    setMenuLabel(layoutHospital, "LOKASI PRAKTIK", hospital)

                }

            }

            .addOnFailureListener {

                Toast.makeText(this, "Gagal memuat profil", Toast.LENGTH_SHORT).show()

            }

    }



    private fun setMenuLabel(view: View, label: String, value: String) {

        val tvLabel = view.findViewById<TextView>(R.id.tvMenuTitle)

        val tvValue = view.findViewById<TextView>(R.id.tvMenuValue)

        tvLabel.text = label

        tvValue.text = value

    }

}