package com.example.monitorwidget.infraestructure.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.monitorwidget.data.local.AppDatabase
import com.example.monitorwidget.data.local.FavoriteAmountDao
import com.example.monitorwidget.data.preferences.ThemeDataStore
import com.example.monitorwidget.data.remote.DollarApiService
import com.example.monitorwidget.data.remote.HexaRateApiService
import com.example.monitorwidget.data.remote.local.datastore.DollarDataStore
import com.example.monitorwidget.domain.repository.DollarRepository
import com.example.monitorwidget.data.repository.DollarRepositoryImpl
import com.example.monitorwidget.data.repository.FavoriteAmountRepositoryImpl
import com.example.monitorwidget.domain.repository.FavoriteAmountRepository
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
	
	@Provides @Singleton
	fun provideMoshi(): Moshi =
		Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
	
	@Provides @Singleton
	fun provideDollarRetrofit(moshi: Moshi): Retrofit =
		Retrofit.Builder()
			.baseUrl("https://ve.dolarapi.com/")
			.addConverterFactory(MoshiConverterFactory.create(moshi))
			.build()
	
	@Provides @Singleton
	fun provideDollarApiService(retrofit: Retrofit): DollarApiService =
		retrofit.create(DollarApiService::class.java)
	
	@Provides @Singleton
	fun provideHexaRateApiService(moshi: Moshi): HexaRateApiService =
		Retrofit.Builder()
			.baseUrl("https://hexarate.paikama.co/api/rates/")
			.addConverterFactory(MoshiConverterFactory.create(moshi))
			.build()
			.create(HexaRateApiService::class.java)
	
	@Provides @Singleton
	fun provideDollarDataStore(@ApplicationContext context: Context): DollarDataStore =
		DollarDataStore(context)
	
	@Provides @Singleton
	fun provideDollarRepository(
		api: DollarApiService,
		hexaApi: HexaRateApiService,
		dataStore: DollarDataStore,
	): DollarRepository = DollarRepositoryImpl(api, hexaApi, dataStore)
	
	@Provides @Singleton
	fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
		Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
			.addMigrations(object : Migration(1, 2) {
				override fun migrate(db: SupportSQLiteDatabase) {
					db.execSQL("ALTER TABLE favorite_amounts ADD COLUMN currency TEXT NOT NULL DEFAULT 'USD'")
				}
			})
			.build()
	
	@Provides
	fun provideFavoriteAmountDao(db: AppDatabase): FavoriteAmountDao = db.favoriteAmountDao()
	
	@Provides @Singleton
	fun provideFavoriteAmountRepository(dao: FavoriteAmountDao): FavoriteAmountRepository =
		FavoriteAmountRepositoryImpl(dao)
	
	@Provides @Singleton
	fun provideThemeDataStore(@ApplicationContext context: Context): ThemeDataStore =
		ThemeDataStore(context)
}
