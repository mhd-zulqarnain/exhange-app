package com.application.core.di


import com.application.core.SealedClassTypeAdapter
import com.application.core.di.qualifier.ApiGateway
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.jvm.internal.Reflection

/**
 * Dagger module to provide core data functionality.
 */

@Module
@InstallIn(SingletonComponent::class)
class CoreDataModule {

    @Provides
    @Singleton
    @ApiGateway
    fun okHttpClientApiGateway(
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        return builder.build()
    }


    @Provides
    @Singleton
    fun gson(): Gson = GsonBuilder().registerTypeAdapterFactory(
        object : TypeAdapterFactory {
            override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
                val kclass = Reflection.getOrCreateKotlinClass(type.rawType)
                return if (kclass.sealedSubclasses.any()) {
                    SealedClassTypeAdapter(kclass, gson)
                } else
                    gson.getDelegateAdapter(this, type)
            }

        }).setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()

    @Provides
    @Singleton
    fun gsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)
}