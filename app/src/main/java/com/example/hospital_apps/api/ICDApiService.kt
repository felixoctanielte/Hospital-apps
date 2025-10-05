package com.example.hospital_apps.api

import com.example.hospital_apps.api.HealthApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ICDApiService {

    // 🔹 Versi pakai categoryId (misalnya kategori penyakit umum)
    @GET("topicsearch.json")
    suspend fun getByCategory(
        @Query("categoryId") categoryId: Int = 21
    ): Response<HealthApiResponse>

    // 🔹 Versi pakai pencarian teks (misal: keyword = "flu", "diabetes")
    @GET("topicsearch.json")
    suspend fun searchDisease(
        @Query("keyword") keyword: String
    ): Response<HealthApiResponse>
}
