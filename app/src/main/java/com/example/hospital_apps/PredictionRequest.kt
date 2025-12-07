package com.example.hospital_apps

data class PredictionRequest(
    val age: Int,
    val hour1: Int,
    val weekday1: Int,
    val triagehtn1: Int,
    val abnvs1: Int,
    val moa1: Int,
    val mm1: Int,
    val crowd1: Int
)