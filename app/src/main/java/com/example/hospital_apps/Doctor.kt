package com.example.hospital_apps

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Doctor(
    val id: String,
    val name: String,
    val isAvailable: Boolean,
    val schedule: List<String>,
    val photoRes: Int,
    val hexBgRes: Int
) : Parcelable
