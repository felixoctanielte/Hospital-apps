package com.example.hospital_apps.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.hospital_apps.api.HealthApiService

interface HealthApiService {
    // Ini yang dipanggil dari DiseaseSearchActivity
    @GET("topicsearch.json")
    fun searchTopics(
        @Query("keyword") keyword: String
    ): Call<HealthApiResponse>
}
