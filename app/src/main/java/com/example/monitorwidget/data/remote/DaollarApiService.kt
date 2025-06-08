package com.example.monitorwidget.data.remote


import com.example.monitorwidget.data.remote.local.datastore.DolarApiItem
import retrofit2.http.GET

interface DollarApiService {
    @GET("v1/dolares")
    suspend fun getDolarRates(): List<DolarApiItem>
}