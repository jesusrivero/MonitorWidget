package com.example.monitorwidget.infraestructure.di

import android.content.Context
import com.example.monitorwidget.data.preferences.ThemeDataStore
import com.example.monitorwidget.data.remote.DollarApiService
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.domain.repository.DollarRepository
import com.example.monitorwidget.data.repository.DollarRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
	

	@Provides
	@Singleton
	fun provideMoshi(): Moshi =
		Moshi.Builder()
			.add(KotlinJsonAdapterFactory())
			.build()
	

	@Provides
	@Singleton
	fun provideRetrofit(moshi: Moshi): Retrofit =
		Retrofit.Builder()
			.baseUrl("https://ve.dolarapi.com/") // 👈 tu endpoint real
			.addConverterFactory(MoshiConverterFactory.create(moshi))
			.build()
	

	@Provides
	@Singleton
	fun provideDollarApiService(retrofit: Retrofit): DollarApiService =
		retrofit.create(DollarApiService::class.java)
	

	@Provides
	@Singleton
	fun provideDollarDataStore(
		@ApplicationContext context: Context
	): DollarDataStore = DollarDataStore(context)
	

	@Provides
	@Singleton
	fun provideDollarRepository(
		api: DollarApiService,
		dataStore: DollarDataStore
	): DollarRepository = DollarRepositoryImpl(api, dataStore)
	
	@Provides
	@Singleton
	fun provideThemeDataStore(@ApplicationContext context: Context): ThemeDataStore {
		return ThemeDataStore(context)
	}
	
}