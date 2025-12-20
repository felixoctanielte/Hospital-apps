package com.example.hospital_apps

data class Appointment(
    var id: String? = null, // Untuk menyimpan ID Dokumen

    // NAMA VARIABEL INI HARUS SAMA PERSIS DENGAN FIRESTORE:
    var doctorName: String? = null,
    var patientName: String? = null,
    var poli: String? = null,
    var selectedTime: String? = null,
    var date: String? = null,
    var status: String? = null,
    var userId: String? = null
) {
    // Constructor kosong wajib untuk Firestore
    constructor() : this(null, null, null, null, null, null, null)
}