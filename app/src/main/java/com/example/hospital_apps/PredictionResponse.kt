package com.example.hospital_apps

data class PredictionResponse(
    val status: String,
    val prediction: Int? = 0,
    val result_text: String? = "",
    val probability: Double? = 0.0,
    val message: String? = null
)