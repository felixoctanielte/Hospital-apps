package com.example.hospital_apps

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Poli(
    val id: String,
    val name: String,
    val hexBgRes: Int
) : Parcelable
