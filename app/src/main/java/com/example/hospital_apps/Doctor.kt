package com.example.hospital_apps

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Doctor(
    val id: String? = null,
    val name: String? = null,
    val specialist: String? = null,

    @get:PropertyName("isAvailable")
    val isAvailable: Boolean = false,

    val schedule: List<String> = emptyList(),

    // UBAH NAMA INI agar tidak error di DoctorListActivity
    val imageResId: Int = 0,

    val hexBgRes: Int = 0
) : Parcelable {
    // Tambahkan constructor kosong agar Firebase tidak error saat parsing data
    constructor() : this(null, null, null, false, emptyList(), 0, 0)
}