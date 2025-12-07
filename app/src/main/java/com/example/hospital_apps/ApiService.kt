package com.example.hospital_apps

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("predict")
    fun predictWaitTime(@Body request: PredictionRequest): Call<PredictionResponse>
}