package com.example.hospital_apps

data class PredictionResponse(
    val status: String,
    val prediction: Int? = 0,      // Tambahkan ? agar aman jika null
    val result_text: String? = "", // Tambahkan ? agar aman jika null
    val probability: Double? = 0.0,
    val message: String? = null    // <--- TAMBAHAN PENTING
)