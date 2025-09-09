package com.example.monitorwidget.data.remote


import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
	private val moshi = Moshi.Builder()
		.add(KotlinJsonAdapterFactory())
		.build()
	
	val dollarApi: DollarApiService = Retrofit.Builder()
		.baseUrl("https://ve.dolarapi.com/")
		.addConverterFactory(MoshiConverterFactory.create(moshi))
		.build()
		.create(DollarApiService::class.java)
	
	val hexaApi: HexaRateApiService = Retrofit.Builder()
		.baseUrl("https://hexarate.paikama.co/api/rates/")
		.addConverterFactory(MoshiConverterFactory.create(moshi))
		.build()
		.create(HexaRateApiService::class.java)
}
