package com.application.core.di

import com.application.core.api.BASE_URL
import com.application.core.api.ExchangeApi
import com.application.core.di.qualifier.ApiGateway
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreNetworkModule {

    @Singleton
    @ApiGateway
    @Provides
    fun retrofitGateway(
        @ApiGateway client: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit =
        Retrofit.Builder()
            .client(client)
            .addConverterFactory(gsonConverterFactory)
            .baseUrl(BASE_URL).build()

    @Singleton
    @Provides
    fun exchangeApi(@ApiGateway retrofit: Retrofit): ExchangeApi =
        retrofit.create(ExchangeApi::class.java)


}