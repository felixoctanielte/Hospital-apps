package com.example.hospital_apps.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://health.gov/myhealthfinder/api/v3/") // ganti sesuai endpoint-mu
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: HealthApiService by lazy {
        retrofit.create(HealthApiService::class.java)
    }
}
