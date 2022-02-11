package com.example.currency_converter_mvvm.di

import com.example.currency_converter_mvvm.BuildConfig
import com.example.currency_converter_mvvm.helper.utility.EndPoints
import com.example.currency_converter_mvvm.network.ApiDataSource
import com.example.currency_converter_mvvm.network.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    //API Base Url
    @Provides
    fun providesBaseUrl() = EndPoints.BASE_URL

    //Gson for converting JSON String to Java Objects
    @Provides
    fun providesGson(): Gson = GsonBuilder().setLenient().create()

    //Retrofit for networking
    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(EndPoints.BASE_URL)
        .client(
            OkHttpClient.Builder().also { client ->
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)
                    client.connectTimeout(120, TimeUnit.SECONDS)
                    client.readTimeout(120, TimeUnit.SECONDS)
                    client.protocols(Collections.singletonList(Protocol.HTTP_1_1))
                }
            }.build()
        )
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    //Api Service with retrofit instance
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    //Class helper with apiService Interface
    @Provides
    @Singleton
    fun provideApiDataSource(apiService: ApiService) = ApiDataSource(apiService)
}