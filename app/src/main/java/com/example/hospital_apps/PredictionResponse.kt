package com.example.hospital_apps

data class PredictionResponse(
    val status: String,
    val prediction: Int,
    val result_text: String,
    val probability: Double
)